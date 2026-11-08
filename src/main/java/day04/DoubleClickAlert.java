package day04;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

public class DoubleClickAlert {

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();
            driver.manage().timeouts()
                    .implicitlyWait(Duration.ofSeconds(10));

            // Mở trang có nút double-click.
            driver.get(
                    "https://demo.guru99.com/test/simple_context_menu.html"
            );

            // Tìm nút theo phần chữ hiển thị trên nút.
            WebElement button = driver.findElement(
                    By.xpath("//button[contains(text(),"
                            + "'Double-Click Me To See Alert')]")
            );

            Actions actions = new Actions(driver);

            // Double-click vào nút.
            actions.doubleClick(button).perform();

            Alert alert = driver.switchTo().alert();

            System.out.println("Nội dung alert: " + alert.getText());

            alert.accept();

        } catch (Exception e) {
            System.out.println("Có lỗi khi chạy: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng trình duyệt khi hoàn thành.
            driver.quit();
        }
    }
}