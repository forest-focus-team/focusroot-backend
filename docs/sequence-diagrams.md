# Sequence Diagrams Document - FocusRoot

Tài liệu này chứa danh sách các sơ đồ tuần tự mô tả chi tiết luồng xử lý dữ liệu giữa Client và hệ thống Backend cho 3 nghiệp vụ quan trọng nhất của Phase 1.

## 1. Danh sách các luồng nghiệp vụ
1. **Luồng Đăng ký & Đăng nhập:** Quản lý xác thực người dùng và phân phối mã bảo mật JWT định danh. 
   - Mã nguồn chi tiết: `docs/register-login-sequence.puml`
2. **Luồng Quản lý Phiên tập trung cá nhân:** Điều hướng bắt đầu, xử lý logic cộng điểm khi thành công hoặc hủy bỏ làm chết cây khi thất bại.
   - Mã nguồn chi tiết: `docs/focus-session-sequence.puml`
3. **Luồng Nghiệp vụ Nhóm cơ bản:** Quản lý hành động khởi tạo phòng và tham gia phòng thông qua cơ chế mã mời ngẫu nhiên.
   - Mã nguồn chi tiết: `docs/group-sequence.puml`