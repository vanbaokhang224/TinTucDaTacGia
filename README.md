# 📰 TinTucTacGia - News API Backend

A RESTful API backend for a news platform built with Spring Boot, featuring role-based access control for Admin, Author, and User roles.

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Programming Language |
| Spring Boot | 3.x | Backend Framework |
| Spring Security | 6.x | Authentication & Authorization |
| JWT (jjwt) | 0.11.x | Token-based Authentication |
| Spring Data JPA | 3.x | Database ORM |
| MySQL | 8.x | Database |
| Lombok | 1.18.x | Boilerplate Reduction |
| Maven | 3.x | Build Tool |

---

## 📁 Project Structure

```
src/main/java/org/example/tintuctacgia/
├── controller/         # API endpoints
├── service/            # Business logic
├── repository/         # Database queries
├── entity/             # Database models
├── dto/                # Data Transfer Objects
├── mapper/             # Entity ↔ DTO converters
├── security/           # JWT Filter & Security Config
├── exception/          # Custom exceptions & Global handler
└── enums/              # Role enum
```

---

## ⚙️ Setup & Installation

### Prerequisites
- Java 17+
- MySQL 8+
- Maven 3+

### 1. Clone the repository
```bash
git clone https://github.com/vanbaokhang224/TinTucDaTacGia.git
cd tintuctacgia
```

### 2. Create MySQL database
```sql
CREATE DATABASE tintuctacgia;
```

### 3. Configure application.properties
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tintuctacgia
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=your_secret_key_at_least_32_characters_long
jwt.expiration=3600000
```

### 4. Run the application
```bash
mvn spring-boot:run
```

Server starts at: `http://localhost:8080`

---

## 👥 Roles & Permissions

| Action | ADMIN | AUTHOR | USER |
|---|---|---|---|
| Register / Login | ✅ | ✅ | ✅ |
| View all posts | ✅ | ✅ | ✅ |
| Create post | ✅ | ✅ | ❌ |
| Edit own post | ✅ | ✅ | ❌ |
| Edit others' post | ✅ | ❌ | ❌ |
| Delete post | ✅ | ❌ | ❌ |
| Create comment | ✅ | ✅ | ✅ |
| Edit own comment | ✅ | ✅ | ✅ |
| Delete own comment | ✅ | ❌ | ✅ |
| Delete any comment | ✅ | ❌ | ❌ |
| View all users | ✅ | ❌ | ❌ |
| Delete user | ✅ | ❌ | ❌ |

---

## 📮 API Endpoints

### 🔐 Authentication

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | ❌ | Register new account |
| POST | `/api/auth/login` | ❌ | Login & get token |
| POST | `/api/auth/logout` | ✅ | Logout & blacklist token |
| POST | `/api/auth/refresh-token` | ✅ | Get new token |
| GET | `/api/auth/users` | ✅ ADMIN | Get all users |
| GET | `/api/auth/users/{id}` | ✅ | Get user by ID |
| PUT | `/api/auth/update/{id}` | ✅ | Update user info |
| DELETE | `/api/auth/delete/{id}` | ✅ ADMIN | Delete user |

### 📝 Posts

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/posts` | ❌ | Get all posts (paginated) |
| GET | `/api/posts/{id}` | ❌ | Get post by ID |
| GET | `/api/posts/search?keyword=abc` | ❌ | Search posts by title |
| GET | `/api/posts/by-category/{category}` | ❌ | Filter by category |
| GET | `/api/posts/by-author/{userId}` | ❌ | Get posts by author |
| GET | `/api/posts/my-posts` | ✅ AUTHOR | Get my own posts |
| POST | `/api/posts` | ✅ AUTHOR/ADMIN | Create new post |
| PUT | `/api/posts/{id}` | ✅ AUTHOR/ADMIN | Update post |
| DELETE | `/api/posts/{id}` | ✅ ADMIN | Delete post |

### 💬 Comments

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/comments/post/{postId}` | ✅ | Get comments by post |
| GET | `/api/comments` | ✅ | Get all comments |
| POST | `/api/comments/{postId}` | ✅ | Create comment |
| PUT | `/api/comments/{id}` | ✅ | Update own comment |
| DELETE | `/api/comments/{id}` | ✅ | Delete comment |

---

## 🧪 Testing with Postman

### Step 1: Register accounts
```json
POST /api/auth/register

// Register ADMIN
{
    "name": "Admin",
    "email": "admin@gmail.com",
    "password": "123456",
    "dateOfBirth": "2000-01-01",
    "role": "ADMIN"
}

// Register AUTHOR
{
    "name": "Author1",
    "email": "author1@gmail.com",
    "password": "123456",
    "dateOfBirth": "2000-01-01",
    "role": "AUTHOR"
}

// Register USER
{
    "name": "Reader1",
    "email": "reader1@gmail.com",
    "password": "123456",
    "dateOfBirth": "2000-01-01",
    "role": "USER"
}
```

### Step 2: Login & get token
```json
POST /api/auth/login
{
    "name": "Author1",
    "email": "author1@gmail.com",
    "password": "123456"
}

// Response:
{
    "message": "Đăng nhập thành công",
    "token": "eyJhbGci...",
    "name": "Author1",
    "email": "author1@gmail.com",
    "role": "AUTHOR"
}
```

### Step 3: Add token to requests
In Postman, go to **Authorization** tab → select **Bearer Token** → paste token.

### Step 4: Create a post (as AUTHOR)
```json
POST /api/posts
Authorization: Bearer <token>

{
    "title": "My First Article",
    "content": "This is the content of my first article.",
    "category": "Technology"
}
```

### Step 5: Test role permissions
```
// USER tries to create post → 403 Forbidden
// AUTHOR tries to edit another author's post → 403 Forbidden
// AUTHOR tries to delete a post → 403 Forbidden
// ADMIN can do everything → 200 OK
```

---

## 🔒 Security Features

- **JWT Authentication** — Stateless token-based auth
- **Token Blacklist** — Invalidate tokens on logout
- **Token Refresh** — Renew token before expiration (1 hour)
- **Password Encryption** — BCrypt hashing
- **Role-based Access Control** — 3 roles: ADMIN, AUTHOR, USER
- **Input Validation** — Request body validation with error messages
- **Global Exception Handler** — Consistent error responses

---

## 📋 Response Format

### Success
```json
{
    "id": 1,
    "title": "Article Title",
    "content": "Article content...",
    "category": "Technology",
    "authorName": "Author1",
    "authorEmail": "author1@gmail.com",
    "createdAt": "2026-05-01T10:00:00",
    "updatedAt": "2026-05-01T10:00:00"
}
```

### Error
```json
{
    "message": "Bạn không có quyền thực hiện hành động này"
}
```

### Validation Error
```json
{
    "title": "Tiêu đề không được để trống",
    "content": "Nội dung phải ít nhất 10 ký tự"
}
```

---

## 👨‍💻 Author

**Văn Bảo Khang** — Student at HUTECH University  
Major: Software Engineering  
Internship Project — 2026
