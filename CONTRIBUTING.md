# Contributing — FocusRoot Backend

## Git Branch Naming

```
main          ← luôn ổn định, deploy được
develop       ← branch tích hợp, merge từ feature/*
feature/<tên> ← tính năng mới, branch từ develop
fix/<tên>     ← bug fix, branch từ develop (hoặc main nếu hotfix)
docs/<tên>    ← chỉ sửa documentation
```

**Ví dụ:**

```bash
git checkout develop
git checkout -b feature/session-timer
git checkout -b fix/jwt-expiry-bug
git checkout -b docs/api-readme
```

---

## Commit Message Format

Tuân theo [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <mô tả ngắn>

[body tùy chọn — giải thích WHY]

[footer tùy chọn — issue/PR reference]
```

**Type:**

| Type | Dùng khi |
|---|---|
| `feat` | Thêm tính năng mới |
| `fix` | Sửa bug |
| `refactor` | Refactor code (không thêm/sửa feature) |
| `test` | Thêm / sửa test |
| `docs` | Sửa tài liệu |
| `chore` | Cập nhật config, dependencies |
| `style` | Format code (không đổi logic) |

**Ví dụ commit:**

```
feat(auth): add JWT refresh token endpoint

fix(session): prevent duplicate IN_PROGRESS sessions per user

test(group): add unit tests for GroupService.joinGroup

docs(readme): update setup instructions for Docker
```

---

## Pull Request Checklist

Trước khi tạo PR, tự kiểm tra:

- [ ] Branch tạo từ `develop` (không phải `main`)
- [ ] Tên branch đúng convention (`feature/`, `fix/`, ...)
- [ ] Code compile không lỗi: `mvn compile`
- [ ] Test pass: `mvn test -Dspring.profiles.active=test`
- [ ] Không có hardcode credential / secret trong code
- [ ] API mới đã có Swagger annotation (`@Operation`, `@Tag`)
- [ ] Entity mới đã có đủ JPA annotation
- [ ] Không commit file `.env`, `application-local.yml`, hay IDE config (`.idea/`, `*.iml`)
- [ ] PR description giải thích rõ thay đổi và lý do

**Tạo PR:**

1. Push branch lên remote: `git push origin feature/<tên>`
2. Tạo Pull Request vào `develop` trên GitHub
3. Request review từ ít nhất 1 thành viên khác
4. Chỉ merge sau khi CI pass và có approval

---

## Code Style

- **Java:** Theo Google Java Style Guide (IntelliJ formatter)
- **Không commit code có warning** chưa được xử lý
- Dùng **Lombok** để giảm boilerplate (không tự viết getter/setter)
- **Package-by-feature:** class của feature nào để trong package đó
- Test file đặt cùng cấu trúc package với source, suffix `Tests`

---

## Setup môi trường dev

```bash
# 1. Cấu hình biến môi trường (JWT_SECRET bắt buộc, >=32 ký tự)
cp .env.example .env          # rồi điền JWT_SECRET
export $(grep -v '^#' .env | xargs)

# 2. Start DB
docker-compose up -d mysql

# 3. Chạy ứng dụng
mvn spring-boot:run

# 4. Chạy test (dùng H2 in-memory, không cần MySQL / JWT_SECRET)
mvn test -Dspring.profiles.active=test
```

---

## Build artifact (JAR) & Docker

### Build fat JAR (runnable)

```bash
mvn -B clean package -DskipTests      # -> target/focusroot-backend-<version>.jar
java -jar target/focusroot-backend-*.jar   # cần JWT_SECRET + MySQL đang chạy
```

JAR sinh ra là **fat/executable JAR** (Spring Boot repackage) — chạy trực tiếp bằng
`java -jar`, không cần cài Maven. Đây là artifact được đính kèm vào GitHub Release.

### Build & chạy bằng Docker (multi-stage)

`Dockerfile` dùng multi-stage: build bằng `maven:3.9-eclipse-temurin-17`, runtime
bằng `eclipse-temurin:17-jre-alpine` (user non-root).

```bash
# Chỉ build image
docker build -t focusroot-backend:latest .

# Chạy image (cần MySQL sẵn + JWT_SECRET)
docker run --rm -p 8080:8080 -e JWT_SECRET="$(openssl rand -base64 48)" focusroot-backend:latest
```

### Chạy cả stack (app + MySQL + phpMyAdmin) bằng docker-compose

```bash
cp .env.example .env          # điền JWT_SECRET
docker-compose up --build     # build image `app` + khởi động MySQL + phpMyAdmin
```

> App container tự trỏ DB sang service `mysql` qua `SPRING_DATASOURCE_URL`.
> Thiếu `JWT_SECRET`, container `app` fail-fast (lệnh `up -d mysql` không bị ảnh hưởng).
