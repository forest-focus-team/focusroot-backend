# UC-3: View Statistics

## Thông tin
- **Actor**: Registered User
- **Precondition**: User đã login, có ít nhất 1 session
- **Postcondition**: Hiển thị thống kê

## Main Flow
1. User click "Statistics" tab
2. System fetch user's sessions từ database
3. System calculate: total focus time, sessions completed, success rate
4. System hiển thị:
   - Total focus time (today/week/month)
   - Number of sessions
   - Success rate (%)
   - Tree/Forest count
   - Charts/graphs
5. User có thể filter theo khoảng thời gian
6. System update display

## Alternative Flow
**Alt 1 - Network error (bước 2)**
- System báo "Failed to load stats"
- User click Retry

**Alt 2 - Không có session nào**
- System hiển thị "No sessions yet"
- Suggest "Start your first session"

**Alt 3 - User click detail session**
- System hiển thị chi tiết: duration, start time, end time, status
- User có thể view hoặc back

**Alt 4 - Database error (bước 2)**
- System báo "Error loading data"
- User click Retry hoặc Back
