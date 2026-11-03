# Day 03 - Test cases cho form Request a Demo

- URL: https://saucelabs.com/request-demo
- Chức năng: Gửi yêu cầu đặt lịch demo
- Trường bắt buộc trên giao diện: Business Email, Company, Use Case
- Trường không bắt buộc: Comments

## Test cases

| ID | Tên test case | Dữ liệu / thao tác | Kết quả mong đợi |
|---|---|---|---|
| TC01 | Gửi form với dữ liệu hợp lệ | Email hợp lệ, Company hợp lệ, giữ Use Case mặc định, nhập Comments | Dữ liệu hợp lệ và form có thể được gửi |
| TC02 | Để trống tất cả trường bắt buộc | Không nhập Email, không nhập Company | Form không được gửi; trình duyệt yêu cầu điền trường bắt buộc |
| TC03 | Để trống Business Email | Nhập Company, để trống Email | Form không được gửi; Email được yêu cầu |
| TC04 | Email sai định dạng | Nhập `abc`, nhập Company | Form không được gửi; Email không hợp lệ |
| TC05 | Email thiếu ký tự `@` | Nhập `userexample.com` | Form không được gửi; Email không hợp lệ |
| TC06 | Email thiếu tên miền | Nhập `user@` | Form không được gửi; Email không hợp lệ |
| TC07 | Để trống Company | Nhập Email hợp lệ, để trống Company | Form không được gửi; Company được yêu cầu |
| TC08 | Để trống Email và Company | Để trống cả hai trường, giữ Use Case mặc định | Form không được gửi; các trường bắt buộc được yêu cầu |
| TC09 | Email có khoảng trắng | Nhập ` user@example.com ` | Kiểm tra trang có từ chối hoặc xử lý khoảng trắng trong Email |
| TC10 | Company chỉ có khoảng trắng | Nhập vài dấu cách vào Company | Ghi nhận cách trang xử lý giá trị chỉ có khoảng trắng |
| TC11 | Bỏ chọn Use Case | Chọn giá trị trống nếu danh sách có lựa chọn trống | Nếu Use Case bắt buộc, form không được gửi |
| TC12 | Kiểm tra các lựa chọn Use Case | Mở danh sách Use Case | Danh sách hiển thị các lựa chọn; có thể chọn một lựa chọn hợp lệ |
| TC13 | Để trống Comments | Nhập đủ các trường bắt buộc, không nhập Comments | Comments được bỏ trống vì đây là trường tùy chọn |
| TC14 | Comments có tiếng Việt và ký tự đặc biệt | Nhập `Tôi muốn tìm hiểu về Selenium & automation!` | Nội dung được nhập và hiển thị bình thường |
| TC15 | Kiểm tra giới hạn độ dài | Nhập nội dung dài vào Comments | Ghi nhận giới hạn nếu trường có khai báo |
| TC16 | Sửa Company sau khi bị báo lỗi | Để trống Company, kiểm tra lỗi, sau đó nhập Company hợp lệ | Có thể nhập lại Company; trạng thái lỗi được cập nhật |
| TC17 | Chọn Use Case hợp lệ và giữ Comments trống | Nhập Email, Company; chọn Use Case; bỏ trống Comments | Comments không chặn việc gửi form |

## Ghi chú

- TC11 chỉ áp dụng nếu danh sách Use Case có lựa chọn trống.
- TC15 chỉ áp dụng nếu trường Comments có giới hạn độ dài.
- TC09 và TC10 cần ghi nhận hành vi thực tế của trang; không kết luận là lỗi nếu chưa có yêu cầu quy định cách xử lý khoảng trắng.
- TC01 kiểm tra dữ liệu hợp lệ trước khi gửi. Chỉ bấm gửi thật khi có dữ liệu thật.