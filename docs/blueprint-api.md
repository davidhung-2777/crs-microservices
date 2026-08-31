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

---

### 4. API Gateway (Port 8080)

Mọi request qua Gateway với định tuyến rewrite path /api → /

---

**Version:** v1.0  
**Last Updated:** Aug 31, 2026