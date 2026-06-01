# UC-2: Create Focus Session

## Thông tin
- **Actor**: Registered User
- **Precondition**: User đã login
- **Postcondition**: Session được tạo & bắt đầu

## Main Flow
1. User click "Start Session" button
2. System hiển thị form: chọn duration (15/30/45/60 mins)
3. User chọn duration
4. User có thể chọn apps cần chặn (optional)
5. User click "Start"
6. System validate duration > 0
7. System bắt đầu timer
8. System chặn selected apps
9. System hiển thị countdown timer
10. User tập trung
11. Timer hết
12. System hiển thị "Completed!" & cộng points
13. User được redirect home, cây được trồng

## Alternative Flow
**Alt 1 - User cancel (bước 5)**
- User click Cancel
- Session không tạo, quay về home

**Alt 2 - Duration = 0 (bước 6)**
- System báo "Please select valid duration"
- User chọn lại

**Alt 3 - User try mở blocked app (bước 10)**
- System warning "Exit session?"
- Nếu yes: session kết thúc, cây chết, không cộng points
- Nếu no: quay về session, tiếp tục

**Alt 4 - Network error (bước 7)**
- System báo "Connection failed"
- User click Retry

**Alt 5 - Session timeout**
- Timer tự động hết
- System save session & cộng points
