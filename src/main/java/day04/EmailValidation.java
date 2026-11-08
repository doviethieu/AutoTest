package day04;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class EmailValidation {
 
    public static void main(String[] args) throws InterruptedException {

        // Khởi tạo WebDriver
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            // Mở trang web
            driver.get("https://saucelabs.com/request-demo");

            // Lấy phần tử dropdown "Use Case" bằng id
            WebElement useCaseDropdown = driver.findElement(By.id("Use-Case"));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String expectedValue = "Visual Testing";
            js.executeScript(
                    "arguments[0].value = arguments[1];" +
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                    useCaseDropdown, expectedValue);
            Thread.sleep(3000); // để quan sát trên màn hình
            // Kiểm tra giá trị đã được chọn
            String selectedValue = useCaseDropdown.getAttribute("value");
            System.out.println("Selected value from droplist: " + selectedValue);
            driver.quit();

    }
}