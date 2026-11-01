package day03;

import java.util.ArrayList;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;

public class SwitchTabsExample {
    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        driver.get("https://saucelabs.com/request-demo");

        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());

        // Selenium mở tab mới và tự chuyển sang tab đó
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get("https://google.com");

        Thread.sleep(5000);

        // Quay lại tab đầu tiên
        driver.switchTo().window(tabs.get(0));

        Thread.sleep(5000);
        driver.quit();
    }
}