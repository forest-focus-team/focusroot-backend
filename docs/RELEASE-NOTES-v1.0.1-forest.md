# 🌿 FocusRoot Backend — v1.0.1-forest (patch)

Bản vá sau **v1.0.0-forest**, gỡ lỗi chặn demo end-to-end. Không đổi API contract.

## 🐛 Fixed
- **500 → 200** ở `POST /api/sessions/{id}/end` và `GET /api/forest`.
  Nguyên nhân: controller trả entity JPA có quan hệ **LAZY**, transaction đóng trước
  khi Jackson serialize (`spring.jpa.open-in-view: false`) → `LazyInitializationException`.
  Cách sửa: `open-in-view: true` + `@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})`
  trên `User`/`TreeSpecies`/`FocusSession`/`MyForest`. Không thêm dependency.
  `passwordHash` vẫn được `@JsonIgnore` (không lộ dù serialize cả graph). (PR #63)

## 📝 Changed
- Sửa mô tả tag `v1.0.0`: **pre-release cũ hơn** của Member B (được giữ lại), không
  phải "tag rác". (PR #64)

## ✅ Kiểm chứng
- GitHub Actions "Backend CI" trên `develop` (sau merge #63/#64): **success** (`b06fc5b`).
- Local: `mvn -B test -Dspring.profiles.active=test` → 42 tests, 0 failures; `package -DskipTests` → BUILD SUCCESS.
- Verify HTTP thật với BE local: chuỗi register→login→start→**end**→**forest**→stats đều 2xx; `passwordHash` không lộ.

## 🔁 Backlog
- Cân nhắc trả **DTO phẳng** cho Session/Forest để tắt lại OSIV (bài bản hơn) — Tuần 6.

**Changelog đầy đủ:** [`CHANGELOG.md`](../CHANGELOG.md)
