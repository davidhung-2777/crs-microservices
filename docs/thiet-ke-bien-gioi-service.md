# Thiết kế Biên Giới & Kiến Trúc Hệ Thống CRS Microservices

## 1. Tổng Quan 4 Thành Phần Hệ Thống

| Service | Cổng | Database | Trách Nhiệm Chính |
|---------|------|----------|-------------------|
| **API Gateway** | 8080 | ❌ Không có | Điểm vào duy nhất, định tuyến, xác thực sơ bộ (kiểm tra JWT), CORS, rate limiting |
| **Auth Service** | 8081 | `auth_db` | Quản lý User (username/password BCrypt), Student profile, đăng nhập, sinh & xác thực JWT |
| **Course Service** | 8082 | `course_db` | Quản lý Course (môn học), tìm kiếm full-text, phân trang, quản lý số chỗ (soChoConLai) |
| **Registration Service** | 8083 | `registration_db` | Quản lý Registration (đăng ký môn học), gọi sang course-service để giữ/hủy chỗ |

---

## 2. Nguyên Tắc Data Ownership

### 🔐 Không Có Cross-Service Direct DB Access
- **Course Service** sở hữu & quản lý duy nhất bảng `course` trong `course_db`
- **Registration Service** sở hữu & quản lý duy nhất bảng `registration` trong `registration_db`
- **Auth Service** sở hữu & quản lý duy nhất bảng `app_user` và `student` trong `auth_db`

### 🔄 Gọi Dữ Liệu Của Nhau Thông Qua REST API
Ví dụ: Registration Service muốn kiểm tra số chỗ còn lại của một môn học:
- ❌ **KHÔNG** kết nối trực tiếp `course_db` để query
- ✅ **CÓ** gọi `GET http://localhost:8082/internal/courses/{id}` (nếu là nội bộ) hoặc thông qua Gateway

---

## 3. Bảng Định Tuyến API Gateway (Dự Kiến)

### Routes & Tính Năng

| Route Prefix | Đích (Backend) | Port | Xác Thực | Ghi Chú |
|--------------|----------------|------|---------|---------|
| `/api/auth/**` | Auth Service | 8081 | ❌ Public | Login để lấy token JWT |
| `/api/courses/**` | Course Service | 8082 | 🔒 JWT (tuỳ chọn) | GET public, POST/PUT/DELETE cần ADMIN role |
| `/api/registrations/**` | Registration Service | 8083 | 🔒 JWT bắt buộc | Cần Student role hoặc ADMIN |
| `/api/public/courses` | Course Service | 8082 | 🔑 API Key | Cho đối tác ngoài (header `X-API-KEY`) |
| `/internal/courses/**` | ❌ KHÔNG route qua Gateway | — | — | Chỉ gọi nội bộ từ Registration Service |

---

## 4. Kiểu Xác Thực

### AuthHeaderFilter (Global Filter ở Gateway)
- **Chặn 401** nếu thiếu header `Authorization: Bearer <JWT>`
- **Ngoại lệ (permit all)**:
  - `POST /api/auth/login` — public
  - `GET /api/courses/**` — public
  - `GET /api/public/courses` — public (nhưng bắt `X-API-KEY`)

### ApiKeyFilter (Global Filter ở Gateway)
- **Kiểm tra** header `X-API-KEY` cho route `/api/public/courses`
- **Từ chối 403** nếu sai hoặc thiếu key

### Zero Trust ở từng Service
- Course Service & Registration Service tự verify JWT từ header `Authorization`
- **Không tin tưởng** Gateway đã lọc rồi, tự thực hiện kiểm tra thêm

---

## 5. Lưu Đồ Luồng Đăng Ký Môn Học (Registration Flow)

```
1. [CLIENT] -> POST /api/registrations (kèm JWT token của student)
                    |
                    v
2. [API GATEWAY] -> AuthHeaderFilter kiểm tra token
                    |
                    v -> Route đến Registration Service (8083)
3. [REGISTRATION SERVICE]
   a. SecurityConfig verify JWT độc lập
   b. RegistrationService.register()
      - Kiểm tra đã đăng ký môn này chưa (unique constraint)
      - Gọi CourseClient.reserveSeat(courseId) -> [COURSE SERVICE]
   c. [COURSE SERVICE] 
      - Verify JWT độc lập
      - Kiểm tra soChoConLai > 0
      - Giảm soChoConLai đi 1, return 200
   d. [REGISTRATION SERVICE] nhận 200
      - Lưu Registration record với trangThai = "DA_DANG_KY"
      - Return 201 + location header
                    |
                    v
4. [CLIENT] nhận 201 + response JSON
```

---

## 6. Format Lỗi JSON (Nhất Quán Toàn Hệ Thống)

```json
{
  "message": "Không tìm thấy môn học với ID: 999"
}
```

Mọi lỗi (404, 400, 409, 401, 403, 500) đều trả format này thông qua `GlobalExceptionHandler` + `@RestControllerAdvice`.

**HTTP Status Codes:**
- `200 OK` — Thành công (GET, PUT)
- `201 Created` — Tạo mới thành công (POST)
- `204 No Content` — Xoá thành công (DELETE)
- `400 Bad Request` — Dữ liệu không hợp lệ (validation errors)
- `401 Unauthorized` — Thiếu hoặc sai token JWT
- `403 Forbidden` — Không đủ quyền (role không phù hợp hoặc sai API key)
- `404 Not Found` — Resource không tồn tại
- `409 Conflict` — Trùng dữ liệu (ví dụ: trùng tên môn học) hoặc trạng thái không hợp lệ (hết chỗ)
- `500 Internal Server Error` — Lỗi server (không lộ chi tiết)

---

## 7. Công Nghệ Stack

- **Framework:** Spring Boot 3.x
- **Language:** Java 17 / 21
- **Build Tool:** Maven
- **Database:** MySQL 8.0+
- **Authentication:** JWT (JJWT library)
- **API Gateway:** Spring Cloud Gateway
- **ORM:** Spring Data JPA
- **Validation:** Spring Validation (Bean Validation)
- **Security:** Spring Security

---

## 8. Giới Hạn & Ghi Chú

### Giao Dịch Phân Tán (Distributed Transactions)
Khi Registration Service gọi Course Service để đặt chỗ:
- **Nếu** `courseClient.reserveSeat()` thành công ✅
- **Nhưng** `registrationRepository.save()` thất bại ❌
- → Dữ liệu 2 service sẽ **lệch**: course có giảm chỗ, nhưng registration không được lưu
- **Giải pháp toàn bộ:** Implement Saga Pattern hoặc Outbox Pattern (ngoài scope dự án này)
- **Hiện tại:** Accept risk, ghi chú trong code, có thể thêm compensating transaction sau

---

## 9. Các File Cần Tạo / Thay Đổi Trong Buổi 1

- ✅ `docs/thiet-ke-bien-gioi-service.md` ← File này
- ✅ `docs/blueprint-api.md` ← Danh sách endpoint (buổi 1)
- ✅ `course-service/` ← Spring Boot project khởi tạo
- ✅ Database `course_db` tạo sẵn
- ✅ Entity `Course` + Controller mock
- ✅ Test GET /courses qua Postman

---

**Phiên bản:** v1.0 | **Ngày:** Aug 31, 2026
