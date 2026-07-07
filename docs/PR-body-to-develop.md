## Mục tiêu
Chuẩn bị release **v1.0.0-forest**: dọn nợ kỹ thuật chặn release, gộp nhánh docs
Tuần 5 an toàn, bump version. Đây là PR gom về `develop` trước khi mở PR `develop → main`.

Liên quan: #43. Chi tiết review & quyết định: `docs/ISSUE-43-REVISED.md`.

## Thay đổi

### ♻️ Hợp nhất module Focus Session (chọn 1 controller chuẩn)
- **Giữ** `SessionController` (`/sessions` → thực tế `/api/sessions` nhờ `context-path=/api`):
  bọc `ApiResponse<T>` nhất quán toàn API + tích hợp trồng cây MyForest khi phiên thành công.
- **Gỡ** `FocusSessionController` (map `/api/sessions` → **double-prefix** `/api/api/sessions`),
  `FocusSessionService`, `FocusSessionServiceTest`, `FocusSessionRepository`.
- Gộp 3 method thống kê vào `SessionRepository`; `StatsService` dùng chung → còn **một** repository cho `FocusSession`.

### 🧹 Gộp DTO trùng lặp (xoá 9 lớp mồ côi, 0 tham chiếu)
`SessionResponse` (×2), `StartSessionRequest`/`EndSessionRequest` (`dto/request/session`),
`CreateGroupRequest` (×2 thừa), `JoinGroupRequest` (×2), `GroupResponse` (`dto/response`).
Bản chuẩn: `session/StartSessionRequest`, `group/CreateGroupRequest`, `group/GroupResponse`.

### 🔒 Bảo mật
`@JsonIgnore` cho `User.passwordHash` — tránh lộ hash mật khẩu khi serialize entity ra JSON.

### 🗑️ Repo hygiene
Gỡ template Expo mặc định lẫn trong repo backend (`app/`, `components/`, `assets/`,
`constants/Colors.ts`, `app.json`, `package.json`, `tsconfig.json`). *(Commit riêng, có thể loại nếu muốn giữ monorepo.)*

### 📄 Docs
Merge `feature/B-integration-test-week5` → `docs/test-report-week5.md`.
Thêm `CHANGELOG.md`, `docs/RELEASE-NOTES-v1.0.0-forest.md`, `docs/ISSUE-43-REVISED.md`.

### 🔖 Version
`pom.xml`: `0.0.1-SNAPSHOT` → `1.0.0-forest`.

## Nhánh Tuần 5 hoãn (defer — không ép merge vì làm đỏ CI)
- `feature/B-session-swagger-week5`: dùng `UserDetailsImpl`/`Session`/`SessionStatus` không tồn tại + trùng bean `OpenApiConfig` + vẫn double-prefix.
- `feature/C-forest-timeline-week5`, `feature/C-myforest-shop-week5`: Shop/Timeline trên model điểm/coin cũ, không compile với model coin hiện tại.

## Kiểm chứng
```
mvn -B package -DskipTests            → BUILD SUCCESS
mvn -B test -Dspring.profiles.active=test → Tests run: 40, Failures: 0, Errors: 0
```
*(chạy local JDK 21; CI dùng JDK 17 temurin — bytecode target 17 qua `maven.compiler.release`.)*

## Checklist
- [x] Chọn & hợp nhất 1 session controller chuẩn
- [x] Xoá DTO trùng, sửa import
- [x] Quyết định code FE Expo trong repo backend
- [x] `mvn test` xanh
- [ ] Backend CI xanh trên PR này (chờ chạy)
