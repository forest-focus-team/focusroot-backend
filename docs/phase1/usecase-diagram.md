# Use Case Diagram - FocusRoot Phase 1

##  Sơ đồ Use Case Tổng Quát
┌─────────────────────────────────────────────────┐
│          FocusRoot Backend System              │
├─────────────────────────────────────────────────┤
│                                                 │
│    UC-1: Register & Login                      │
│    UC-2: Create Focus Session                  │
│    UC-3: View Statistics & History             │
│    UC-4: Configure Blocked Apps                │
│    UC-5: Invite Friend & View Leaderboard      │
│                                                 │
└─────────────────────────────────────────────────┘
▲                              ▲
│                              │
┌───┴────┐                  ┌──────┴───────┐
│ New    │                  │ Registered   │
│ User   │                  │ User         │
└────────┘                  └──────────────┘

## Actors (Các vai trò)

### 1. **New User** (Người dùng mới)
- Người chưa có tài khoản
- Có thể: Đăng ký, Đăng nhập
- Không thể: Tạo session, Xem thống kê

### 2. **Registered User** (Người dùng đã đăng ký)
- Người đã đăng ký & đăng nhập thành công
- Có thể: Tạo session, Xem thống kê, Cấu hình app, Mời bạn
- Phạm vi: Quản lý dữ liệu của chính họ

---

## 5 Use Cases Chính (Phase 1 MVP)

| ID | Tên | Mô tả | Actor |
|----|----|--------|-------|
| **UC-1** | Register & Login | Đăng ký tài khoản & đăng nhập vào hệ thống | New User, Registered User |
| **UC-2** | Create Focus Session | Tạo & bắt đầu session tập trung, chặn apps | Registered User |
| **UC-3** | View Statistics | Xem thống kê tập trung, lịch sử session | Registered User |
| **UC-4** | Configure Blocked Apps | Cài đặt danh sách app cần chặn | Registered User |
| **UC-5** | Invite Friend & Leaderboard | Mời bạn, xem bảng xếp hạng | Registered User |

---

## Mối quan hệ giữa các Use Cases
1️ New User
└─→ UC-1: Register & Login 
└─→ Đăng nhập thành công
└─→ Trở thành Registered User 
2️ Registered User
├─→ UC-2: Create Focus Session 
│   └─→ Chặn apps, bắt đầu timer
│
├─→ UC-3: View Statistics 
│   └─→ Xem tổng thời gian, cây trồng
│
├─→ UC-4: Configure Blocked Apps 
│   └─→ Chọn app cần chặn
│
└─→ UC-5: Invite Friend & Leaderboard 
└─→ Mời bạn, xem BXH

---

## Phạm vi Phase 1 (MVP)

 **Được làm ở Phase 1:**
- Đăng ký & đăng nhập (email/password)
- Tạo session & chặn app
- Xem thống kê cơ bản
- Cấu hình app
- Mời bạn bè

 **Không làm ở Phase 1:**
- Social features (comment, like)
- Achievements & badges
- In-app currency system
- Premium features

---

## Ghi chú kỹ thuật

- **Backend**: Spring Boot REST API
- **Authentication**: JWT Token
- **Database**: Lưu user, session, stats
- **API Response**: JSON format
