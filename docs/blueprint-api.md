# Blueprint API - CRS Microservices

## 📋 Danh Sách Toàn Bộ Endpoint (4 Services)

### 1. Auth Service (Port 8081)

#### Public Endpoints (không cần JWT)

| Method | Endpoint | Description | Request | Response | Status |
|--------|----------|-------------|---------|----------|--------|
| `POST` | `/auth/login` | Đăng nhập, nhận JWT | `LoginRequestDTO` | `LoginResponseDTO` | 200 |

**LoginRequestDTO:**
```json
{
  "username": "student1",
  "password": "student123"
}
```

**LoginResponseDTO:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "student1",
  "role": "STUDENT"
}
```

---

### 2. Course Service (Port 8082)

#### Public Endpoints (không cần JWT, qua Gateway)

| Method | Endpoint | Description | Response | Status |
|--------|----------|-------------|----------|--------|
| `GET` | `/courses` | Danh sách môn học (có phân trang, tìm kiếm) | `Page<CourseDTO>` | 200 |
| `GET` | `/courses/{id}` | Chi tiết 1 môn học | `CourseDTO` | 200 |

**Query Parameters cho GET /courses:**
- `keyword` (optional): tìm kiếm theo tên môn học
- `page` (default: 0): số trang
- `size` (default: 10): số bản ghi/trang
- `sort` (default: id,asc): sắp xếp (ví dụ: `tenMonHoc,asc`)

**Response Page<CourseDTO>:**
```json
{
  "content": [
    {
      "id": 1,
      "tenMonHoc": "Lập Trình Java",
      "soTinChi": 3,
      "soChoToiDa": 30,
      "soChoConLai": 15
    }
  ],
  "page": 0,
  "size": 10,
  "totalPages": 1,
  "totalElements": 1
}
```

#### Admin Endpoints (cần JWT + ADMIN role)

| Method | Endpoint | Description | Request | Response | Status |
|--------|----------|-------------|---------|----------|--------|
| `POST` | `/courses` | Tạo môn học mới | `CourseDTO` | `CourseDTO` | 201 |
| `PUT` | `/courses/{id}` | Cập nhật môn học | `CourseDTO` | `CourseDTO` | 200 |
| `DELETE` | `/courses/{id}` | Xóa môn học | — | — | 204 |

**CourseDTO (dùng cho POST/PUT):**
```json
{
  "tenMonHoc": "Lập Trình Java",
  "soTinChi": 3,
  "soChoToiDa": 30
}
```

#### Internal Endpoints (chỉ gọi từ Registration Service)

| Method | Endpoint | Description | Request | Response | Status |
|--------|----------|-------------|---------|----------|--------|
| `PATCH` | `/internal/courses/{id}/reserve-seat` | Đặt chỗ (giảm soChoConLai) | — | `{"message": "Đặt chỗ thành công"}` | 200 |
| `PATCH` | `/internal/courses/{id}/release-seat` | Hủy chỗ (tăng soChoConLai) | — | `{"message": "Hủy chỗ thành công"}` | 200 |

**Lỗi có thể xảy ra:**
- `404` — Không tìm thấy môn học
- `409` — Hết chỗ (soChoConLai <= 0)
- `400` — Dữ liệu không hợp lệ

**Error Response Format:**
```json
{
  "message": "Không tìm thấy môn học với ID: 999"
}
```

---

### 3. Registration Service (Port 8083)

#### Authenticated Endpoints (cần JWT)

| Method | Endpoint | Description | Request | Response | Status |
|--------|----------|-------------|---------|----------|--------|
| `POST` | `/registrations` | Đăng ký môn học | `RegistrationRequestDTO` | `RegistrationResponseDTO` | 201 |
| `DELETE` | `/registrations/{id}` | Hủy đăng ký | — | — | 204 |
| `GET` | `/registrations/student/{studentId}` | Danh sách đăng ký của sinh viên | — | `List<RegistrationResponseDTO>` | 200 |

**RegistrationRequestDTO:**
```json
{
  "studentId": 1,
  "courseId": 1
}
```

**RegistrationResponseDTO:**
```json
{
  "id": 1,
  "studentId": 1,
  "courseId": 1,
  "trangThai": "DA_DANG_KY",
  "ngayDangKy": "2026-08-31T10:30:00"
}
```

**Lỗi có thể xảy ra:**
- `201` — Đăng ký thành công
- `400` — Dữ liệu không hợp lệ (thiếu studentId/courseId)
- `401` — Không có JWT
- `404` — Không tìm thấy sinh viên hoặc môn học
- `409` — Trùng đăng ký hoặc hết chỗ

**Error Response Format:**
```json
{
  "message": "Sinh viên đã đăng ký môn học này rồi"
}
```

---

### 4. API Gateway (Port 8080)

#### Routes & Tính Năng

| Client Request | Gateway Route | Destination | Auth | API Key | Ghi Chú |
|----------------|---------------|-------------|------|---------|---------|
| `POST /api/auth/login` | `/api/auth/login` → `/auth/login` | Auth Service 8081 | ❌ | ❌ | Public |
| `GET /api/courses` | `/api/courses` → `/courses` | Course Service 8082 | ❌ | ❌ | Public, phân trang |
| `GET /api/courses/{id}` | `/api/courses/{id}` → `/courses/{id}` | Course Service 8082 | ❌ | ❌ | Public |
| `POST /api/courses` | `/api/courses` → `/courses` | Course Service 8082 | ✅ JWT | ❌ | ADMIN only |
| `PUT /api/courses/{id}` | `/api/courses/{id}` → `/courses/{id}` | Course Service 8082 | ✅ JWT | ❌ | ADMIN only |
| `DELETE /api/courses/{id}` | `/api/courses/{id}` → `/courses/{id}` | Course Service 8082 | ✅ JWT | ❌ | ADMIN only |
| `GET /api/public/courses` | `/api/public/courses` → `/courses` | Course Service 8082 | ❌ | ✅ X-API-KEY | External partner |
| `POST /api/registrations` | `/api/registrations` → `/registrations` | Registration Service 8083 | ✅ JWT | ❌ | STUDENT only |
| `DELETE /api/registrations/{id}` | `/api/registrations/{id}` → `/registrations/{id}` | Registration Service 8083 | ✅ JWT | ❌ | STUDENT only |
| `GET /api/registrations/student/{id}` | `/api/registrations/student/{id}` → `/registrations/student/{id}` | Registration Service 8083 | ✅ JWT | ❌ | STUDENT only |

**⚠️ NOT ROUTED THROUGH GATEWAY:**
- `/internal/courses/**` — Chỉ gọi nội bộ từ Registration Service (port 8083 gọi thẳng 8082)

#### Gateway Filters

**AuthHeaderFilter (Order: -1)**
- Kiểm tra header `Authorization: Bearer <JWT>`
- **Permit All (Public):**
  - `POST /api/auth/login`
  - `GET /api/courses/**`
  - `GET /api/public/courses`
- **Require JWT:**
  - Mọi endpoint còn lại
- **Response 401** nếu thiếu/sai token

**ApiKeyFilter (Order: -2)**
- Kiểm tra header `X-API-KEY` cho route `/api/public/courses`
- Expected key: `crs-partner-key-2026`
- **Response 403** nếu sai/thiếu key
- Bypass nếu có JWT hợp lệ

**CORS Filter**
- Allow Origin: `http://localhost:5173` (Frontend)
- Allow Methods: `GET, POST, PUT, DELETE, OPTIONS, PATCH`
- Allow Headers: `Content-Type, Authorization, X-API-KEY`

---

## 📊 HTTP Status Codes Map

| Status | Tình Huống | Ví Dụ Response |
|--------|-----------|----------------|
| **200 OK** | GET thành công, PUT thành công | `{"id": 1, "tenMonHoc": "Java"}` |
| **201 Created** | POST tạo mới thành công | `{"id": 1, "tenMonHoc": "Java"}` |
| **204 No Content** | DELETE thành công | (body rỗng) |
| **400 Bad Request** | Validation error, dữ liệu sai | `{"message": "tenMonHoc không được để trống"}` |
| **401 Unauthorized** | Thiếu/sai JWT | `{"message": "Unauthorized"}` |
| **403 Forbidden** | Sai API key, không đủ role | `{"message": "Forbidden"}` |
| **404 Not Found** | Resource không tồn tại | `{"message": "Không tìm thấy môn học với ID: 999"}` |
| **409 Conflict** | Trùng dữ liệu, hết chỗ, lỗi state | `{"message": "Hết chỗ trong môn học này"}` |
| **500 Internal Server Error** | Lỗi server (không lộ chi tiết) | `{"message": "Lỗi server, vui lòng thử lại"}` |

---

## 🔐 JWT Token Structure

**Header:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload:**
```json
{
  "sub": "student1",
  "role": "STUDENT",
  "iat": 1693478400,
  "exp": 1693564800
}
```

**Secret:** `crs-jwt-secret-key-super-secure-2026` (giống trên tất cả 4 services)

---

## 🗂️ Luồng Dữ Liệu (Data Flow)

### Luồng Đăng Ký Môn Học

```
[CLIENT] (Browser)
    |
    | POST /api/registrations (JWT token)
    |
    v
[API GATEWAY 8080]
    |
    | AuthHeaderFilter verify JWT ✅
    | Rewrite path: /registrations
    |
    v
[REGISTRATION SERVICE 8083]
    |
    | JwtAuthFilter verify JWT again ✅
    | RegistrationService.register()
    | - Check duplicate (DB query)
    | - Call HTTP PATCH /internal/courses/{id}/reserve-seat
    |
    v
[COURSE SERVICE 8082]
    |
    | InternalCourseController.reserveSeat()
    | - Check soChoConLai > 0 (TX)
    | - soChoConLai -= 1
    | - Return 200
    |
    v
[REGISTRATION SERVICE 8083]
    |
    | Save Registration record (DB)
    | Return 201 + response DTO
    |
    v
[CLIENT] nhận 201
```

---

## ⚙️ Configuration Summary

| Service | Port | Database | Timeout | Retry |
|---------|------|----------|---------|-------|
| API Gateway | 8080 | ❌ None | 30s | 0 |
| Auth Service | 8081 | auth_db | — | — |
| Course Service | 8082 | course_db | — | — |
| Registration Service | 8083 | registration_db | 10s (HTTP call) | 1 (exponential backoff) |

---

**Version:** v1.0-BUỔI-1-4  
**Last Updated:** Aug 31, 2026
