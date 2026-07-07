# Changelog

Tất cả thay đổi đáng chú ý của **FocusRoot Backend** được ghi tại đây.
Định dạng theo [Keep a Changelog](https://keepachangelog.com/vi/1.0.0/),
dự án tuân theo [Semantic Versioning](https://semver.org/lang/vi/).

## [1.0.0-forest] — 2026-07-07

Bản phát hành chính thức **Forest 1.0 — Core MVP** (kết thúc Phase 1, Tuần 2–5).
Khép lại vòng lặp lõi: Đăng ký/Đăng nhập → Bắt đầu phiên tập trung → Tập trung
thành công thì trồng cây vào khu rừng → Nhận coin → Thống kê.

### Added
- **Auth/JWT**: đăng ký, đăng nhập, refresh token (access 24h, refresh 7 ngày). `POST /api/auth/register|login|refresh`.
- **Focus Session**: bắt đầu/kết thúc/lịch sử phiên tập trung. `POST /api/sessions/start`, `POST /api/sessions/{id}/end`, `GET /api/sessions/history`.
- **MyForest**: xem khu rừng + danh sách loài cây. `GET /api/forest`, `GET /api/forest/species`. Trồng cây tự động khi phiên thành công.
- **Statistics** (Tuần 5, A): tổng hợp + thống kê theo ngày, tính streak. `GET /api/stats/summary`, `GET /api/stats/daily`.
- **Group / WebSocket** (Tuần 3–5, D): tạo/tham gia/liệt kê nhóm, phiên nhóm realtime qua STOMP `/ws`, reconnect + broadcast trạng thái.
- **Prediction**: API dự đoán tỷ lệ thành công (`/api/predictions`).
- **GlobalExceptionHandler** + bao response chuẩn `ApiResponse<T>`.
- **Swagger/OpenAPI**: `GET /api/swagger-ui.html`.
- **Báo cáo kiểm thử tích hợp Tuần 5** (`docs/test-report-week5.md`, từ nhánh `feature/B-integration-test-week5`).

### Changed / Refactored (dọn nợ kỹ thuật chặn release — issue #43)
- **Hợp nhất module Focus Session về một controller chuẩn duy nhất**: giữ
  `SessionController` (`/sessions` → thực tế `/api/sessions` nhờ `context-path=/api`),
  bọc `ApiResponse<T>` nhất quán toàn bộ API, và tích hợp trồng cây khi phiên thành công.
- **Gộp hai repository FocusSession** (`SessionRepository` + `FocusSessionRepository`)
  về một `SessionRepository` duy nhất; `StatsService` dùng chung.
- Gắn `@JsonIgnore` cho `User.passwordHash` để không lộ hash mật khẩu khi
  serialize entity ra JSON.

### Removed
- **`FocusSessionController`** map `/api/sessions` bị **double-prefix** thành
  `/api/api/sessions` (do quên `context-path=/api`) — cùng `FocusSessionService`,
  `FocusSessionServiceTest`, `FocusSessionRepository`.
- **DTO trùng lặp/mồ côi** (0 tham chiếu): `SessionResponse` (×2),
  `StartSessionRequest`/`EndSessionRequest` (bản `dto/request/session`),
  `CreateGroupRequest` (×2 bản thừa), `JoinGroupRequest` (×2), `GroupResponse` (bản `dto/response`).
- **Template Expo mặc định** lẫn trong repo backend (`app/`, `components/`,
  `assets/`, `constants/Colors.ts`, `app.json`, `package.json`, `tsconfig.json`) —
  FE thật nằm ở repo `focusroot-frontend`.

### Deferred (chưa merge vào bản phát hành này — xem `docs/ISSUE-43-REVISED.md`)
- `feature/B-session-swagger-week5`: biến thể `FocusSessionController` bổ sung
  Swagger nhưng dựa trên model không tồn tại trên `develop` (`UserDetailsImpl`,
  `Session`, `SessionStatus`), trùng bean `OpenApiConfig`, và vẫn double-prefix
  (`/api/v1/sessions`) → **làm đỏ CI**, defer.
- `feature/C-forest-timeline-week5` & `feature/C-myforest-shop-week5`: tính năng
  Shop/Timeline dựa trên model điểm/coin **cũ** (`getTotalCoins`, `getTotalPoints`,
  `getBuyCostCoins`, `getCostPoints`, `MyForest.setStatus(String)`) đã bị refactor
  ở Tuần 5 → conflict + không compile với model coin hiện tại → defer sang Tuần 6.

### Notes
Bản phát hành thực đầu tiên là
tag **annotated `v1.0.0-forest`** trên `main` sau khi merge `develop → main`.
