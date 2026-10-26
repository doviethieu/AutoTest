# Day 01: Tìm hiểu về Automation Test

## 1. Cucumber, Serenity BDD và Screenplay

Đây là cách kết hợp nhiều công cụ và cách tổ chức test:

- Cucumber cho phép viết kịch bản bằng Gherkin, thường theo cấu trúc Given, When, Then.
- Serenity BDD hỗ trợ tổ chức bài test và tạo báo cáo về các kịch bản đã chạy.
- Screenplay là một cách tổ chức code, mô tả Actor thực hiện Task và kiểm tra kết quả.

Trong hướng dẫn, kịch bản được viết trong file `.feature`, sau đó các bước được nối với code Java. Serenity có thể tích hợp Selenium để kiểm tra website.

Nguồn:
- https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay
- https://serenity-bdd.github.io/

## 2. Playwright

Playwright là framework để tự động hóa và kiểm tra ứng dụng web. Playwright Test có sẵn bộ chạy test, phần kiểm tra kết quả (assertions), cách cô lập test, chạy song song và báo cáo.

Playwright hỗ trợ Chromium, Firefox và WebKit. Bản Playwright cho Node.js thường được cài bằng npm và dùng JavaScript hoặc TypeScript. Playwright cũng có phiên bản Java, được quản lý bằng Maven.

Nguồn:
- https://playwright.dev/docs/intro

## 3. So sánh ngắn

Project của em hiện dùng Java và Selenium. Serenity BDD có thể kết hợp với Selenium và Java để tổ chức test, đặc biệt khi dùng Cucumber và Screenplay.

Playwright là một lựa chọn khác cho việc tự động hóa trình duyệt web, có bộ công cụ kiểm thử và báo cáo riêng.

## 4. Câu hỏi

### Automation test là gì?

Automation test là việc dùng chương trình và công cụ để tự động tái hiện lại các bước của manual tester (thực hiện các bước kiểm tra phần mềm, sau đó so sánh kết quả thực tế với kết quả mong đợi).

### Tại sao cần automation test?

Automation test giúp chạy lại các bước kiểm tra thường xuyên nhanh và nhất quán với độ tin cậy cao, phát hiện lỗi sau khi code thay đổi, và giảm thao tác kiểm thử lặp đi lặp lại bằng tay.

### Khi nào cần chạy kịch bản automation test?

Có thể chạy khi code thay đổi, trước khi phát hành phần mềm, hoặc tự động sau khi cập nhật code theo quy trình CI/CD. Nhóm phát triển chọn bộ test phù hợp với phạm vi thay đổi.