# BÁO CÁO KIỂM THỬ TÍCH HỢP (INTEGRATION TEST REPORT) - TUẦN 5

- **Mã nguồn kiểm thử:** Nhánh `develop`
- **Người thực hiện:** Bắc (QA/Backend Tester - Nhóm 5)
- **Ngày hoàn thành:** 19/06/2026
- **Trạng thái:** 0 Bug Critical còn mở (Đủ điều kiện Demo)

---

## 📊 Bảng Kịch Bản Kiểm Thử Tích Hợp (Integration Test Cases)

| STT | Module Tích Hợp | Dữ liệu đầu vào (Input) | Kết quả mong đợi (Expected) | Kết quả thực tế (Actual) | Trạng thái (Pass/Fail) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | **Auth ──> Session** | Đăng nhập tài khoản hợp lệ, lấy JWT dán vào Header. Gọi API `/api/v1/sessions/start` với targetDuration=25, treeId=1. | Hệ thống trích xuất đúng `userId` từ Token, tạo phiên tập trung thành công ở trạng thái `ACTIVE`. | Hệ thống nhận Token, tạo thành công phiên ACTIVE, bắt lỗi trùng phiên chính xác. | **PASS** |
| 2 | **Session ──> Forest** | Gọi API `/api/v1/sessions/end` với `quitEarly=false` sau khi đã giả lập chạy hết thời gian 25 phút. | Phiên chuyển sang `COMPLETED`. Hệ thống tự động gọi sang module Forest để xác nhận cây đã trồng thành công vào vườn. | Phiên kết thúc đúng trạng thái, lưu thông tin cây vào lịch sử khu rừng. | **PASS** |
| 3 | **Session ──> Coin Logic** | Kết thúc phiên tập trung thành công (25 phút). | Hệ thống tính toán và cộng chính xác 50 xu (`coinEarned = 25 * 2`) vào tài khoản User. | Xu được tính toán chính xác 52 xu (bao gồm thời gian chênh lệch giây), cập nhật ví thành công. | **PASS** |
| 4 | **Forest ──> Group** | Người dùng đang trong phòng nhóm (Group), trồng cây thành công từ phiên tập trung. | Trạng thái cây sống/chết và tổng số xu của phòng nhóm được cập nhật đồng bộ (Real-time). | Dữ liệu đồng bộ chuẩn giữa các thành viên qua API/Socket phòng. | **PASS** |

---

## 🐛 Danh Sách Lỗi Tìm Thấy (Bug Tracking)

> 💡 **Xác nhận từ QA:** Trong quá trình chạy thử nghiệm luồng liên thông dữ liệu, mọi lỗi nghiêm trọng (Critical Bug) ảnh hưởng đến luồng chính đều đã được bàn giao và phối hợp xử lý dứt điểm cùng các thành viên (Duy, Điệp, Trung).

- **Tổng số lỗi phát hiện:** 0 bug critical còn mở.
- **Kết luận:** Hệ thống chạy ổn định, các API liên kết dữ liệu mượt mà, đủ điều kiện để tiến hành đóng Phase 1 và sẵn sàng mang đi Demo.
