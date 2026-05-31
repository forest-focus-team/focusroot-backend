# Software Requirements Specification (SRS) - FocusRoot Project

## 1. Giới thiệu (Introduction)
### 1.1. Mục đích (Purpose)
Tài liệu Đặc tả Yêu cầu Phần mềm (SRS) này định nghĩa toàn bộ các yêu cầu chức năng và phi chức năng cho hệ thống quản lý và tối ưu hiệu suất làm việc **FocusRoot**. Tài liệu đóng vai trò là cơ sở kỹ thuật để đội ngũ phát triển (Backend, Frontend) phối hợp triển khai, hỗ trợ đội ngũ kiểm thử (Tester) lên kịch bản test, và làm căn cứ nghiệm thu sản phẩm qua từng giai đoạn.

### 1.2. Phạm vi hệ thống (Scope)
FocusRoot là một hệ thống ứng dụng di động (phát triển trên nền tảng React Native + Expo) kết hợp với hệ thống Backend (Spring Boot 3.2.x, Java 17, MySQL 8.0). Ứng dụng giúp người dùng nâng cao mức độ tập trung, giảm thiểu xao nhãng thông qua cơ chế trò chơi hóa "Trồng cây ảo" (Gamification). Hệ thống hỗ trợ các phiên tập trung cá nhân, tập trung theo nhóm thời gian thực, lưu trữ lịch sử, log hoạt động và cung cấp các phân tích dự đoán tối ưu hiệu suất làm việc.

---

## 2. Danh sách Tác nhân (Actors List)
Hệ thống FocusRoot bao gồm các tác nhân tương tác sau:

| STT | Tên Actor | Mô tả |
| :--- | :--- | :--- |
| 1 | **Guest (Người dùng chưa xác thực)** | Người dùng mới tải ứng dụng, chưa đăng nhập hệ thống. Chỉ có quyền xem giới thiệu hoặc thực hiện đăng ký/đăng nhập. |
| 2 | **User (Người dùng tiêu chuẩn)** | Người dùng đã đăng nhập hệ thống thành công. Có quyền tạo phiên tập trung, trồng cây, quản lý khu rừng, tham gia nhóm và xem lịch sử hoạt động. |
| 3 | **System/Timer (Hệ thống tự động)** | Tác nhân hệ thống chạy ngầm chịu trách nhiệm kiểm tra thời gian đếm ngược, tự động ghi nhận trạng thái phiên tập trung khi hết giờ hoặc khi phát hiện vi phạm. |

---

## 3. Danh sách Yêu cầu Chức năng - Phase 1 (Functional Requirements - Phase 1)
*Mục tiêu Phase 1 (Forest 1.0 - Tuần 1-4): Tập trung hoàn thiện các tính năng cốt lõi liên quan đến quản lý tài khoản, phiên tập trung cá nhân, quản lý thư viện cây và ghi nhật ký hoạt động dựa trên 8 thực thể JPA cốt lõi.*

Hệ thống phải đáp ứng tối thiểu 15 yêu cầu chức năng (FR) sau trong Phase 1:

| Mã YC | Tên Chức Năng | Mô tả Chi Tiết | Actor |
| :--- | :--- | :--- | :--- |
| **FR1.1** | Đăng ký tài khoản | Người dùng tạo tài khoản mới bằng các thông tin: Email, Mật khẩu và Họ tên (Lưu vào thực thể `User`). | Guest |
| **FR1.2** | Đăng nhập hệ thống | Xác thực tài khoản bằng Email/Mật khẩu; hệ thống trả về mã mã hóa JWT Token phục vụ cho các phiên làm việc tiếp theo. | Guest |
| **FR1.3** | Cập nhật hồ sơ cá nhân | Người dùng thay đổi thông tin hiển thị cơ bản bao gồm ảnh đại diện (Avatar) và tên hiển thị. | User |
| **FR1.4** | Xem thông tin tài khoản | Hiển thị thông tin tổng quan của người dùng, ngày tham gia và tổng số điểm tích lũy hệ thống. | User |
| **FR1.5** | Xem danh sách loài cây | Hiển thị thư viện toàn bộ các loài cây được cấu hình sẵn trong hệ thống (`TreeSpecies`). | User |
| **FR1.6** | Lựa chọn loài cây để trồng | Người dùng chọn một loài cây cụ thể trong thư viện trước khi bắt đầu bấm giờ tập trung. | User |
| **FR1.7** | Khởi tạo phiên tập trung cá nhân | Thiết lập thời gian mục tiêu (ví dụ: 25, 50 phút) và bắt đầu kích hoạt đồng hồ đếm ngược (`FocusSession`). | User |
| **FR1.8** | Hủy bỏ phiên tập trung | Người dùng chủ động chọn bỏ cuộc giữa chừng; hệ thống đánh dấu phiên "Thất bại", cây đang trồng bị chết. | User |
| **FR1.9** | Ghi nhận phiên thành công | Khi đồng hồ đếm ngược về 0, hệ thống tự động cộng điểm tích lũy, đánh dấu phiên "Thành công" và lưu thông tin cây vào khu rừng. | System, User |
| **FR1.10** | Quản lý khu rừng cá nhân | Hiển thị trực quan khu rừng (`MyForest`) chứa danh sách toàn bộ các cây đã được trồng thành công theo bộ lọc thời gian. | User |
| **FR1.11** | Ghi nhật ký hoạt động | Hệ thống tự động ghi lại lịch sử các hành động quan trọng (Đăng nhập, Thay đổi mật khẩu, Bắt đầu phiên) vào `UserActivityLog`. | System |
| **FR1.12** | Xem nhật ký hoạt động | Người dùng có thể truy cập lịch sử hành động hệ thống của chính mình theo dạng danh sách tuyến tính thời gian. | User |
| **FR1.13** | Thống kê hiệu suất cơ bản | Hiển thị tổng số phút đã tập trung và tỷ lệ phiên thành công/thất bại thông qua biểu đồ trực quan. | User |
| **FR1.14** | Thay đổi mật khẩu | Cho phép người dùng cập nhật mật khẩu mới khi đang trong trạng thái đăng nhập để bảo mật tài khoản. | User |
| **FR1.15** | Đăng xuất hệ thống | Hủy phiên làm việc hiện tại, xóa thông tin JWT Token lưu trữ tạm thời trên thiết bị di động. | User |

---

## 4. Danh sách Yêu cầu Chức năng - Phase 2 (Functional Requirements - Phase 2)
*Mục tiêu Phase 2 (Forest 2.0 - Tuần 5-7): Mở rộng kết nối cộng đồng thời gian thực thông qua giao thức WebSocket và tích hợp các module thuật toán thông minh phục vụ phân tích, dự đoán hiệu suất.*

Hệ thống phải đáp ứng tối thiểu 8 yêu cầu chức năng (FR) sau trong Phase 2:

| Mã YC | Tên Chức Năng | Mô tả Chi Tiết | Actor |
| :--- | :--- | :--- | :--- |
| **FR2.1** | Tạo nhóm tập trung | Người dùng khởi tạo một nhóm tập trung (`FocusGroup`) và nhận mã mời (Invite Code) ngẫu nhiên để chia sẻ. | User |
| **FR2.2** | Tham gia và rời khỏi nhóm | Người dùng tham gia vào một nhóm bằng cách nhập mã mời hoặc chủ động chọn rời khỏi nhóm (`GroupMember`). | User |
| **FR2.3** | Khởi tạo phiên tập trung nhóm | Trưởng nhóm kích hoạt phiên tập trung chung (`GroupSession`); hệ thống phát tín hiệu mời toàn bộ thành viên online tham gia. | User |
| **FR2.4** | Đồng bộ trạng thái thời gian thực | Sử dụng **Spring WebSocket + STOMP** để đồng bộ đồng hồ đếm ngược và trạng thái trồng cây của tất cả thành viên trong nhóm. | System, User |
| **FR2.5** | Kênh chat nhóm nội bộ | Cho phép các thành viên trong cùng nhóm gửi tin nhắn văn bản thời gian thực để khích lệ nhau trong phiên tập trung. | User |
| **FR2.6** | Bảng xếp hạng nhóm | Hiển thị danh sách xếp hạng đóng góp thời gian của các thành viên trong nhóm theo tuần hoặc theo tháng. | User |
| **FR2.7** | Dự đoán xu hướng tập trung | Module thuật toán (`prediction`) phân tích dữ liệu lịch sử để dự đoán khung giờ tập trung đạt hiệu suất cao nhất của người dùng. | System, User |
| **FR2.8** | Cảnh báo rủi ro xao nhãng | Phân tích thói quen chuyển đổi ứng dụng từ nhật ký hoạt động nhằm đưa ra cảnh báo sớm khi người dùng có dấu hiệu giảm tập trung. | System, User |

---

## 5. Yêu cầu Phi Chức Năng (Non-Functional Requirements)

### 5.1. Hiệu năng & Khả năng chịu tải (Performance & Scalability)
* **NFR1:** Thời gian phản hồi (Response Time) cho các API REST thông thường (Xác thực, cấu hình, dữ liệu cây) phải nhỏ hơn **200ms**.
* **NFR2:** Độ trễ truyền tải thông điệp qua WebSocket (trạng thái phòng, tin nhắn chat) phải nhỏ hơn **100ms** trong điều kiện kết nối mạng ổn định.
* **NFR3:** Hệ thống Backend có khả năng xử lý tốt tối thiểu **2,000 người dùng hoạt động đồng thời (CCU)** mà không xảy ra hiện tượng nghẽn kết nối cơ sở dữ liệu.

### 5.2. An toàn & Bảo mật (Security)
* **NFR4:** Toàn bộ thông tin mật khẩu của người dùng bắt buộc phải được mã hóa một chiều bằng thuật toán mạnh (BCrypt) thông qua cấu hình Spring Security trước khi lưu xuống cơ sở dữ liệu MySQL.
* **NFR5:** Cơ chế xác thực sử dụng mã định danh **JWT (JSON Web Token)** với thư viện mã nguồn `jjwt 0.12.5`, được đính kèm vào HTTP Header dưới dạng `Authorization: Bearer` cho mọi request yêu cầu xác thực.
* **NFR6:** Áp dụng Spring Security Filter Chain để cấu hình phân quyền nghiêm ngặt cho toàn bộ hệ thống API endpoints, chặn các truy cập trái phép từ bên ngoài.

### 5.3. Độ tin cậy & Tính toàn vẹn dữ liệu (Reliability & Data Integrity)
* **NFR7:** Sử dụng cơ chế quản lý giao dịch Spring `@Transactional` đảm bảo tính toàn vẹn dữ liệu (ACID) khi thực hiện chuỗi hành động phức tạp (cộng/trừ điểm hệ thống, ghi log hoạt động, và cập nhật trạng thái trồng cây).
* **NFR8:** Mọi thay đổi mã nguồn đẩy lên hệ thống bắt buộc phải vượt qua pipeline kiểm thử tự động (CI) của GitHub Actions (`backend-test.yml`) chạy trên cơ sở dữ liệu H2 in-memory trước khi được xem xét tích hợp vào nhánh chính.

### 5.4. Khả năng tương thích (Compatibility)
* **NFR9:** Giao diện ứng dụng di động phát triển bằng React Native + Expo phải đảm bảo hiển thị đồng bộ, tối ưu hiệu năng và chạy mượt mà trên cả 2 nền tảng hệ điều hành di động phổ biến hiện nay là **iOS** và **Android**.