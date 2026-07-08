# FocusRoot — Project Context (Bộ nhớ dự án)

> File này dùng để Claude Code đọc vào đầu mỗi session làm việc.
> Cập nhật cuối mỗi tuần bởi Member A (Nhóm trưởng).
> ⚠️ Phần dưới (Tuần 1–2) là bản cũ. Bản tổng mới nhất ở `D:\Project\PROJECT_MEMORY.md`.

---

## 🔖 CẬP NHẬT v1.0.1-forest (08/07/2026)

**Backend đã phát hành `v1.0.1-forest`** (patch, tag annotated trên `main`) — bản vá sau `v1.0.0-forest`.

### 🐛 Fix LazyInitializationException (chặn demo)
- `POST /api/sessions/{id}/end` và `GET /api/forest` trước đây trả **HTTP 500**: controller
  trả thẳng entity JPA có quan hệ **LAZY** (`user`, `treeSpecies`, `focusSession`), transaction
  đóng trước khi Jackson serialize (`spring.jpa.open-in-view: false`) → `LazyInitializationException`.
- **Sửa:** bật `open-in-view: true` **và** `@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})`
  trên `User`/`TreeSpecies`/`FocusSession`/`MyForest` (không thêm dependency). `passwordHash` vẫn
  `@JsonIgnore` (không lộ dù serialize cả graph). Thêm `EntitySerializationIntegrationTest`
  (`@SpringBootTest`+MockMvc) canh hồi quy. (PR #63)
- Verify HTTP thật: chuỗi register→login→start→**end**→**forest**→stats đều **2xx**.
- Backlog Phase 2: cân nhắc trả **DTO phẳng** để tắt lại OSIV.

### 🏷️ Đính chính tag `v1.0.0`
- `v1.0.0` **KHÔNG phải "tag rác"**. Đó là **pre-release cũ hơn** do Member B tạo (có asset JAR +
  release notes coursework). Git tag của nó trỏ `Initial commit` do `main` lúc đó chưa có code,
  nhưng **được giữ lại** làm phiên bản cũ, cùng tồn tại với `v1.0.0-forest`/`v1.0.1-forest`
  (latest stable). **Không xoá.** (PR #64)

### 📱 Trạng thái FE demo (xuyên repo)
- FE nối API thật đã xong (Member D) — PR #7 (repo frontend):
  https://github.com/forest-focus-team/focusroot-frontend/pull/7 → đã merge vào nhánh E →
  lên `develop` FE (PR #8) → đang chờ release FE `v1.0.0-forest` (PR #9 develop→main).
- Đã verify: `tsc --noEmit` exit 0, `expo export --platform web` exit 0, luồng API 2xx với BE.

---

## 🏗️ Tech Stack
- Backend: Spring Boot 3.2.x, Java 17, Maven
- ORM: Spring Data JPA + Hibernate
- Security: Spring Security + JWT (jjwt 0.12.5)
- Database: MySQL 8.0
- Realtime: Spring WebSocket + STOMP
- API Docs: Springdoc OpenAPI (Swagger UI)
- Frontend: React Native + Expo (TypeScript)
- CI/CD: GitHub Actions

## 👥 Thành viên
- Member A: Trần Mạnh Danh — GitHub: kekhongten1 — Lead, DB Architect, Backend Core
- Member B: Lê Viết Bắc — GitHub: bac1234556 — Backend Developer
- Member C: Phạm Quang Hùng — GitHub: PhamHung210 — Backend + Algorithm
- Member D: Lê Thành Chung — GitHub: chung-lol — Backend + Realtime
- Member E: Ngô Hữu Điệp — GitHub: ngohuudiep — Frontend + Tester

## 📁 Repositories
- Backend: https://github.com/forest-focus-team/focusroot-backend
- Mobile: (chưa tạo - Member E phụ trách)
- Docs: (chưa tạo)

## 🌿 Git Workflow
- main: production, chỉ merge từ develop qua PR
- develop: nhánh tích hợp chính
- feature/<member>-<task>: nhánh làm việc
- Commit format: <type>: <mô tả> #<issue_number>
- Chỉ Member A có quyền merge PR vào develop/main

## 📅 Timeline tổng quan
- Phase 1 — Forest 1.0: Tuần 1–4
- Phase 2 — Forest 2.0: Tuần 5–7

## ✅ TUẦN 1 (29/05 – 04/06/2026) — COMPLETED
### Đã hoàn thành (Member A):
- [x] Spring Boot skeleton: 52 files, 2267 dòng
- [x] 8 JPA Entities đầy đủ
- [x] 7 feature packages (auth/user/session/forest/group/prediction/websocket)
- [x] docker-compose.yml (MySQL 8 + phpMyAdmin port 8081)
- [x] GitHub Actions CI (backend-test.yml)
- [x] README.md + ARCHITECTURE.md + CONTRIBUTING.md
- [x] PR #6 merged vào develop
- [x] 5 Issues Tuần 1 tạo và assign
- [x] week-01-report.md

### Đã hoàn thành (các member):
- [x] Member B: Research Forest app (#2) — CLOSED 01/06 — docs/chapter2_survey.md (bảng so sánh 4 app, phân tích UI/UX Forest, 5 tài liệu tham khảo)
- [x] Member C: Viết SRS (#3) — CLOSED 31/05 — docs/SRS.md (15+ FR Phase 1, 8+ FR Phase 2, NFR, Actor list)
- [x] Member D: Vẽ Use Case (#4) — CLOSED 01/06 — usecase-diagram.md + usecase-diagram.puml (2 actors, 5 UC, mỗi UC có main flow + alt flows)

### Đã hoàn thành toàn bộ — Tuần 1 DONE ✅
- [x] Member E: Setup Expo + Wireframe (#5) — CLOSED — Expo TS + Router + App chạy Expo Go + Wireframe 8 màn hình

### Cấu trúc DB hiện tại (8 entities):
User, TreeSpecies, FocusSession, MyForest, 
FocusGroup, GroupMember, GroupSession, UserActivityLog

### Lưu ý kỹ thuật:
- application.yml: ddl-auto=create (đổi thành validate sau khi có schema.sql)
- CI dùng H2 in-memory (application-test.yml)
- Branch protection: cả main lẫn develop cần PR

---

## 🔄 TUẦN 2 (05/06 – 11/06/2026) — IN PROGRESS
### Lưu ý kỹ thuật đầu tuần:
- develop: circular dependency đã được fix, CI xanh ✅

### Mục tiêu:
- [ ] Member A: Thiết kế ERD chuẩn 3NF + schema.sql + triggers.sql
- [ ] Member B: API Spec đầy đủ + Entity classes
- [ ] Member C: Class Diagram + Sequence Diagram
- [ ] Member D: Use Case Phase 2 + Activity Diagram
- [ ] Member E: Mockup UI Figma chuẩn mobile

---

## 🚫 TUẦN 3–7 — PLANNED
(Xem Master Plan đầy đủ tại docs/weekly-reports/)

---

## 🗓️ GitHub Milestones & Workflow

### Cấu trúc 2 Phase Milestones
Mỗi issue được gán vào 1 trong 2 Phase Milestone (để track % tiến độ):

| Milestone | # | Due | Progress | Nội dung |
|-----------|---|-----|----------|----------|
| 🌱 Phase 1 — Forest 1.0 (Tuần 1-4) | #8 | 2026-06-26 | 5/5 closed (100%) | Core MVP: Auth, Focus Session, My Forest, Statistics |
| 🌳 Phase 2 — Forest 2.0 (Tuần 5-7) | #9 | 2026-07-17 | 0/0 | Advanced: Drop-out Prediction, Peer-pressure Group, Testing, Báo cáo |

### Phân bổ tuần theo label
Mỗi issue dùng label week-x để biết thuộc tuần nào:

| Label | Tuần | Nội dung |
|-------|------|----------|
| week-1 | Tuần 1 | Khởi động & Đặc tả yêu cầu (Issues #1-5) |
| week-2 | Tuần 2 | Thiết kế hệ thống & CSDL |
| week-3 | Tuần 3 | Cài đặt Backend Core |
| week-4 | Tuần 4 | Hoàn thiện Forest 1.0 + Demo |
| week-5 | Tuần 5 | Tính năng cạnh tranh |
| week-6 | Tuần 6 | Tích hợp & Kiểm thử |
| week-7 | Tuần 7 | Báo cáo & Bảo vệ |

### Workflow mỗi tuần
1. Lead tạo Issues mới → gán đúng Phase Milestone + label week-x + label phase-x
2. Thành viên làm xong mỗi checklist → comment báo cáo ngắn vào issue
3. Thành viên push lên feature branch cá nhân
4. Lead review + AI optimize → tạo PR merge vào develop
5. Khi issue hoàn tất → Close issue → milestone % tự cập nhật

### Issues tuần 1 — dữ liệu thực từ GitHub (cập nhật 03/06/2026)
- #1 [A] Init Spring Boot project skeleton & GitHub setup ✅ CLOSED 29/05 — labels: week-1, setup, backend, phase-1
- #2 [B] Research Forest app & competitors analysis ✅ CLOSED 01/06 — labels: documentation, week-1, phase-1
- #3 [C] Write Software Requirements Specification (SRS) ✅ CLOSED 31/05 — labels: documentation, week-1, phase-1
- #4 [D] Draw Use Case diagrams & write Use Case specs ✅ CLOSED 01/06 — labels: documentation, week-1, phase-1
- #5 [E] Setup React Native + Expo project & wireframes ✅ CLOSED — Expo TS + Router + 8 màn hình chạy Expo Go — branch: feature/E-init-expo-app

---

## 📝 Hướng dẫn dùng file này
Khi bắt đầu session mới với Claude Code, gõ:
"Đọc file PROJECT_CONTEXT.md rồi tiếp tục dự án FocusRoot"
Claude Code sẽ load đủ context và làm việc tiếp mà không cần giải thích lại.
