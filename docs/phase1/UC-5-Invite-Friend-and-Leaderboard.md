# UC-5: Invite Friend & Leaderboard

## Thông tin
- **Actor**: Registered User
- **Precondition**: User đã login, có ít nhất 1 session
- **Postcondition**: Friend được add hoặc leaderboard được hiển thị

## Main Flow
1. User click "Social" / "Leaderboard" tab
2. System fetch leaderboard từ database (sort by total focus time)
3. System hiển thị top users
4. User click "Invite Friend"
5. System generate invite code
6. System hiển thị code hoặc shareable link
7. User copy code & share (email/chat/QR)
8. Friend nhập code khi sign up
9. System verify code & link 2 users
10. System hiển thị leaderboard giữa bạn bè
11. User có thể view friend's stats

## Alternative Flow
**Alt 1 - Network error (bước 2)**
- System báo "Cannot load leaderboard"
- User click Retry

**Alt 2 - Không có friend nào**
- System hiển thị empty state
- Suggest "Invite your first friend!"

**Alt 3 - Friend nhập code sai (bước 8)**
- System báo "Invalid invite code"
- Friend retry nhập

**Alt 4 - Code expired (bước 8)**
- System báo "Invite code expired"
- Suggest "Ask user to resend new invite"

**Alt 5 - User click detail friend**
- System hiển thị friend's stats
- User có thể compare hoặc back
