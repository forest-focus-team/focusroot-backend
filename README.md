# FocusRoot Backend

> Backend API cho ứng dụng **FocusRoot** — một Focus Timer app lấy cảm hứng từ Forest.  
> Đồ án môn **Công nghệ Phần mềm** — Nhóm 5.

---

## Mô tả Project

FocusRoot giúp người dùng tập trung làm việc bằng cách trồng cây ảo. Mỗi phiên tập trung thành công sẽ trồng được một cây trong khu rừng cá nhân. Người dùng có thể tạo nhóm để cùng focus với bạn bè.

**Tính năng chính:**
- Đăng ký / đăng nhập (JWT)
- Bắt đầu / kết thúc phiên focus (5–180 phút)
- Khu rừng cá nhân (xem cây đã trồng)
- Nhóm focus (tạo nhóm, mời bạn, focus cùng nhau)
- Dự đoán tỷ lệ thành công dựa trên lịch sử hoạt động
- Thông báo realtime qua WebSocket (STOMP)

---

## 💰 Mô hình kinh doanh

FocusRoot áp dụng mô hình **Freemium kết hợp Đồng tiền ảo (Coin System)**,
cho phép kiếm doanh thu mà không làm gián đoạn trải nghiệm tập trung của người dùng.

### Các gói dịch vụ

| Gói | Giá | Tính năng |
|-----|-----|-----------|
| 🆓 **Miễn phí** | Mãi mãi | Trồng cây cơ bản, 5 loài cây, rừng cá nhân (tối đa 50 cây), thống kê đơn giản |
| 💎 **Premium** | 25.000đ/tháng | Chế độ nhóm Peer-pressure, bảng dự đoán Drop-out (DRS), 20+ loài cây hiếm, thống kê nâng cao |
| 🌱 **Gói Coin** | Từ 10.000đ | Mua thêm coin để mở khóa cây hiếm và tùy chỉnh giao diện |

### Hệ thống Coin
- **Kiếm coin:** Hoàn thành phiên tập trung → nhận coin tương ứng với thời lượng
- **Tiêu coin:** Mở khóa loài cây hiếm, trang trí rừng cá nhân
- **Donate:** 100 coin = trồng 1 cây thật (hợp tác tổ chức môi trường Việt Nam)

---

## Tech Stack

| Layer | Công nghệ |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.x |
| Security | Spring Security + JWT (jjwt 0.12.x) |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA / Hibernate |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Realtime | Spring WebSocket (STOMP) |
| Build | Maven |
| Container | Docker / Docker Compose |
| CI | GitHub Actions |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- (Tuỳ chọn) IntelliJ IDEA / VS Code

---

## Setup & Chạy

### 1. Clone project

```bash
git clone https://github.com/<your-org>/focusroot-backend.git
cd focusroot-backend
```

### 2. Khởi động MySQL bằng Docker

```bash
docker-compose up -d mysql
```

Truy cập phpMyAdmin tại `http://localhost:8081` (user: root / pass: focusroot123).

### 3. Cấu hình environment (tuỳ chọn)

Tạo file `.env` hoặc set biến môi trường:

```bash
DB_PASSWORD=focusroot123
JWT_SECRET=your-super-secret-key-at-least-32-chars
```

> Mặc định trong `application.yml` đã có giá trị fallback để chạy local.

### 4. Chạy ứng dụng

```bash
mvn spring-boot:run
```

Ứng dụng chạy tại: `http://localhost:8080/api`

### 5. Xem Swagger UI

```
http://localhost:8080/api/swagger-ui.html
```

---

## Cấu trúc thư mục

```
focusroot-backend/
├── src/
│   ├── main/
│   │   ├── java/com/focusroot/
│   │   │   ├── FocusRootApplication.java
│   │   │   ├── auth/          # JWT, Security, Login/Register
│   │   │   ├── user/          # User entity & profile API
│   │   │   ├── session/       # Focus session logic
│   │   │   ├── forest/        # MyForest, TreeSpecies
│   │   │   ├── group/         # FocusGroup, GroupMember, GroupSession
│   │   │   ├── prediction/    # Activity log & success prediction
│   │   │   ├── websocket/     # STOMP WebSocket handlers
│   │   │   └── common/        # ApiResponse, GlobalExceptionHandler
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-test.yml
│   └── test/
├── docker-compose.yml
├── pom.xml
├── ARCHITECTURE.md
└── CONTRIBUTING.md
```

---

## API Documentation

Sau khi chạy, truy cập Swagger UI:  
`http://localhost:8080/api/swagger-ui.html`

Các nhóm API:

| Tag | Endpoint prefix | Mô tả |
|---|---|---|
| Authentication | `/api/auth` | Đăng ký, đăng nhập |
| User | `/api/users` | Xem và sửa hồ sơ |
| Focus Session | `/api/sessions` | Bắt đầu/kết thúc phiên |
| Forest | `/api/forest` | Xem khu rừng, danh sách cây |
| Group | `/api/groups` | Tạo nhóm, tham gia nhóm |
| Prediction | `/api/predictions` | Dự đoán tỷ lệ thành công |

---

## Git Workflow

```
main          ← production-ready
develop       ← integration branch
feature/*     ← new features (branch from develop)
fix/*         ← bug fixes
```

Chi tiết xem [CONTRIBUTING.md](CONTRIBUTING.md).

---

## Thành viên nhóm 5

| Họ tên | GitHub | Phụ trách |
|---|---|---|
| Trần Mạnh Danh | [@manhdanhtran](https://github.com/manhdanhtran) | Lead, DB Architect, Backend Core |
| Lê Viết Bắc | [@bac1234556](https://github.com/bac1234556) | Backend Developer |
| Phạm Quang Hùng | [@PhamHung210](https://github.com/PhamHung210) | Backend + Algorithm |
| Lê Thành Chung | [@chung-lol](https://github.com/chung-lol) | Backend + Realtime |
| Ngô Hữu Điệp | [@ngohuudiep](https://github.com/ngohuudiep) | Frontend + Tester |

---

## License

MIT © 2026 FocusRoot Team
