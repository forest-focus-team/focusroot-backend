# 🌱 FocusRoot Backend — v1.0.0-forest

**Ngày phát hành:** 2026-07-07
**Milestone:** Phase 1 — Forest 1.0 (Core MVP, Tuần 2–5)
**Nhánh nguồn:** `develop` → `main`

Bản phát hành chính thức đầu tiên của FocusRoot Backend. Hoàn thiện vòng lặp lõi
kiểu Forest: **tập trung thành công → trồng cây ảo → nhận coin → thống kê tiến bộ**.

## ✨ Tính năng chính
- **Xác thực JWT**: đăng ký, đăng nhập, refresh token (`/api/auth/*`).
- **Phiên tập trung (Focus Session)**: bắt đầu / kết thúc / lịch sử (`/api/sessions/*`), tự động trồng cây khi hoàn thành.
- **Khu rừng (MyForest)**: xem rừng + loài cây (`/api/forest`, `/api/forest/species`).
- **Thống kê**: tổng hợp, theo ngày, streak (`/api/stats/summary`, `/api/stats/daily`).
- **Nhóm + Realtime**: tạo/tham gia nhóm, phiên nhóm qua WebSocket STOMP (`/ws`).
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`.

> ⚠️ Mọi endpoint có prefix `/api` do `server.servlet.context-path=/api`.

## 🧹 Dọn nợ kỹ thuật (issue #43)
- Hợp nhất **một** controller session chuẩn (`SessionController` `/api/sessions`, bọc `ApiResponse<T>`); gỡ bản `FocusSessionController` bị double-prefix `/api/api/sessions`.
- Gộp **một** repository (`SessionRepository`) cho `FocusSession`.
- Gỡ 9 lớp DTO trùng lặp/mồ côi.
- `@JsonIgnore` cho `User.passwordHash` (không lộ hash ra JSON).
- Gỡ template Expo lẫn trong repo backend.

## ⏭️ Hoãn sang Tuần 6 (defer)
- `feature/B-session-swagger-week5` — dựa trên class không tồn tại (`UserDetailsImpl`), trùng bean, làm đỏ CI.
- `feature/C-forest-timeline-week5`, `feature/C-myforest-shop-week5` — Shop/Timeline dựng trên model điểm/coin cũ, không compile với model coin hiện tại.

Chi tiết & tiêu chí: `docs/ISSUE-43-REVISED.md`.

## ✅ Chất lượng
- `mvn -B package -DskipTests` → BUILD SUCCESS.
- `mvn -B test -Dspring.profiles.active=test` → **40 tests, 0 failures** (H2 in-memory).

## 📝 Ghi chú tag
Bản phát hành ổn định là tag **annotated `v1.0.0-forest`**.
Tag `v1.0.0` là **pre-release cũ hơn** (do Member B tạo) — được giữ lại làm phiên bản cũ, không xoá.

**Changelog đầy đủ:** [`CHANGELOG.md`](https://github.com/forest-focus-team/focusroot-backend/blob/main/CHANGELOG.md)
