# FocusRoot — Project Context (Bộ nhớ dự án)

> File này dùng để Claude Code đọc vào đầu mỗi session làm việc.
> Cập nhật cuối mỗi tuần bởi Member A (Nhóm trưởng).
> Bản tổng mới nhất ở `D:\Project\PROJECT_MEMORY.md`.

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
- Member A: Trần Mạnh Danh — GitHub: manhdanhtran — Lead, DB Architect, Backend Core
- Member B: Lê Viết Bắc — GitHub: bac1234556 — Backend Developer
- Member C: Phạm Quang Hùng — GitHub: PhamHung210 — Backend + Algorithm
- Member D: Lê Thành Chung — GitHub: chung-lol — Backend + Realtime
- Member E: Ngô Hữu Điệp — GitHub: ngohuudiep — Frontend + Tester

## 📁 Repositories
- Backend: https://github.com/forest-focus-team/focusroot-backend
- Frontend: https://github.com/forest-focus-team/focusroot-frontend

## 🌿 Git Workflow
- main: production, chỉ merge từ develop qua PR
- develop: nhánh tích hợp chính
- feature/<member>-<task>: nhánh làm việc
- Commit format: <type>: <mô tả> #<issue_number>
- Chỉ Member A có quyền merge PR vào develop/main

## 📅 Timeline tổng quan
- Phase 1 — Forest 1.0: Tuần 1–4
- Phase 2 — Forest 2.0: Tuần 5–7


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


## 📝 Hướng dẫn dùng file này
Khi bắt đầu session mới với Claude Code, gõ:
"Đọc file PROJECT_CONTEXT.md rồi tiếp tục dự án FocusRoot"
Claude Code sẽ load đủ context và làm việc tiếp mà không cần giải thích lại.
