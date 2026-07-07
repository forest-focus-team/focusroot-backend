# [Week 5][A] Code Review + Release v1.0.0-forest — BẢN VIẾT LẠI (REVISED)

> **Issue gốc:** #43 · **Milestone:** 🌱 Phase 1 — Forest 1.0 (Tuần 2–5) · **Nhánh:** `feature/A-release-week5`
> **Người chuẩn bị:** Lead A · **Ngày:** 2026-07-07
> Bản này **thay thế** phần mô tả của issue #43 (bản gốc chỉ có 3 gạch đầu dòng, thiếu tiêu chí đo được, bỏ sót nợ kỹ thuật chặn release). Xem mục cuối *"Đề xuất chỉnh sửa issue"*.

---

## 1. Mục tiêu (giữ tinh thần bản gốc)

Đóng Phase 1 và phát hành **Forest 1.0 — Core MVP**:
1. Review & xử lý toàn bộ PR/nhánh Tuần 5 (merge cái an toàn, defer cái làm đỏ CI — kèm lý do).
2. Đưa `develop → main` qua PR (main hiện gần trống — chỉ README/LICENSE).
3. Gắn tag annotated **`v1.0.0-forest`** trên `main` + tạo GitHub Release.
4. Cập nhật tài liệu dự án.

Điều kiện tiên quyết chưa nêu trong bản gốc: **release chỉ hợp lệ khi CI xanh và các nợ kỹ thuật chặn release đã xử lý** (chi tiết mục 3).

---

## 2. Bối cảnh & phát hiện khi review (evidence)

| Hạng mục | Thực trạng phát hiện |
|---|---|
| `main` | Chỉ có `Initial commit` (`c427382`: `.gitignore` + `LICENSE` + `README.md`). Chưa có code thật. |
| `develop` | Đã merge tới PR #59/#60 (nhóm D). Build baseline: **BUILD SUCCESS, 41 tests, 0 failures**. |
| 2 session controller | `SessionController` `/sessions` (đúng, `ApiResponse<T>`, có trồng cây) **vs** `FocusSessionController` `/api/sessions` → **double-prefix `/api/api/sessions`** (context-path=/api), trả DTO trần, **không** trồng cây. |
| DTO trùng | 9 lớp trùng/mồ côi ở 2–3 package (`SessionResponse`, `CreateGroupRequest`, `JoinGroupRequest`, `GroupResponse`, `StartSessionRequest`, `EndSessionRequest`). |
| Bảo mật | `User.passwordHash` **không** `@JsonIgnore`; controller trả entity `FocusSession` (có quan hệ `User`, open-in-view) → nguy cơ lộ `password_hash` ra JSON. |
| Code FE Expo | Repo backend còn **template Expo mặc định** (25 file: `app/`, `components/`, `assets/`, `app.json`, `package.json`, `tsconfig.json`). Không coupling build Maven. |

---

## 3. Checklist bổ sung so với bản gốc

### 3.1 Quyết định số phận từng nhánh Tuần 5 chưa merge

**Tiêu chí quyết định:** *merge* nếu (a) merge sạch hoặc conflict tầm thường **và** (b) compile + `mvn test` xanh **và** (c) không phá API/model hiện tại. Ngược lại → *defer* kèm lý do. Không ép merge nhánh làm đỏ CI.

| Nhánh | merge-base | Kết quả test-merge (thực nghiệm) | Quyết định | Lý do |
|---|---|---|---|---|
| `feature/B-integration-test-week5` | init | `rc=0` sạch, chỉ thêm `docs/test-report-week5.md` | ✅ **MERGE** | Docs-only, không đụng code/CI. |
| `feature/B-session-swagger-week5` | init | `rc=1`, add/add conflict 3 file + `OpenApiConfig` trùng path | ⛔ **DEFER** | Tham chiếu class **không tồn tại** trên develop: `UserDetailsImpl`, `Session`, `SessionStatus`, `setTargetDuration`, `findByUserIdAndStatus`. Trùng bean `OpenApiConfig` (`common/` vs `common/config/`). Đổi mapping thành `/api/v1/sessions` nhưng **vẫn double-prefix**. → chắc chắn **đỏ CI**. |
| `feature/C-myforest-shop-week5` | `fbc5f8d` | `rc=1`, conflict `data.sql` | ⛔ **DEFER** | Là **tập con** của C-forest-timeline (cùng commit `6a6c8a0`). |
| `feature/C-forest-timeline-week5` | `fbc5f8d` | `rc=1`, conflict `ForestService` + `data.sql` | ⛔ **DEFER** | Dựng trên **model điểm/coin cũ** đã bị refactor ở Tuần 5: dùng `User.getTotalCoins()/getTotalPoints()`, `TreeSpecies.getBuyCostCoins()/getCostPoints()`, `MyForest.setStatus(String)` — model hiện tại chỉ có `User.coin`, `TreeSpecies.coinCost`, `MyForest.isAlive`. → **không compile** khi merge. |

> **Việc tiếp theo cho nhánh defer:** mở issue Tuần 6 *"Re-implement Tree Shop + Forest Timeline trên model coin"* — port lại logic `buyTree` (trừ coin an toàn, chống âm) và timeline sort theo `plantedAt`, viết lại test theo entity hiện tại. Swagger annotations của B có thể cherry-pick riêng lên `SessionController` chuẩn.

### 3.2 Dọn nợ kỹ thuật chặn release
- [x] **Hợp nhất 2 session controller** → chọn `SessionController` làm chuẩn *(vì sao: xem mục 4)*; gỡ `FocusSessionController`/`Service`/`Test`/`Repository`.
- [x] **Gộp 2 repository** `FocusSession` về một `SessionRepository` (thêm `countByUser_Id`, `countByUser_IdAndStatus`, `sumCoinEarnedByUserId`); `StatsService` dùng chung.
- [x] **Gộp DTO trùng**: giữ `session/StartSessionRequest`, `group/CreateGroupRequest`, `group/GroupResponse`; xoá 9 lớp mồ côi.
- [x] **Bảo mật**: `@JsonIgnore` cho `User.passwordHash`.

### 3.3 Quyết định code FE Expo trong repo backend
- [x] **GỠ**. Là template Expo mặc định (không phải FE thật — FE thật ở `focusroot-frontend`, nhánh `feature/E-jwt-refresh-demo-week5`). Không coupling build. Đặt ở **commit riêng** để Lead có thể loại nếu muốn giữ monorepo.

### 3.4 CI xanh trên cả develop và main
- [ ] PR `feature/A-release-week5 → develop`: chờ Backend CI xanh trước khi merge.
- [ ] PR `develop → main`: chờ Backend CI xanh trước khi merge.
> Workflow `.github/workflows/backend-test.yml` chỉ chạy khi push/PR vào `develop`/`main` (không chạy trên nhánh feature) → phải xác nhận qua 2 PR trên.

### 3.5 Cập nhật PROJECT_MEMORY.md
- [x] Cập nhật `D:\Project\PROJECT_MEMORY.md` (mức tổng): controller chuẩn, DTO giữ lại, nhánh đã merge/defer, tag.

---

## 4. Quyết định kiến trúc: chọn `SessionController` làm chuẩn (vì sao)

| Tiêu chí | `SessionController` `/sessions` ✅ chọn | `FocusSessionController` `/api/sessions` ❌ bỏ |
|---|---|---|
| URL thực tế | `/api/sessions/*` (đúng) | `/api/api/sessions/*` (**double-prefix**) |
| Response | `ApiResponse<T>` — **nhất quán** với Auth/Forest/Stats/Group & hợp đồng FE (`response.data.data`) | DTO trần, **không** bọc → lệch shape toàn API |
| Tính năng lõi | **Có trồng cây MyForest** khi phiên thành công (vòng lặp lõi của app) | **Không** trồng cây → mất tính năng flagship |
| Ownership | Có kiểm tra chủ sở hữu phiên khi end | — |
| Điểm yếu | Trả entity (đã chặn leak bằng `@JsonIgnore`); chưa phân trang history | Có phân trang + DTO sạch |

**Kết luận:** với bản phát hành "Forest 1.0", **nhất quán API + giữ vòng lặp trồng cây** quan trọng hơn phân trang/DTO sạch. Các điểm hay của bản kia (phân trang, DTO response) đưa vào backlog Tuần 6, không chặn release.

---

## 5. Release checklist
- [x] Bump version `pom.xml`: `0.0.1-SNAPSHOT` → `1.0.0-forest`.
- [x] `CHANGELOG.md` (Added/Changed/Removed/Deferred/Notes).
- [x] Soạn nội dung PR `develop → main` (`docs/PR-body-to-main.md`) + PR gom `→ develop` (`docs/PR-body-to-develop.md`).
- [ ] Tag **annotated** `v1.0.0-forest` trên `main` (lệnh sẵn ở `docs/PR-and-tag-commands.md`) — **Lead A bấm**.
- [ ] GitHub Release notes (`docs/RELEASE-NOTES-v1.0.0-forest.md`).

---

## 6. Definition of Done — đo được (mỗi mục có lệnh/URL/artifact)

| # | Tiêu chí | Cách kiểm chứng | Trạng thái |
|---|---|---|---|
| 1 | Build đóng gói được | `mvn -B package -DskipTests` → `BUILD SUCCESS` | ✅ (log `02-ci-equivalent.log`) |
| 2 | Test xanh (H2) | `mvn -B test -Dspring.profiles.active=test` → `Tests run: 40, Failures: 0, Errors: 0` | ✅ |
| 3 | Không còn double-prefix | `grep -rn '"/api/sessions"' src/main` → 0 kết quả; `grep -rn '@RequestMapping("/sessions")' src/main` → `SessionController` | ✅ |
| 4 | Không còn DTO trùng | `find src/main/java -name 'SessionResponse.java' -o -name 'CreateGroupRequest.java'` chỉ còn bản chuẩn | ✅ |
| 5 | Một repository session | `ls src/main/java/com/focusroot/session/*Repository.java` → chỉ `SessionRepository.java` | ✅ |
| 6 | Không lộ password_hash | `grep -n '@JsonIgnore' src/main/java/com/focusroot/user/User.java` | ✅ |
| 7 | Không còn Expo trong backend | `git ls-files app app.json package.json tsconfig.json` → rỗng | ✅ |
| 8 | CI xanh trên develop | GitHub Actions "Backend CI" trên PR `→ develop` = ✅ | ⏳ chờ PR |
| 9 | CI xanh trên main | GitHub Actions "Backend CI" trên PR `→ main` = ✅ | ⏳ chờ PR |
| 10 | Tag tồn tại | `git tag -l v1.0.0-forest` + `git cat-file -t v1.0.0-forest` = `tag` (annotated) | ⏳ Lead bấm |
| 11 | Release công bố | URL: `https://github.com/forest-focus-team/focusroot-backend/releases/tag/v1.0.0-forest` | ⏳ Lead bấm |
| 12 | Tài liệu cập nhật | `PROJECT_MEMORY.md` + `CHANGELOG.md` + `docs/ISSUE-43-REVISED.md` | ✅ |

---

## 7. Rollback plan (ngắn)
- **Tag sai/lỗi:** `git push origin :refs/tags/v1.0.0-forest && git tag -d v1.0.0-forest`, sửa, tag lại.
- **Phát hiện bug sau khi merge `develop → main`:** `main` có branch protection → **không force-push**. Tạo nhánh revert merge-commit → PR revert vào `main`. Release đánh dấu *pre-release/yanked* trên GitHub.
- **Nhánh feature/A-release-week5 lỗi:** chưa đụng `develop`/`main` nên chỉ cần `git branch -D` + làm lại; hoặc `git reset --hard origin/develop`.

---

## 8. Đề xuất chỉnh sửa issue (khác gì bản gốc & vì sao)

| Bản gốc #43 | Bản revised | Vì sao |
|---|---|---|
| "Merge toàn bộ PR tuần 5" | Review từng nhánh, **merge/defer kèm tiêu chí + bằng chứng test-merge** | "Merge toàn bộ" là bất khả thi: 3/4 nhánh làm đỏ CI (compile fail). Ép merge = vỡ release. |
| Không nhắc nợ kỹ thuật | Thêm mục **dọn nợ chặn release** (double-prefix, DTO trùng, password leak, Expo) | Đây là thứ *thật sự* chặn một bản release dùng được, không chỉ là thao tác git. |
| DoD: "CI xanh, cập nhật PROJECT_CONTEXT.md" | DoD **12 mục đo được** (mỗi mục có lệnh/URL/artifact) | DoD gốc không đo được → không biết khi nào "xong". |
| Không có rollback | Thêm **rollback plan** phù hợp branch-protection | Release phải có đường lùi. |
| `PROJECT_CONTEXT.md` | Chuyển sang `PROJECT_MEMORY.md` (bản tổng, mới hơn) | `PROJECT_CONTEXT.md` dừng ở Tuần 2, đã bị `PROJECT_MEMORY.md` thay thế. |
| Tag `v1.0.0-forest` (ngầm định main sạch) |
| Không phân vai thao tác | Tách rõ: **Claude chuẩn bị** (branch/commit/docs) — **Lead A bấm** (push/PR/merge/tag) | Chỉ Lead có quyền merge `develop`/`main`; tránh vượt quyền. |
