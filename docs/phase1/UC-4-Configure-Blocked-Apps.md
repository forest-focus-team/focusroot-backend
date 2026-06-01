# UC-4: Configure Blocked Apps

## Thông tin
- **Actor**: Registered User
- **Precondition**: User đã login, device có apps
- **Postcondition**: Blocked apps list được lưu

## Main Flow
1. User click "Settings" > "Blocked Apps"
2. System fetch list apps từ device
3. System hiển thị checkbox list của tất cả apps
4. User select/unselect apps cần block
5. User click "Save"
6. System validate selection
7. System lưu vào database
8. System hiển thị "Saved successfully"
9. Apps trong list sẽ bị block ở session tương lai

## Alternative Flow
**Alt 1 - User cancel (bước 5)**
- User click Cancel
- Config không lưu, quay về settings

**Alt 2 - Database error (bước 7)**
- System báo "Failed to save"
- User click Retry

**Alt 3 - No permission (bước 2)**
- System báo "Permission denied, enable in Settings"
- User go to device settings & enable

**Alt 4 - User block all apps (bước 5)**
- System warning "Are you sure? This will block ALL apps"
- Nếu yes: lưu, nếu no: quay lại

**Alt 5 - Network error (bước 7)**
- System báo "Connection failed"
- User click Retry
