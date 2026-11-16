package day06;

import static UI.BookDemoUI.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.support.ui.*;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.*;
import data.BookDemoData;
import data.BookDemoData.Case;
import reporting.TestLog;

public class BookDemoTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    @DataProvider(name = "bookDemoCases", parallel = false)
    public Object[][] cases() throws IOException {
        String check = System.getProperty("bookDemo.check", "").trim();
        String inputLength = System.getProperty("bookDemo.inputLength", "").trim();
        Object[][] cases = Arrays.stream(BookDemoData.read())
                .filter(row -> check.isEmpty() || ((Case) row[0]).get("Check").equals(check))
                .filter(row -> inputLength.isEmpty() || ((Case) row[0]).get("Input Length").equals(inputLength))
                .toArray(Object[][]::new);
        if (cases.length == 0) throw new IllegalArgumentException("Không có dữ liệu khớp bộ lọc Check/Input Length");
        return cases;
    }

    @BeforeMethod
    public void openForm() throws IOException {
        ChromeDriverService.Builder service = new ChromeDriverService.Builder();
        if (Boolean.getBoolean("bookDemo.debug")) {
            Path log = Path.of("target", "browser-logs", "chromedriver-" + System.currentTimeMillis() + ".log");
            Files.createDirectories(log.getParent());
            service.withLogFile(log.toFile()).withVerbose(true);
            System.out.println("ChromeDriver log: " + log.toAbsolutePath());
        }
        driver = new ChromeDriver(service.build());
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        js = (JavascriptExecutor) driver;
        driver.manage().window().maximize();
        driver.get(URL);
        visible(EMAIL);
    }

    @Test(dataProvider = "bookDemoCases", description = "Kiểm tra Request a demo từ Excel")
    public void testBookDemo(Case data) {
        Reporter.getCurrentTestResult().setAttribute("report.title", data.toString());
        TestLog.step(data.toString() + " | Check = " + data.get("Check"));
        fill(data);
        By target = data.get("Target Field").isBlank() ? EMAIL : field(data.get("Target Field"));
        // Mã TC không tham gia chọn logic: mỗi Check dùng được cho nhiều dòng Excel.
        switch (data.get("Check")) {
            case "INITIAL_FORM" -> initialForm(data);
            case "REQUIRED_FIELDS" -> {
                for (String name : List.of("Business Email", "First name", "Last name", "Company",
                        "Phone number", "Country", "Use Case"))
                    TestLog.assertEquals(required(visible(field(name))), data.expectedBoolean(), name + " bắt buộc");
                TestLog.assertTrue(!required(visible(COMMENTS)), "Comments không bắt buộc");
                stateHidden();
                validForm();
            }
            case "SUBMIT" -> submit(data);
            case "EMAIL_VALIDITY" -> {
                String validity = data.get("Validity");
                if (!List.of("valueMissing", "typeMismatch").contains(validity))
                    throw new IllegalArgumentException("Validity không hợp lệ: " + validity);
                TestLog.assertEquals(flag(visible(EMAIL), validity), data.expectedBoolean(), "Email validity." + validity);
                TestLog.step("validationMessage: " + visible(EMAIL).getDomProperty("validationMessage"));
            }
            case "BUSINESS_EMAIL" -> {
                wait.until(d -> displayed(EMAIL_ERROR));
                wait.until(d -> !visible(SUBMIT).isEnabled());
                TestLog.assertEquals(visible(SUBMIT).isEnabled(), data.expectedBoolean(), "Nút gửi được bật");
                TestLog.assertTrue(displayed(EMAIL_ERROR), "Hiện lỗi business email");
            }
            case "MISSING" -> missing(data, target);
            case "DEFAULT_SELECT" -> {
                TestLog.assertEquals(selected(target), data.get("Expected Result"), "Lựa chọn mặc định");
                validForm();
            }
            case "OPTIONS" -> {
                Select select = new Select(visible(target));
                List<String> options = select.getOptions().stream()
                        .filter(WebElement::isEnabled).map(e -> e.getDomAttribute("value")).filter(v -> v != null && !v.isBlank()).toList();
                TestLog.assertTrue(!options.isEmpty(), "Có lựa chọn trong danh sách");
                for (String option : options) {
                    select.selectByValue(option);
                    TestLog.assertEquals(select.getFirstSelectedOption().getDomAttribute("value").equals(option),
                            data.expectedBoolean(), "Chọn option: " + option);
                }
                validForm();
            }
            case "OPTIONAL" -> {
                TestLog.assertEquals(required(visible(target)), data.expectedBoolean(), "Trường có bắt buộc không");
                TestLog.assertEquals(visible(target).getDomProperty("value"), "", "Dữ liệu đang trống");
                validForm();
            }
            case "TEXT" -> {
                TestLog.assertEquals(visible(target).getDomProperty("value"), data.get("Expected Result"), "Nội dung nhập được giữ nguyên");
                validForm();
            }
            case "LENGTH" -> length(data, target);
            case "RECOVERY" -> {
                WebElement input = visible(target);
                input.clear();
                TestLog.assertTrue(flag(input, "valueMissing"), "Trường không hợp lệ khi xóa dữ liệu");
                input.sendKeys(data.get(data.get("Target Field")));
                TestLog.assertEquals(valid(input), data.expectedBoolean(), "Hợp lệ sau khi nhập lại");
                validForm();
            }
            case "SELECT_RECOVERY" -> {
                Select select = new Select(visible(target));
                TestLog.assertTrue(flag(visible(target), "valueMissing"), "Dropdown chưa chọn gây lỗi required");
                select.selectByVisibleText(data.get(data.get("Target Field")));
                TestLog.assertEquals(valid(visible(target)), data.expectedBoolean(), "Dropdown hợp lệ sau chọn lại");
                TestLog.assertEquals(selected(target), data.get(data.get("Target Field")), "Giữ đúng lựa chọn");
                validForm();
            }
            case "EMAIL_CHANGE" -> changeEmail(data);
            case "SELECT" -> {
                TestLog.assertEquals(selected(target), data.get("Expected Result"), "Lựa chọn đúng Excel");
                stateHidden();
                validForm();
            }
            case "VALID" -> {
                TestLog.assertEquals(valid(visible(target)), data.expectedBoolean(), "State hợp lệ");
                TestLog.assertEquals(selected(target), data.get("State"), "State đúng dữ liệu Excel");
                validForm();
            }
            case "COUNTRY_CHANGE" -> {
                TestLog.assertEquals(selected(STATE), data.get("State"), "State đã được chọn trước khi đổi Country");
                new Select(visible(COUNTRY)).selectByVisibleText(data.get("Next Country"));
                wait.until(ExpectedConditions.invisibilityOfElementLocated(STATE));
                TestLog.assertEquals(displayed(STATE), data.expectedBoolean(), "State hiển thị sau đổi Country");
                stateHidden();
                TestLog.assertEquals(selected(COUNTRY), data.get("Next Country"), "Country sau thay đổi");
                validForm();
            }
            case "COUNTRY_SHOW", "COUNTRY_ROUNDTRIP" -> {
                if (data.get("Check").equals("COUNTRY_ROUNDTRIP")) {
                    TestLog.assertEquals(selected(STATE), data.get("State"), "State trước khi đổi Country");
                    new Select(visible(COUNTRY)).selectByVisibleText(data.get("Next Country"));
                    stateHidden();
                    new Select(visible(COUNTRY)).selectByVisibleText(data.get("Country"));
                } else {
                    stateHidden();
                    new Select(visible(COUNTRY)).selectByVisibleText(data.get("Next Country"));
                }
                WebElement state = visible(STATE);
                TestLog.assertEquals(state.isDisplayed(), data.expectedBoolean(), "State xuất hiện");
                TestLog.assertTrue(required(state), "State bắt buộc khi chọn United States");
                // Trang tự xóa State khi Country khác US; không có option trống để chọn lại.
                TestLog.assertTrue(flag(state, "valueMissing"), "State trống chặn form");
                new Select(state).selectByVisibleText(data.get("State"));
                TestLog.assertEquals(selected(STATE), data.get("State"), "Chọn lại State thành công");
                validForm();
            }
            case "REFRESH" -> {
                validForm();
                driver.navigate().refresh();
                visible(EMAIL);
                initialForm(data);
            }
            default -> throw new IllegalArgumentException("Check không hỗ trợ: " + data.get("Check"));
        }
    }

    private void fill(Case data) {
        if (data.get("Check").equals("INITIAL_FORM")) return;
        enter(EMAIL, data.get("Business Email"));
        visible(COMPANY).click(); // Blur email để form mở rộng.
        if (List.of("EMAIL_VALIDITY", "BUSINESS_EMAIL", "EMAIL_CHANGE").contains(data.get("Check"))) return;
        fillDetails(data);
    }

    private void fillDetails(Case data) {
        for (String name : List.of("First name", "Last name", "Company", "Phone number"))
            enter(field(name), data.get(name));
        // Case sửa dropdown bắt đầu ở trạng thái chưa chọn, không giả lập bằng JavaScript.
        if (!data.get("Country").isBlank() && !leaveUnselected(data, "Country"))
            new Select(visible(COUNTRY)).selectByVisibleText(data.get("Country"));
        if (data.get("Country").equals("United States")) {
            visible(STATE);
            if (!data.get("State").isBlank() && !leaveUnselected(data, "State"))
                new Select(visible(STATE)).selectByVisibleText(data.get("State"));
        }
        if (!data.get("Use Case").isBlank()) new Select(visible(USE_CASE)).selectByVisibleText(data.get("Use Case"));
        enter(COMMENTS, data.get("Comments"));
    }

    private boolean leaveUnselected(Case data, String name) {
        return data.get("Check").equals("SELECT_RECOVERY") && data.get("Target Field").equals(name);
    }

    private void initialForm(Case data) {
        for (By locator : List.of(EMAIL, COMPANY, USE_CASE, COMMENTS, SUBMIT))
            TestLog.assertTrue(visible(locator).isDisplayed(), "Trường ban đầu hiển thị: " + locator);
        TestLog.assertEquals(visible(EMAIL).getDomProperty("value"), "", "Email ban đầu trống");
        TestLog.assertEquals(visible(COMPANY).getDomProperty("value"), "", "Company ban đầu trống");
        TestLog.assertEquals(visible(COMMENTS).getDomProperty("value"), "", "Comments ban đầu trống");
        TestLog.assertEquals(selected(USE_CASE), data.get("Expected Result"), "Use Case mặc định");
        for (String name : List.of("First name", "Last name", "Phone number", "Country")) {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(field(name)));
            TestLog.assertTrue(!displayed(field(name)), name + " chưa hiển thị khi chưa nhập email");
        }
        stateHidden();
        TestLog.assertTrue(!valid(visible(EMAIL)), "Email trống không hợp lệ");
    }

    private void changeEmail(Case data) {
        // Xác nhận trạng thái trước và sau sửa để tránh PASS khi lỗi chưa từng xuất hiện.
        switch (data.get("Initial Email State")) {
            case "FORMAT_ERROR" -> TestLog.assertTrue(flag(visible(EMAIL), "typeMismatch"), "Email ban đầu sai định dạng");
            case "ACCEPTED" -> {
                visible(field("First name"));
                wait.until(d -> !displayed(EMAIL_ERROR) && visible(SUBMIT).isEnabled());
                TestLog.assertTrue(!displayed(EMAIL_ERROR), "Email ban đầu được chấp nhận");
            }
            case "BLOCKED" -> {
                wait.until(d -> displayed(EMAIL_ERROR) && !visible(SUBMIT).isEnabled());
                TestLog.assertTrue(displayed(EMAIL_ERROR), "Email ban đầu bị từ chối");
            }
            default -> throw new IllegalArgumentException("Initial Email State không hợp lệ");
        }
        enter(EMAIL, data.get("Next Email"));
        visible(COMPANY).click();
        wait.until(d -> visible(SUBMIT).isEnabled() == data.expectedBoolean()
                && displayed(EMAIL_ERROR) != data.expectedBoolean());
        TestLog.assertEquals(visible(SUBMIT).isEnabled(), data.expectedBoolean(), "Trạng thái nút sau đổi email");
        TestLog.assertEquals(displayed(EMAIL_ERROR), !data.expectedBoolean(), "Trạng thái lỗi sau đổi email");
        if (data.expectedBoolean()) {
            visible(field("First name"));
            fillDetails(data);
            validForm();
        }
    }

    private void stateHidden() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(STATE));
        TestLog.assertTrue(!displayed(STATE), "State được ẩn");
        TestLog.assertTrue(driver.findElements(STATE).stream().noneMatch(e -> e.isEnabled() && required(e)),
                "State ẩn không chặn validation");
    }

    private void missing(Case data, By target) {
        WebElement input = visible(target);
        TestLog.assertTrue(flag(input, "valueMissing"), "Trường mục tiêu thực sự đang thiếu dữ liệu");
        TestLog.assertEquals(flag(input, "valueMissing"), data.expectedBoolean(), "Lỗi thiếu " + data.get("Target Field"));
        // Chỉ trường mục tiêu được sai; không cho PASS vì một trường khác cũng trống.
        boolean othersValid = (Boolean) js.executeScript(
                "return Array.from(arguments[0].elements).filter(e=>e!==arguments[1]&&e.willValidate).every(e=>e.validity.valid)", form(), input);
        TestLog.assertTrue(othersValid, "Các trường còn lại đều hợp lệ");
        if (data.get("Submit With").equals("ENTER")) {
            visible(COMPANY).sendKeys(Keys.ENTER);
        } else {
            wait.until(ExpectedConditions.elementToBeClickable(SUBMIT)).click();
        }
        TestLog.assertTrue((Boolean) js.executeScript("return document.activeElement===arguments[0]", input),
                "Trình duyệt tập trung vào đúng trường bị thiếu");
        TestLog.assertTrue(!input.getDomProperty("validationMessage").isBlank(), "Có thông báo validation");
        TestLog.assertEquals(driver.getCurrentUrl(), URL, "Form chưa chuyển sang trang cảm ơn");
    }

    private void length(Case data, By target) {
        WebElement input = visible(target);
        String limit = input.getDomAttribute("maxlength");
        if (limit == null) throw new SkipException("Trường không khai báo maxlength; case không áp dụng");
        int requested = Integer.parseInt(data.get("Input Length"));
        int expected = Integer.parseInt(data.get("Expected Result"));
        int maxLength = Integer.parseInt(limit);
        TestLog.assertTrue(requested >= 0 && requested <= 10000, "Độ dài dữ liệu test trong phạm vi 0..10000");
        TestLog.assertTrue(maxLength >= 0, "maxlength được khai báo hợp lệ");
        input.clear();
        enterBoundaryText(input, requested, maxLength);
        TestLog.assertEquals(input.getDomProperty("value").length(), expected, "Số ký tự được chấp nhận");
        validForm();
    }

    private void enterBoundaryText(WebElement input, int count, int maxLength) {
        int prefixLength = Math.min(Math.max(0, count - 1), Math.max(0, maxLength - 1));
        // Chỉ chuẩn bị chuỗi dưới giới hạn; ký tự chạm/vượt biên luôn nhập bằng sendKeys.
        if (prefixLength > 0) {
            js.executeScript("arguments[0].value = arguments[1];"
                    + "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));"
                    + "arguments[0].focus();"
                    + "arguments[0].setSelectionRange(arguments[1].length, arguments[1].length);",
                    input, "A".repeat(prefixLength));
            TestLog.assertEquals(input.getDomProperty("value").length(), prefixLength, "Độ dài chuỗi chuẩn bị dưới giới hạn");
        }
        TestLog.step("Chuẩn bị " + prefixLength + " ký tự, nhập thêm " + (count - prefixLength) + " ký tự bằng sendKeys");
        enterLongText(input, count - prefixLength);
    }

    private void enterLongText(WebElement input, int count) {
        // Chia lệnh nhập dài; vẫn để trình duyệt xử lý maxlength qua thao tác gõ.
        for (int offset = 0; offset < count; offset += 250)
            input.sendKeys("A".repeat(Math.min(250, count - offset)));
    }

    private void submit(Case data) {
        validForm();
        TestLog.step("Bấm Request a demo để gửi yêu cầu thật");
        wait.until(ExpectedConditions.elementToBeClickable(SUBMIT)).click();
        awaitSubmission(data.get("Expected Result"));
        TestLog.assertEquals(driver.getCurrentUrl(), data.get("Expected Result"), "URL xác nhận gửi thành công");
        TestLog.assertTrue(visible(THANK_YOU).isDisplayed(), "Hiện Thanks for reaching out!");
    }

    private void awaitSubmission(String expectedUrl) {
        // DOM cũ có thể biến mất giữa lúc tìm và kiểm tra phần tử khi chuyển trang.
        wait.until(ExpectedConditions.refreshed(
                d -> d.getCurrentUrl().equals(expectedUrl) || displayed(SUBMIT_ERROR)));
    }

    private void validForm() {
        TestLog.assertTrue(valid(form()), "Toàn bộ form hợp lệ theo HTML validation");
        TestLog.assertTrue(visible(SUBMIT).isEnabled(), "Nút gửi được bật");
        TestLog.assertTrue(!displayed(EMAIL_ERROR), "Không có lỗi business email");
    }

    private WebElement form() { return visible(EMAIL).findElement(By.xpath("./ancestor::form")); }
    private WebElement visible(By locator) { return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)); }
    private boolean displayed(By locator) { return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed); }
    private void enter(By locator, String text) {
        WebElement element = visible(locator);
        element.clear();
        if (!text.isEmpty()) element.sendKeys(text);
    }
    private String selected(By locator) { return new Select(visible(locator)).getFirstSelectedOption().getText().trim(); }
    private boolean required(WebElement element) { return (Boolean) js.executeScript("return arguments[0].required", element); }
    private boolean valid(WebElement element) { return (Boolean) js.executeScript("return arguments[0].checkValidity()", element); }
    private boolean flag(WebElement element, String state) { return (Boolean) js.executeScript("return arguments[0].validity[arguments[1]]", element, state); }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
