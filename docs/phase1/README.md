# Phase 1 - FocusRoot Use Case Documentation

## 📊 Use Case Diagram Tổng Quát
┌─────────────────────────────────────────┐
             │    FocusRoot Backend System            │
             │                                        │
             │  ┌──────────────────────────────────┐  │
             │  │                                  │  │
             │  │  UC-1: Register & Login          │  │
             │  │  UC-2: Create Focus Session      │  │
New User ────┼──│  UC-3: View Statistics           │  │
(👤)         │  │  UC-4: Configure Blocked Apps    │  │
             │  │  UC-5: Invite Friend & Leaderboard│ │
             │  │                                  │  │
             │  └──────────────────────────────────┘  │
             │                                        │
Registered ──┤  (Tất cả 5 use cases)                  │
User (👤)    │                                        │
             │                                        │
             └─────────────────────────────────────────┘
---

## 👥 Actors

### New User
- Người chưa có tài khoản
- Có thể: Đăng ký, Đăng nhập

### Registered User
- Người đã đăng ký & login
- Có thể: Tạo session, Xem stats, Cấu hình app, Mời bạn

---

## 📋 5 Use Cases

| # | Tên | Mô tả |
|---|-----|-------|
| [UC-1](./UC-1-Register-and-Login.md) | Register & Login | Đăng ký tài khoản & đăng nhập |
| [UC-2](./UC-2-Create-Focus-Session.md) | Create Focus Session | Tạo session tập trung, chặn apps |
| [UC-3](./UC-3-View-Statistics.md) | View Statistics | Xem thống kê & lịch sử |
| [UC-4](./UC-4-Configure-Blocked-Apps.md) | Configure Blocked Apps | Cài đặt apps bị chặn |
| [UC-5](./UC-5-Invite-Friend-and-Leaderboard.md) | Invite Friend & Leaderboard | Mời bạn, xem bảng xếp hạng |

---

## 🎯 Phase 1 Scope

✅ **In Scope:**
- Email/Password authentication
- JWT-based session management
- Focus session tracking & app blocking
- Basic statistics (total time, sessions count, success rate)
- Friend invite system & leaderboard

❌ **Out of Scope:**
- Social features (comments, likes)
- Achievements & badges
- In-app currency system
- Premium/subscription features

---

## 📝 Files

- `usecase-diagram.md` - Sơ đồ diagram chi tiết
- `UC-1-Register-and-Login.md` - Use Case 1
- `UC-2-Create-Focus-Session.md` - Use Case 2
- `UC-3-View-Statistics.md` - Use Case 3
- `UC-4-Configure-Blocked-Apps.md` - Use Case 4
- `UC-5-Invite-Friend-and-Leaderboard.md` - Use Case 5

---

## ✅ Definition of Done

- [x] Sơ đồ Use Case tổng quát
- [x] 5 Use Case đặc tả chi tiết
- [x] Mỗi UC có Main Flow + Alternative Flow

Created: Week 1
Author: [Your Team]
