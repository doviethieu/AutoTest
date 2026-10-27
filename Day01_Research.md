# Day 01: Tìm hiểu về Automation Test

## 1. Cucumber, Serenity BDD và Screenplay

Đây là ba phần có vai trò khác nhau, có thể kết hợp để tạo một bộ automation test bằng Java.

- **Cucumber** giúp mô tả tình huống kiểm thử bằng ngôn ngữ gần với cách người dùng nói, theo cấu trúc `Given – When – Then`. Kịch bản thường được lưu trong file `.feature`.
- **Serenity BDD** hỗ trợ tổ chức và chạy các bài test, đồng thời tạo báo cáo cho biết những tình huống nào đã chạy và kết quả ra sao.
- **Screenplay** là cách tổ chức code test. Ta mô tả một `Actor` có khả năng dùng trình duyệt, thực hiện `Task` (hành động) và kiểm tra kết quả qua `Question`.

Ví dụ, một kịch bản “người dùng mở trang chủ” có thể được viết bằng Given–When–Then. Cucumber nối các câu đó với code Java; code dùng Screenplay để thực hiện thao tác, còn Serenity ghi lại kết quả vào báo cáo.

**Điểm mạnh:** Kịch bản dễ đọc với cả người không viết code; code được chia thành các hành động nhỏ để dễ tái sử dụng; báo cáo giúp xem test nào đã chạy và kết quả thế nào.

**Điểm cần học thêm:** Cần hiểu cách các phần kết nối với nhau, như file `.feature`, Java step definitions và Screenplay. Vì vậy cấu hình ban đầu phức tạp hơn một script Selenium `main()` đơn giản.

Nguồn:
- [Cucumber with Serenity BDD and Screenplay](https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay)
- [Screenplay Fundamentals](https://serenity-bdd.github.io/docs/screenplay/screenplay_fundamentals)

## 2. Playwright

Playwright là bộ công cụ tự động hóa và kiểm thử ứng dụng web. Playwright Test dành cho Node.js có sẵn bộ chạy test, assertions, cách cô lập test, chạy song song và báo cáo. Playwright hỗ trợ Chromium, Firefox và WebKit.

Tài liệu có sẵn hướng dẫn bắt đầu cho Node.js hướng dẫn cài bằng npm và viết test bằng JavaScript hoặc TypeScript. Playwright cũng có phiên bản Java; phiên bản này được thêm vào project bằng Maven.

**Điểm mạnh:** Có nhiều công cụ kiểm thử được tích hợp sẵn. Playwright tự chờ một số điều kiện trên trang trước khi thao tác và có assertions tự thử lại khi trang chưa cập nhật xong.

**Điểm cần cân nhắc:** Khi dùng bản Node.js, cần học hệ sinh thái JavaScript/TypeScript và npm. Nếu muốn tiếp tục dùng Java, có thể tìm hiểu tài liệu Playwright Java và Maven riêng.

**Phù hợp với:** Theo em, Playwright hợp với nhóm làm kiểm thử web cần chạy trên nhiều trình duyệt và muốn có sẵn nhiều công cụ kiểm thử.

Nguồn:
- [Playwright Test for Node.js](https://playwright.dev/docs/intro)
- [Playwright Java](https://playwright.dev/java/docs/intro)
- [Playwright Java: Writing Tests](https://playwright.dev/java/docs/writing-tests)

## 3. So sánh với project hiện tại

Project của em hiện dùng Java và Selenium. Selenium điều khiển trình duyệt Chrome thông qua `ChromeDriver`.

Serenity BDD, Cucumber và Screenplay có thể được kết hợp với Selenium để mô tả kịch bản, tổ chức code test và tạo báo cáo.

Playwright là một lựa chọn khác để tự động hóa trình duyệt. Nó có bộ công cụ test riêng cho Node.js, đồng thời cũng cung cấp API Java dùng với Maven.

Tóm lại: Selenium là công cụ em đang dùng để điều khiển trình duyệt; Serenity, Cucumber và Screenplay giúp tổ chức kịch bản và code theo cách dễ đọc hơn; Playwright là một lựa chọn khác để tự động hóa và kiểm thử trình duyệt web.


## 4. Câu hỏi

### Automation test là gì?

Automation test là việc dùng chương trình và công cụ để tự động tái hiện lại các bước của manual tester (thực hiện các bước kiểm tra phần mềm, sau đó so sánh kết quả thực tế với kết quả mong đợi).

### Tại sao cần automation test?

Automation test giúp chạy lại các bước kiểm tra thường xuyên nhanh và nhất quán với độ tin cậy cao, phát hiện lỗi sau khi code thay đổi, và giảm thao tác kiểm thử lặp đi lặp lại bằng tay.

### Khi nào cần chạy kịch bản automation test?

Có thể chạy khi code thay đổi, trước khi phát hành phần mềm, hoặc tự động sau khi cập nhật code theo quy trình CI/CD. Nhóm phát triển chọn bộ test phù hợp với phạm vi thay đổi.