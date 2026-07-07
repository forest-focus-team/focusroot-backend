## 🌱 Release v1.0.0-forest — Forest 1.0 (Core MVP)

Đưa toàn bộ `develop` (đã gồm dọn nợ kỹ thuật + docs Tuần 5 + version bump) lên `main`
để phát hành chính thức. Liên quan: #43.

Đây là bước đưa **code thật** lên `main` (trước đây `main` gần như trống — chỉ
README/LICENSE). Sau khi merge PR này, tag annotated **`v1.0.0-forest`** trên `main`.

## Nội dung phát hành
Vòng lặp lõi kiểu Forest: **Auth (JWT) → Focus Session → trồng cây MyForest khi
thành công → coin → Statistics**, kèm Group/WebSocket realtime và Swagger UI.

Xem đầy đủ: `docs/RELEASE-NOTES-v1.0.0-forest.md` và `CHANGELOG.md`.

## Điều kiện phát hành (DoD)
- [x] `mvn -B package -DskipTests` → BUILD SUCCESS
- [x] `mvn -B test -Dspring.profiles.active=test` → 40 tests, 0 failures
- [ ] **Backend CI xanh trên PR này** (workflow chạy khi PR vào `main`)
- [ ] Sau merge: tag annotated `v1.0.0-forest` + GitHub Release (xem `docs/PR-and-tag-commands.md`)

## Ghi chú
- Tag `v1.0.0` cũ là **init-tag rác** (lightweight, trỏ `Initial commit`). Bản thật là `v1.0.0-forest`.
- Nhánh Tuần 5 defer (B-swagger, C-timeline, C-shop) → xử lý ở Tuần 6, xem `docs/ISSUE-43-REVISED.md`.
