# Lệnh chốt release v1.0.0-forest (dành cho Lead A bấm tay)

> Toàn bộ code đã chuẩn bị trên nhánh `feature/A-release-week5` (off `develop`).
> **Claude KHÔNG tự push / merge / tag.** Lead A chạy các lệnh dưới đây.

Nội dung nhánh (5 commit, +103 / −933 dòng, 46 file):

```
ec154b9 release: bump version 1.0.0-forest + CHANGELOG #43
a3e7ff2 Merge feature/B-integration-test-week5: báo cáo kiểm thử tích hợp tuần 5 #43
b97b195 chore: gỡ code Expo template lẫn trong repo backend #43
7f2ac6e refactor: hợp nhất session module + gộp DTO trùng lặp (chặn release) #43
425cbbb Create test-report-week5.md   (từ nhánh B-integration)
```

---

## Bước 1 — Push nhánh release
```bash
cd focusroot-backend
git push -u origin feature/A-release-week5
```

## Bước 2 — PR #1: feature/A-release-week5 → develop
```bash
gh pr create --repo forest-focus-team/focusroot-backend \
  --base develop --head feature/A-release-week5 \
  --title "[Week 5][A] Release prep: hợp nhất session + dọn DTO + v1.0.0-forest #43" \
  --body-file docs/PR-body-to-develop.md
```
→ Chờ **Backend CI xanh** trên PR (workflow chạy khi PR vào `develop`). Rồi merge.

## Bước 3 — PR #2: develop → main
```bash
gh pr create --repo forest-focus-team/focusroot-backend \
  --base main --head develop \
  --title "Release v1.0.0-forest: Forest 1.0 (Core MVP) develop → main #43" \
  --body-file docs/PR-body-to-main.md
```
→ Chờ **Backend CI xanh** trên PR (workflow chạy khi PR vào `main`). Rồi merge.

## Bước 4 — Tag annotated v1.0.0-forest trên main (sau khi PR #2 đã merge)
```bash
git checkout main
git pull origin main
git tag -a v1.0.0-forest -m "FocusRoot Backend v1.0.0-forest — Forest 1.0 (Core MVP)

Vòng lặp lõi: Auth → Focus Session → trồng cây MyForest → coin → Statistics.
Chi tiết: docs/RELEASE-NOTES-v1.0.0-forest.md & CHANGELOG.md.
Lưu ý: tag v1.0.0 cũ là init-tag rác (trỏ Initial commit)."
git push origin v1.0.0-forest
```

## Bước 5 — GitHub Release
```bash
gh release create v1.0.0-forest --repo forest-focus-team/focusroot-backend \
  --title "🌱 v1.0.0-forest — Forest 1.0 (Core MVP)" \
  --notes-file docs/RELEASE-NOTES-v1.0.0-forest.md \
  --target main
```

---

## (Tuỳ chọn) Dọn tag rác `v1.0.0`
Tag `v1.0.0` cũ trỏ `Initial commit` gần-trống. Nếu muốn xoá cho gọn:
```bash
git tag -d v1.0.0
git push origin :refs/tags/v1.0.0
```
> Cân nhắc: nếu đã có ai clone/tham chiếu tag này thì giữ lại cũng vô hại.

## Rollback nhanh (nếu phát hiện lỗi sau khi tag)
```bash
# Gỡ tag đã push
git push origin :refs/tags/v1.0.0-forest
git tag -d v1.0.0-forest
# main vẫn ở commit trước release; nếu đã merge develop→main và cần lùi:
#   tạo PR revert merge-commit thay vì force-push (main có branch protection).
gh pr create --repo forest-focus-team/focusroot-backend --base main --head <revert-branch> \
  --title "Revert release v1.0.0-forest" --body "Lý do: ..."
```
