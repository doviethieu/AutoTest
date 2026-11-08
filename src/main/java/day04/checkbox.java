package day04;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class checkbox {
    private static final String URL =
            "https://demo.guru99.com/test/radio.html";

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

            Select select = new Select(useCase());

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
