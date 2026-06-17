# Tài liệu API Specification

**Base URL:** `/api/v1`
**Authorization:** Sử dụng Bearer Token (JWT) cho tất cả các endpoint ngoại trừ module Auth.

## 1. Module Auth (Bảo mật & Xác thực)
Xử lý các nghiệp vụ cấp phát JWT khi người dùng đăng ký và đăng nhập.

| Method | Path | Request Body | Response Body | Status Codes |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | `RegisterRequest` | `UserResponse` | 201 (Created), 400 (Bad Request) |
| `POST` | `/auth/login` | `LoginRequest` | `TokenResponse` | 200 (OK), 401 (Unauthorized) |

## 2. Module User (Người dùng)
Quản lý hồ sơ, thông tin cá nhân và tài sản số (Coin).

| Method | Path | Request Body | Response Body | Status Codes |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/users/me` | *None* | `UserResponse` | 200 (OK), 401 (Unauthorized) |
| `PUT` | `/users/me` | `UpdateProfileRequest` | `UserResponse` | 200 (OK), 400 (Bad Request) |

## 3. Module Session (Phiên tập trung)
Kiểm soát logic thời gian bắt đầu và kết thúc của các phiên tập trung cá nhân. Vòng lặp hành vi yêu cầu cam kết thời gian từ 5 đến 180 phút.

| Method | Path | Request Body | Response Body | Status Codes |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/sessions/start` | `StartSessionRequest` | `SessionResponse` | 201 (Created), 400 (Bad Request) |
| `POST` | `/sessions/{id}/end`| `EndSessionRequest` | `SessionResponse` | 200 (OK), 404 (Not Found) |
| `GET` | `/sessions/history`| *None* | `List<SessionResponse>`| 200 (OK), 401 (Unauthorized) |

## 4. Module Forest (Khu rừng cá nhân)
Quản lý dữ liệu khu rừng cá nhân và trạng thái các loài cây (xanh tốt hoặc héo úa) dựa trên kết quả của phiên tập trung.

| Method | Path | Request Body | Response Body | Status Codes |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/forest` | *None* | `ForestResponse` | 200 (OK), 401 (Unauthorized) |
| `GET` | `/forest/trees` | *None* | `List<TreeItemResponse>`| 200 (OK), 401 (Unauthorized) |

## 5. Module Group (Tương tác nhóm Real-time)
Xử lý logic tương tác nhóm, áp lực đồng lứa (Peer-pressure) và các phòng tập trung chung. 

| Method | Path | Request Body | Response Body | Status Codes |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/groups` | `CreateGroupRequest` | `GroupResponse` | 201 (Created), 400 (Bad Request) |
| `POST` | `/groups/{id}/join` | `JoinGroupRequest` | `GroupResponse` | 200 (OK), 403 (Forbidden), 404 (Not Found) |
| `GET` | `/groups/{id}` | *None* | `GroupDetailResponse`| 200 (OK), 404 (Not Found) |
