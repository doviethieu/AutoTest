package day03;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class GetCurrentUrl {
    static WebDriver driver = null;
    public static void main(String[] args) throws InterruptedException {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://saucelabs.com/");
        Thread.sleep(5000);
        driver.navigate().to("https://saucelabs.com/request-demo");
        Thread.sleep(5000);
        driver.navigate().back();
        Thread.sleep(5000);
        driver.navigate().forward();
        Thread.sleep(5000);
        String currrentUrl = driver.getCurrentUrl();
        System.out.println("Current URL: " + currrentUrl);
        driver.close();
    }

}
