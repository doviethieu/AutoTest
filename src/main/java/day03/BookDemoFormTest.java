package day03;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class BookDemoFormTest {

    private static final String URL =
            "https://saucelabs.com/request-demo";

    private static WebDriver driver;
    private static JavascriptExecutor js;

    // Locator dựa trên các trường đã tìm ở Day02.
    private static final By EMAIL =
            By.xpath("//input[contains(@id,'Business')]");

    private static final By COMPANY =
            By.xpath("//input[contains(@id,'Company')]");

    private static final By USE_CASE =
            By.xpath("//select[contains(@id,'Use')]");

    private static final By COMMENTS =
            By.xpath("//textarea[contains(@id,'Comment')]");

    private static final By REQUEST_BUTTON =
            By.xpath("//button[contains(translate(.,"
                    + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                    + "'abcdefghijklmnopqrstuvwxyz'),"
                    + "'request a demo')]");

    public static void main(String[] args) {
        driver = new ChromeDriver();
        js = (JavascriptExecutor) driver;

        driver.manage().window().maximize();
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(10));

        try {
            // TC01 - Dữ liệu hợp lệ.
            // Chỉ điền và kiểm tra dữ liệu, không gửi form thật.
            openForm();
            fillRequiredFields();
            comments().sendKeys("I would like to learn more about automation testing.");
            check("TC01 - Dữ liệu hợp lệ",
                    formIsValid());

            // TC02 - Để trống tất cả trường bắt buộc.
            openForm();
            clearRequiredFields();
            check("TC02 - Tất cả trường bắt buộc để trống",
                    !formIsValid());

            // TC03 - Business Email để trống.
            openForm();
            company().sendKeys("Example Company");
            check("TC03 - Business Email để trống",
                    !isValid(email()));

            // TC04 - Email sai định dạng.
            openForm();
            email().sendKeys("abc");
            company().sendKeys("Example Company");
            check("TC04 - Email sai định dạng",
                    !isValid(email()));

            // TC05 - Email thiếu ký tự @.
            openForm();
            email().sendKeys("userexample.com");
            company().sendKeys("Example Company");
            check("TC05 - Email thiếu @",
                    !isValid(email()));

            // TC06 - Email thiếu tên miền.
            openForm();
            email().sendKeys("user@");
            company().sendKeys("Example Company");
            check("TC06 - Email thiếu tên miền",
                    !isValid(email()));

            // TC07 - Company để trống.
            openForm();
            email().sendKeys("student@example.com");
            check("TC07 - Company để trống",
                    !isValid(company()));

            // TC08 - Email và Company cùng để trống.
            openForm();
            check("TC08 - Email và Company cùng để trống",
                    !formIsValid());

            // TC09 - Email có khoảng trắng.
            openForm();
            email().sendKeys(" user@example.com ");
            company().sendKeys("Example Company");
            check("TC09 - Email có khoảng trắng",
                    !isValid(email()));

            // TC10 - Company chỉ có khoảng trắng.
            // Ghi nhận hành vi trình duyệt; trường required có thể
            // xem dấu cách là một giá trị không rỗng.
            openForm();
            email().sendKeys("student@example.com");
            company().sendKeys("   ");
            System.out.println("TC10 - Company chỉ có khoảng trắng: "
                    + (isValid(company())
                    ? "trình duyệt xem là có giá trị"
                    : "trình duyệt từ chối"));

            // TC11 - Use Case để trống, nếu có lựa chọn trống.
            openForm();
            boolean useCaseCanBeEmpty = tryClearUseCase();
            if (useCaseCanBeEmpty) {
                check("TC11 - Use Case để trống",
                        !isValid(useCase()));
            } else {
                System.out.println(
                        "TC11 - Bỏ qua: danh sách không có lựa chọn trống.");
            }

            // TC12 - Kiểm tra danh sách Use Case.
            openForm();
            Select useCaseSelect = new Select(useCase());
            List<WebElement> options = useCaseSelect.getOptions();

            check("TC12 - Danh sách Use Case có lựa chọn",
                    !options.isEmpty());

            System.out.println("Các lựa chọn Use Case:");
            for (WebElement option : options) {
                System.out.println("- " + option.getText());
            }

            // TC13 - Comments để trống.
            openForm();
            fillRequiredFields();
            check("TC13 - Comments để trống là hợp lệ",
                    formIsValid());

            // TC14 - Comments có tiếng Việt và ký tự đặc biệt.
            openForm();
            fillRequiredFields();
            comments().sendKeys(
                    "Tôi muốn tìm hiểu Selenium & automation!");
            check("TC14 - Nhập tiếng Việt và ký tự đặc biệt",
                    comments().getAttribute("value")
                            .contains("automation"));

            // TC15 - Kiểm tra maxlength của Comments nếu có.
            openForm();
            String maxLength = comments().getAttribute("maxlength");
            if (maxLength == null || maxLength.isBlank()) {
                System.out.println(
                        "TC15 - Comments không khai báo maxlength.");
            } else {
                System.out.println(
                        "TC15 - maxlength của Comments là " + maxLength);
            }

            // TC16 - Sửa Company sau khi để trống.
            openForm();
            email().sendKeys("student@example.com");
            check("TC16a - Company trống thì form chưa hợp lệ",
                    !formIsValid());

            company().sendKeys("Example Company");
            check("TC16b - Nhập Company hợp lệ sau đó",
                    formIsValid());

            // TC17 - Có Email, Company, Use Case; Comments để trống.
            openForm();
            email().sendKeys("student@example.com");
            company().sendKeys("Example Company");
            check("TC17 - Use Case mặc định và Comments trống",
                    formIsValid());

            System.out.println("Đã chạy xong các kiểm tra.");

        } catch (Exception e) {
            System.out.println("Có lỗi khi chạy: " + e.getMessage());
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    private static void openForm() {
        driver.get(URL);
    }

    private static WebElement email() {
        return driver.findElement(EMAIL);
    }

    private static WebElement company() {
        return driver.findElement(COMPANY);
    }

    private static WebElement useCase() {
        return driver.findElement(USE_CASE);
    }

    private static WebElement comments() {
        return driver.findElement(COMMENTS);
    }

    private static void fillRequiredFields() {
        email().sendKeys("student@example.com");
        company().sendKeys("Example Company");
    }

    private static void clearRequiredFields() {
        email().clear();
        company().clear();
    }

    // Kiểm tra tính hợp lệ của toàn bộ form theo validation của trình duyệt.
    private static boolean formIsValid() {
        WebElement form = driver.findElement(
                By.xpath("//form[.//input[contains(@id,'Business')]]")
        );

        return (Boolean) js.executeScript(
                "return arguments[0].checkValidity();",
                form
        );
    }

    // Kiểm tra tính hợp lệ của một trường.
    private static boolean isValid(WebElement element) {
        return (Boolean) js.executeScript(
                "return arguments[0].checkValidity();",
                element
        );
    }

    // Thử chọn lựa chọn đầu tiên của Use Case.
    // Trả về true nếu lựa chọn đó có giá trị trống.
    private static boolean tryClearUseCase() {
        Select select = new Select(useCase());
        List<WebElement> options = select.getOptions();

        if (options.isEmpty()) {
            return false;
        }

        String firstOptionValue = options.get(0).getAttribute("value");

        if (firstOptionValue == null || firstOptionValue.isBlank()) {
            select.selectByIndex(0);
            return true;
        }

        return false;
    }

    private static void check(String testCaseName, boolean passed) {
        if (passed) {
            System.out.println("PASS: " + testCaseName);
        } else {
            System.out.println("FAIL: " + testCaseName);
        }
    }
}