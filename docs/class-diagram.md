# Class Diagram Architecture - FocusRoot

Tài liệu này mô tả kiến trúc phân lớp hệ thống Backend Spring Boot 3.2.x của dự án FocusRoot bao gồm các lớp thành phần Controller, Service, Repository và thực thể Entity.

## 1. Thành phần kiến trúc
- **Controllers:** Tiếp nhận HTTP Request từ ứng dụng Mobile React Native, kiểm tra bảo mật qua Spring Security JWT và điều hướng dữ liệu xử lý.
- **Services:** Nơi chứa toàn bộ nghiệp vụ (Business Logic) cốt lõi của hệ thống.
- **Repositories:** Lớp trừu tượng hóa Spring Data JPA tương tác trực tiếp xuống cơ sở dữ liệu MySQL 8.0.
- **Entities:** Bản đồ ánh xạ dữ liệu trực tiếp 1-1 với các bảng trong Database.

## 2. Mã nguồn PlantUML
Sơ đồ lớp được quản lý bằng mã nguồn PlantUML trực tiếp tại tệp cấu trúc: `docs/class-diagram.puml`.