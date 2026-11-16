# BTVN Day06: TestNG – Request a demo

Kiểm thử https://saucelabs.com/request-demo bằng TestNG với 47 bộ dữ liệu Excel.
Mỗi dòng là một lần chạy `@Test` qua `@DataProvider`. Mỗi lần có trình duyệt riêng,
được đóng trong `@AfterMethod(alwaysRun = true)`.

## Chạy và xem báo cáo

Yêu cầu JDK 21, Maven, Chrome, kết nối mạng. Mở Terminal tại thư mục có pom.xml:

```bash
mvn clean test -Dtest=BookDemoTest
```

Chạy riêng ca LENGTH có Input Length = 5000 (hiện tại là TC25), kèm log ChromeDriver:

```bash
mvn test -Dtest=BookDemoTest -DbookDemo.check=LENGTH -DbookDemo.inputLength=5000 -DbookDemo.debug=true
```

Lệnh này chỉ chạy dòng khớp bộ lọc, không chạy case gửi demo thật.
Log chi tiết nằm trong `target/browser-logs/`. Nếu không truyền bộ lọc, cả 47 dòng vẫn chạy.

Báo cáo: `target/surefire-reports/index.html`. Trên macOS:

```bash
open target/surefire-reports/index.html
```

Báo cáo tự sinh gồm tên từng dòng Excel, PASS/FAIL/SKIP, các bước,
mong đợi/thực tế, exception, biểu đồ kết quả và thời gian.
FAIL vẫn làm Maven báo build failure; unhappy case PASS khi phát hiện đúng lỗi mong đợi.
Không chỉnh file HTML sinh ra để thay đổi kết quả.

## File cần nộp

- `src/test/java/day06/BookDemoTest.java`: annotation và phép kiểm tra theo Check.
- `src/test/java/data/BookDemoData.java`: đọc Excel từ classpath, trả dữ liệu cho DataProvider.
- `src/test/java/UI/BookDemoUI.java`: locator và ánh xạ tên trường.
- `src/test/resources/BookDemoDay06Data.xlsx`: dữ liệu, cách kiểm tra và kết quả mong đợi.
- `index.html`
## Các nhóm test

| Case | Nhóm kiểm tra |
| --- | --- |
| TC01 | Gửi form đủ dữ liệu, xác nhận đúng URL thank-you-demo và tiêu đề cảm ơn |
| TC02–TC05 | Email trống, thiếu @, thiếu domain, khoảng trắng bên trong |
| TC06 | Gmail bị từ chối làm business email; nút submit bị khóa |
| TC07–TC12 | Thiếu từng trường First name, Last name, Company, Phone, Country, State |
| TC13 | Use Case có lựa chọn mặc định |
| TC14 | Duyệt và chọn tất cả option Use Case hợp lệ |
| TC15 | Comments là tùy chọn, để trống vẫn hợp lệ |
| TC16 | Comments giữ tiếng Việt và ký tự đặc biệt |
| TC17 | Comments vượt giới hạn một ký tự |
| TC18 | Xóa Company gây lỗi rồi nhập lại để sửa lỗi |
| TC19 | United States và California hợp lệ |
| TC20 | Đổi từ United States sang Vietnam: State ẩn và không còn bắt buộc |
| TC21–TC22 | Email thiếu local part, email có hai ký tự @ |
| TC23–TC24 | First name và Last name giữ được tiếng Việt |
| TC25–TC26 | Comments tại giới hạn và ngay dưới giới hạn |
| TC27 | Form ban đầu: trường hiển thị, trường đang ẩn, ô trống và Use Case mặc định |
| TC28 | Các trường bắt buộc khi form mở rộng; Comments tùy chọn và State ẩn với Vietnam |
| TC29–TC31 | Xóa rồi sửa lại First name, Last name, Phone; xác nhận lỗi trước khi sửa |
| TC32–TC33 | Để Country/State chưa chọn, xác nhận lỗi required rồi chọn dữ liệu hợp lệ |
| TC34 | Sửa email sai định dạng thành email được chấp nhận; form mở rộng và hết lỗi |
| TC35–TC36 | Đổi Gmail sang business email và chiều ngược lại; kiểm tra lỗi và trạng thái nút |
| TC37 | Đổi Vietnam sang United States: State hiện, bắt buộc và chọn được California |
| TC38 | Đổi United States → Vietnam → United States; kiểm tra State lại khi quay về |
| TC39–TC40 | Chọn Japan/United Kingdom; xác nhận Country đúng, State ẩn và form hợp lệ |
| TC41 | Nhấn Enter khi thiếu Company; trình duyệt tập trung đúng trường và không gửi form |
| TC42 | Refresh sau khi điền: ô dữ liệu trở về trống, form thu gọn và Use Case về mặc định |
| TC43–TC44 | Comments nhiều dòng; Company chứa dấu & và dấu chấm được giữ nguyên |
| TC45–TC46 | Email có dấu + và subdomain hợp lệ về cú pháp HTML |
| TC47 | Duyệt các lựa chọn State và đối chiếu lựa chọn thực tế |

Đây là phạm vi 47 case theo form/bài học hiện tại, không khẳng định bao phủ mọi đầu vào,
trình duyệt hoặc kiểm tra phía server. Chưa có đặc tả để tự đặt quy tắc số điện thoại,
độ dài tên hay thời gian phản hồi. Các case VALID/OPTIONAL/TEXT kiểm tra form trước gửi,
không coi việc nhập được dữ liệu là đã gửi thành công.

## Excel thực sự điều khiển bài test

`Test Case` là tên, `Type` là phân loại. Code không dùng mã TC để chọn logic.

| Check | Cách dùng Expected Result / cột phụ |
| --- | --- |
| SUBMIT | Expected Result là URL xác nhận; đây là case gửi yêu cầu thật |
| EMAIL_VALIDITY | Target Field = Business Email, Validity = valueMissing hoặc typeMismatch; Expected Result = true/false |
| BUSINESS_EMAIL | Expected Result là trạng thái enabled của nút gửi; kiểm tra thêm lỗi business email |
| MISSING | Target Field là trường thiếu; Expected Result = true cho valueMissing. Các trường còn lại phải hợp lệ trước khi bấm submit |
| DEFAULT_SELECT | Expected Result là text mặc định của dropdown; để trống ô dữ liệu dropdown trong Excel |
| OPTIONS | Expected Result = true; chọn lần lượt các option có giá trị, đối chiếu option đang chọn |
| OPTIONAL | Expected Result = false cho thuộc tính required; ô dữ liệu mục tiêu để trống |
| TEXT | Expected Result là chính xác chuỗi mong đợi được giữ lại trong trường |
| LENGTH | Input Length là số ký tự nhập; Expected Result là số ký tự được chấp nhận |
| RECOVERY | Target Field là trường cần xóa/nhập lại; Expected Result = true sau khi sửa |
| VALID | Target Field = State; Expected Result = true, đối chiếu State với dữ liệu |
| COUNTRY_CHANGE | Next Country là quốc gia sau đổi; Expected Result = false cho State hiển thị |
| INITIAL_FORM | Không nhập dữ liệu; Expected Result là Use Case mặc định; kiểm tra form lúc mở |
| REQUIRED_FIELDS | Expected Result = true cho các trường bắt buộc; Comments không bắt buộc, State ẩn |
| SELECT_RECOVERY | Target Field = Country/State; bước điền ban đầu để trường mục tiêu chưa chọn, sau đó chọn dữ liệu cùng tên cột; Expected Result = true |
| EMAIL_CHANGE | Initial Email State = FORMAT_ERROR/BLOCKED/ACCEPTED; Business Email là giá trị đầu, Next Email là giá trị sau sửa; Expected Result là trạng thái enabled của nút sau sửa |
| COUNTRY_SHOW | Country = Vietnam, Next Country = United States, State = California; Expected Result = true cho State hiển thị |
| COUNTRY_ROUNDTRIP | Country = United States, Next Country = Vietnam; quay lại Country và chọn State; Expected Result = true cho State hiển thị |
| SELECT | Target Field = Country; Expected Result là quốc gia được chọn; dùng cho quốc gia không cần State |
| REFRESH | Điền dữ liệu rồi refresh; Expected Result là Use Case mặc định sau tải lại |

Muốn thêm case cùng loại, thêm một dòng Excel và điền Check/Target Field/kết quả mong đợi.
Ví dụ thêm MISSING cho Company không phải sửa code dù mã case là TC48.
Chỉ khi bổ sung một hành vi mới chưa có trong các loại Check thì mới cần thêm hàm kiểm tra.

Đọc Excel không trim dữ liệu nhập nên vẫn kiểm tra được khoảng trắng.
Case thiếu email chỉ kiểm tra constraint riêng của email vì các trường mở rộng chưa hiển thị.
Case thiếu các trường còn lại xác nhận các trường khác hợp lệ và trình duyệt tập trung đúng trường lỗi.
Nội dung validationMessage được ghi lại; loại lỗi được so sánh bằng validity để không phụ thuộc ngôn ngữ Chrome.
`Submit With` để trống hoặc CLICK dùng nút gửi; ENTER dùng phím Enter cho Check MISSING.
TC45–TC46 chỉ xác nhận cú pháp HTML, không xác nhận hộp thư tồn tại hay server sẽ nhận yêu cầu.
Country và State không có option `value=""`, nên không dùng `selectByValue("")`
để bỏ chọn. Country khởi tạo với selectedIndex = -1; trang tự xóa State khi Country khác United States.
TC37–TC38 xác nhận State trống theo hành vi này trước khi chọn California.
Use Case có giá trị mặc định AI Test Authoring; TC13 kiểm tra riêng hành vi đó.

## Phạm vi cần bổ sung khi có môi trường hoặc đặc tả

- Checkbox nhận marketing khi xuất hiện theo khu vực: mặc định, chọn/bỏ chọn và có bắt buộc hay không.
- Gửi bằng Enter với dữ liệu hợp lệ, gửi hai lần, trạng thái đang gửi và lỗi server/mất mạng.
- Quy tắc nghiệp vụ số điện thoại, chỉ khoảng trắng, độ dài tên/công ty và chuẩn hóa dữ liệu.
- Thiết bị di động, trình duyệt khác, bàn phím và trợ năng.

Các mục này chưa được tự động hóa trong bộ 47 case. Cần xác nhận hành vi mong đợi;
kiểm thử lỗi server và gửi lặp cần môi trường có thể kiểm soát, tránh tạo nhiều yêu cầu demo thật.

TC01 gửi yêu cầu demo thật. Chỉ một dòng dùng Check SUBMIT; các dòng khác không gửi form hợp lệ.
Email example.com là dữ liệu mẫu. Nếu server không chấp nhận thì TC01 phải FAIL;
không thay kết quả mong đợi bằng giá trị thực tế để làm test PASS.
Các case LENGTH chuẩn bị prefix bằng JavaScript, luôn nằm dưới maxlength và dưới độ dài cần thử,
rồi nhập phần sát biên bằng sendKeys. Với maxlength = 5000:

| Input Length | Chuỗi chuẩn bị | Ký tự nhập bằng sendKeys | Độ dài mong đợi |
| --- | --- | --- | --- |
| 4999 | 4998 | 1 | 4999 |
| 5000 | 4999 | 1 | 5000 |
| 5001 | 4999 | 2 | 5000 |

Đây là kiểm tra xử lý ở biên, không đo hiệu năng khi gõ liên tục 5.000 ký tự.
Không gán trực tiếp chuỗi cuối cùng hoặc tự cắt theo kết quả mong đợi;
trình duyệt phải xử lý ký tự chạm/vượt giới hạn qua sendKeys, rồi so sánh kết quả với Excel.
Nếu trường không khai báo maxlength, case LENGTH được đánh dấu SKIP có lý do.

Khi submit chuyển trang, bước chờ tìm lại phần tử nếu DOM vừa thay đổi gây stale element.
Chỉ kiểm tra lại điều kiện chờ, không bấm gửi thêm lần nữa.
Nếu Chrome đóng phiên hoặc mất kết nối renderer, case vẫn báo lỗi để điều tra môi trường;
không tự chạy lại toàn bộ case hay đổi kết quả thành PASS.
