# 🌲 FocusRoot Backend — v1.0.2-forest (patch)

**Ngày phát hành:** 2026-07-08
**Nhánh nguồn:** `feature/A-release-v1.0.2-week6` → `develop` → `main`
**Loại:** PATCH — release engineering & hardening. **Không đổi API contract.**

## Summary

Bản vá tập trung nâng **chất lượng phát hành** sau `v1.0.1-forest`: tài liệu chính
xác (thông tin nhóm, link CHANGELOG), cấu hình an toàn hơn (JWT secret **fail-fast**,
`.env.example`), **artifact JAR runnable** + `Dockerfile` đính kèm, và mở rộng test
hồi quy. Không thêm/đổi endpoint, không sửa logic nghiệp vụ.

> **Vì sao PATCH mà không MINOR?** Chỉ gồm docs + chore + reliability/security
> hardening; không có tính năng/endpoint mới, không đổi contract, không bugfix
> nghiệp vụ mới (bug LazyInit đã vá ở `1.0.1-forest`). Theo SemVer đây đúng là
> bump PATCH. Giữ hậu tố `-forest` cho nhất quán với `v1.0.0-forest`/`v1.0.1-forest`.

## New Features / Changed

- **Build artifact thật**: đính kèm `focusroot-backend-1.0.2-forest.jar` (fat JAR
  runnable, `java -jar`) vào GitHub Release — không còn chỉ có source zip do GitHub tự sinh.
- **Docker hoá**: thêm `Dockerfile` multi-stage (build `maven:3.9-eclipse-temurin-17`
  → runtime `eclipse-temurin:17-jre-alpine`, chạy user non-root) + `.dockerignore`;
  thêm service `app` vào `docker-compose.yml` để chạy cả stack bằng `docker-compose up --build`.
- **Cấu hình an toàn hơn**: `app.jwt.secret` bỏ giá trị mặc định đoán được →
  `${JWT_SECRET}` (bắt buộc, fail-fast). Thêm `.env.example` (liệt kê đủ biến, JWT_SECRET
  ≥ 32 ký tự) và ignore `.env`.
- **Tài liệu**: README bảng "Thành viên nhóm 5" dùng tên thật + GitHub username (bỏ
  placeholder giả); hướng dẫn build JAR & Docker trong `CONTRIBUTING.md`; gỡ thuộc tính
  `version` lỗi thời trong `docker-compose.yml`.

## Bug Fixes

- **Link CHANGELOG hỏng trong release notes**: đường dẫn tương đối `../CHANGELOG.md`
  bị GitHub rewrite thành `.../blob/CHANGELOG.md` (thiếu tên branch) → 404/redirect sai.
  Đổi sang URL tuyệt đối `.../blob/main/CHANGELOG.md`.
  *(Không có bugfix nghiệp vụ mới trong bản này — bug 500 LazyInit đã vá ở `v1.0.1-forest`.)*

## Installation / Upgrade

**Yêu cầu:** Java 17+, MySQL 8 (hoặc Docker). **Bắt buộc** set `JWT_SECRET` (≥ 32 ký tự).

### Cách 1 — chạy JAR trực tiếp (asset của release này)
```bash
# 1. DB
docker-compose up -d mysql                 # MySQL :3306 + phpMyAdmin :8081
# 2. Secret (bắt buộc)
export JWT_SECRET="$(openssl rand -base64 48)"
export DB_PASSWORD=focusroot123            # tuỳ chọn, mặc định đã là focusroot123
# 3. Tải JAR từ Assets của release rồi chạy
java -jar focusroot-backend-1.0.2-forest.jar
# -> API: http://localhost:8080/api   ·   Swagger: /api/swagger-ui.html
```

### Cách 2 — cả stack bằng Docker Compose
```bash
cp .env.example .env      # điền JWT_SECRET
docker-compose up --build # app + MySQL + phpMyAdmin
```

**Nâng cấp từ `v1.0.1-forest`:** không có thay đổi schema/DB, không breaking change.
Chỉ cần **đảm bảo biến `JWT_SECRET` đã được set** (bản này đã bỏ secret mặc định —
nếu trước đây bạn dựa vào default thì app sẽ fail-fast cho tới khi set biến).

## Testing Evidence

- **Unit + integration:** `mvn -B test -Dspring.profiles.active=test` →
  **43 tests, 0 failures** (H2 in-memory). Tăng từ 42 (thêm 1 case).
- **Guard hồi quy serialize LAZY:** `EntitySerializationIntegrationTest`
  (`@SpringBootTest` + MockMvc, không `@Transactional`) — **3 case**:
  `POST /sessions/{id}/end`, `GET /forest` (list), `GET /sessions/history` (list);
  mỗi case assert 200 + quan hệ LAZY lồng nhau serialize được + `passwordHash` không lộ.
- **Build:** `mvn -B clean package -DskipTests` → BUILD SUCCESS → fat JAR runnable.
- **Docker:** `docker build` từ `Dockerfile` multi-stage → image chạy được (JRE 17 alpine).
- **CI (GitHub Actions "Backend CI", Java 17 + MySQL 8):** ✅ **success** — PR #70
  (`feature/A-release-v1.0.2-week6` → `develop`),
  [run #28926496212](https://github.com/forest-focus-team/focusroot-backend/actions/runs/28926496212).
  CI trên `develop → main` cũng phải xanh trước khi merge & tag.

## Known Issues

Liệt kê trung thực các giới hạn/nợ kỹ thuật hiện tại (không chặn phát hành):

- **OSIV còn bật (`open-in-view: true`)**: `SessionController`/`ForestController` trả
  thẳng entity JPA + `@JsonIgnoreProperties` trên `User`/`TreeSpecies`/`FocusSession`/`MyForest`.
  Đây là cách vá thực dụng ở `v1.0.1-forest`. **Backlog Phase 2:** chuyển sang **DTO phẳng**
  để tắt lại OSIV (ổn định contract, tránh N+1 tiềm ẩn).
- **`DB_PASSWORD` còn giá trị mặc định local** `focusroot123` (khớp docker-compose) để
  onboarding nhanh — **bắt buộc override** khi triển khai thật (đã ghi chú trong `.env.example`).
- **Swagger annotation chưa đầy đủ** trên `SessionController` chuẩn (bản bổ sung của B bị
  defer ở #43 vì đỏ CI) — cherry-pick ở Phase 2.
- **Chưa có endpoint** `GET /groups/{id}/members`; **Tree Shop / Forest Timeline** bị defer
  (model coin cũ) — Phase 2.
- Không có thay đổi ở phía Frontend trong bản này (repo `focusroot-frontend` riêng; tab Nhóm
  còn mock).

## Contributors

| Họ tên | GitHub | Vai trò |
|---|---|---|
| Trần Mạnh Danh | [@manhdanhtran](https://github.com/manhdanhtran) | Lead, DB Architect, Backend Core, Release |
| Lê Viết Bắc | [@bac1234556](https://github.com/bac1234556) | Backend Developer |
| Phạm Quang Hùng | [@PhamHung210](https://github.com/PhamHung210) | Backend + Algorithm |
| Lê Thành Chung | [@chung-lol](https://github.com/chung-lol) | Backend + Realtime |
| Ngô Hữu Điệp | [@ngohuudiep](https://github.com/ngohuudiep) | Frontend + Tester |

**Changelog đầy đủ:** [`CHANGELOG.md`](https://github.com/forest-focus-team/focusroot-backend/blob/main/CHANGELOG.md)
