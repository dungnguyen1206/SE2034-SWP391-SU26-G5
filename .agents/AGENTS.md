# Coding Rules

## Phong cách code
- Code syntax đơn giản, dễ hiểu (người mới học).
- Không dùng lambda expression trừ khi thực sự cần thiết.
- Comment ngắn gọn bằng tiếng Việt trước hàm, khối lệnh, câu điều kiện, hoặc vòng lặp khi cần.

## Kiến trúc
- Tuân thủ mô hình MVC: Controller → Service → Repository.
- Sử dụng DTO khi cần truyền dữ liệu giữa các layer.
- Đặt logic đúng layer (Controller không chứa business logic, Repository không chứa logic xử lý).

## Quy tắc làm việc nhóm
- Tuyệt đối không code phần của thành viên khác mà chưa hỏi ý kiến trước.
- Chỉ sửa đổi file/module thuộc phạm vi được giao.
