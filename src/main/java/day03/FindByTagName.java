package day03;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class FindByTagName {
    public static void main(String[] args) {
        // Khởi tạo WebDriver
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Truy cập vào trang web
        driver.get("https://saucelabs.com/request-demo");

        // Tìm thẻ `<h1>` và in nội dung
        WebElement header = driver.findElement(By.tagName("h1"));
        System.out.println("Header text: " + header.getText());

        // Tìm tất cả các thẻ `<p>` và in nội dung
        List<WebElement> paragraphs = driver.findElements(By.tagName("p"));
        System.out.println("\nParagraphs:");
        for (WebElement paragraph : paragraphs) {
            System.out.println(paragraph.getText());
        }

        // Tìm tất cả các thẻ `<button>` và in nội dung
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        System.out.println("\nButtons:");
        for (WebElement button : buttons) {
            System.out.println(button.getText());
        }
        // Đóng trình duyệt
        driver.quit();
    }
} 
