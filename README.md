# 📰 TinTucTacGia - Hệ Thống Quản Lý Tòa Soạn Báo (Backend API)

Một hệ thống Backend RESTful API hoàn chỉnh dành cho tòa soạn báo điện tử, được xây dựng bằng **Spring Boot 3**. Dự án mô phỏng quy trình kiểm duyệt nội dung thực tế với hệ thống phân quyền (Role-based) chặt chẽ, tối ưu hóa hiệu năng bằng Caching và lưu trữ ảnh trên Cloud.

---

## 🛠️ Công Nghệ Sử Dụng (Tech Stack)

| Công nghệ | Phiên bản | Chức năng |
|---|---|---|
| Java | 17 | Ngôn ngữ lập trình cốt lõi |
| Spring Boot | 3.x | Framework Backend chính |
| Spring Security | 6.x | Phân quyền & Bảo mật |
| JWT (jjwt) | 0.11.x | Xác thực người dùng (Token-based) |
| Spring Data JPA | 3.x | Tương tác Cơ sở dữ liệu (ORM) |
| MySQL | 8.x | Hệ quản trị Cơ sở dữ liệu |
| Spring Cache | Caching | Tối ưu hóa tốc độ truy vấn API |
| Cloudinary | API | Lưu trữ file ảnh (Images Cloud Storage) |
| Lombok | 1.18.x | Giảm thiểu code thừa (Boilerplate) |
| Maven | 3.x | Quản lý thư viện & Build dự án |

---

## 👥 Hệ Thống Phân Quyền (Roles)

Hệ thống được thiết kế với **4 Role riêng biệt**, mô phỏng chính xác cấu trúc của một tòa soạn thực tế:

1. **READER (Độc giả):** Chỉ có thể xem bài viết đã xuất bản (`PUBLISHED`), tìm kiếm bài viết, bình luận, và thả cảm xúc (Reaction).
2. **AUTHOR (Tác giả/Phóng viên):** Có quyền viết bài (lưu nháp `DRAFT`), đẩy bài lên chờ duyệt (`REVIEW`). Được quyền sửa bài của mình (kể cả bài đã đăng, nhưng khi sửa sẽ bị giáng xuống chờ duyệt lại).
3. **EDITOR (Biên tập viên):** Quản lý nội dung. Có quyền xem danh sách bài chờ duyệt, Duyệt bài (`APPROVE`), Từ chối bài kèm lý do (`REJECT`), Khóa bài/Sửa bài của người khác.
4. **ADMIN (Quản trị viên):** Có toàn quyền (Superuser), bao gồm xóa bài viết vĩnh viễn, quản lý User, xóa User, và mọi quyền của Editor.

---

## 🔄 Quy Trình Kiểm Duyệt Bài Viết (Workflow)

Điểm nhấn của hệ thống là luồng trạng thái bài viết (`PostStatus`):
1. **DRAFT (Bản nháp):** Author tạo bài viết mới. Bài viết chỉ Author đó thấy.
2. **REVIEW (Chờ duyệt):** Author gọi API `Submit for Review`. Bài viết lọt vào danh sách chờ duyệt của Editor.
3. **REJECTED (Bị từ chối):** Editor không đồng ý, trả về kèm lý do. Author có thể sửa lại và Submit lần nữa.
4. **PUBLISHED (Đã xuất bản):** Editor duyệt bài (`Approve`). Bài viết chính thức hiển thị công khai cho Reader.
*(Đặc biệt: Nếu Author sửa một bài đang PUBLISHED, bài viết sẽ tự động quay về trạng thái REVIEW để đảm bảo nội dung không bị sửa bậy bạ sau khi duyệt).*

---

## ⚙️ Hướng Dẫn Cài Đặt (Setup & Installation)

### Yêu cầu hệ thống
- Java 17+
- MySQL 8+
- Maven 3+

### 1. Clone dự án
```bash
git clone https://github.com/vanbaokhang224/TinTucDaTacGia.git
cd tintuctacgia
```

### 2. Tạo Cơ sở dữ liệu MySQL
```sql
CREATE DATABASE blog_db;
```

### 3. Cấu hình ứng dụng (application.yaml)
Cấu hình thông tin DB, JWT Secret và **tài khoản Cloudinary** của bạn trong file `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
    username: root
    password: password_cua_ban

jwt:
  secret: day-la-secret-key-cua-ban-doi-thanh-gia-tri-that
  expiration: 3600000

cloudinary:
  cloud-name: "dien_cloud_name_cua_ban"
  api-key: "dien_api_key_cua_ban"
  api-secret: "dien_api_secret_cua_ban"
```

### 4. Chạy ứng dụng
```bash
mvn spring-boot:run
```
Server sẽ khởi chạy tại: `http://localhost:8080`

---

## 📮 Các API Chính (Endpoints Highlights)

### 🔐 Authentication & Users
| Method | Endpoint | Quyền hạn | Mô tả |
|---|---|---|---|
| POST | `/api/auth/register` | Tất cả | Đăng ký tài khoản mới |
| POST | `/api/auth/login` | Tất cả | Đăng nhập & Lấy JWT Token |
| GET | `/api/auth/users` | ADMIN | Lấy danh sách tất cả Users |
| PUT | `/api/auth/role/{id}` | ADMIN | Đổi Role của User (Ví dụ: Thăng cấp Editor) |

### 📝 Posts (Bài viết)
| Method | Endpoint | Quyền hạn | Mô tả |
|---|---|---|---|
| GET | `/api/posts` | Tất cả | Xem tất cả bài viết PUBLISHED (Có Phân trang & Cache) |
| GET | `/api/posts/{id}` | Tất cả | Xem chi tiết bài viết (Cache) |
| GET | `/api/posts/my-posts` | AUTHOR | Xem danh sách bài viết của chính mình |
| GET | `/api/posts/pending-review` | EDITOR, ADMIN | Xem danh sách bài chờ duyệt |
| POST | `/api/posts` | AUTHOR, ADMIN | Viết bài mới (Lưu DRAFT) |
| PUT | `/api/posts/{id}` | AUTHOR, EDITOR, ADMIN | Sửa bài viết |
| PATCH | `/api/posts/{id}/submit` | AUTHOR | Gửi bài lên chờ duyệt (Thành REVIEW) |
| PATCH | `/api/posts/{id}/approve` | EDITOR, ADMIN | Duyệt bài xuất bản (Thành PUBLISHED) |
| PATCH | `/api/posts/{id}/reject` | EDITOR, ADMIN | Từ chối bài viết (Thành REJECTED) |
| DELETE| `/api/posts/{id}` | EDITOR, ADMIN | Xóa bài viết |

### 🖼️ Files (Upload)
| Method | Endpoint | Quyền hạn | Mô tả |
|---|---|---|---|
| POST | `/api/files/upload` | AUTHOR, EDITOR, ADMIN | Upload ảnh lên Cloudinary và lấy URL |

*(Ngoài ra còn có các module APIs cho Comments, Categories, Tags, Bookmarks, Profile, Statistics...)*

---

## 🔒 Tính Năng Nổi Bật Về Bảo Mật
- **JWT & Role-based Authorization:** Chặn quyền tuyệt đối từ tầng Filter của Spring Security. Phân chia rạch ròi giới hạn của từng Role.
- **Global Exception Handler:** Bắt toàn bộ lỗi (404 Not Found, 403 Forbidden, 400 Validation) và trả về định dạng JSON chuẩn mực thay vì quăng lỗi trắng trang.
- **Xử lý Token Hết Hạn / Blacklist:** Token bị vô hiệu hóa ngay khi người dùng gọi API Đăng xuất (Logout).

---

## 👨‍💻 Thông tin Tác giả

**Văn Bảo Khang** — Sinh viên trường Đại học HUTECH  
Chuyên ngành: Kỹ thuật Phần mềm (Software Engineering)  
Dự án Đồ án Thực tập Backend — 2026
