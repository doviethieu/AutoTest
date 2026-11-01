package day03;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class FindElement {
    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://saucelabs.com/request-demo");

        WebElement element1 = driver.findElement(By.name("Company"));
        element1.sendKeys("Techcombank");
        
        WebElement element2 = driver.findElement(By.className("form-button"));
        element2.click();


        driver.quit();

    }
}