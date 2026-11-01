package day03;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class FindByLinkText {
     public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Truy cập vào trang web
        driver.get("https://saucelabs.com/request-demo");

        WebElement element = driver.findElement(By.partialLinkText("Terms of Service"));
        element.click();

     }
}
