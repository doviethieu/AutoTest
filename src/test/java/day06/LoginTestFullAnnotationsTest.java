package day06;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import action.LoginPage;

public class LoginTestFullAnnotationsTest {
    private static final String BASE_URL = "https://www.saucedemo.com/";
    private static final String INVENTORY_URL = BASE_URL + "inventory.html";

    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeSuite
    public void beforeSuite() {
        System.out.println("=== @BeforeSuite: Chạy đầu tiên, trước tất cả các test trong suite ===");
    }

    @BeforeTest
    public void beforeTest() {
        System.out.println("=== @BeforeTest: Chạy trước các class trong cùng 1 thẻ <test> trong xml ===");
    }

    @BeforeClass //mở trình duyệt 1 lần
    public void beforeClass() {
        System.out.println("=== @BeforeClass: Khởi tạo Driver - Chạy trước các method trong class này ===");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        loginPage = new LoginPage(driver);
    }

    @BeforeMethod
    public void beforeMethod() {
        System.out.println("=== @BeforeMethod: Mở trang web - Chạy trước mỗi test case (@Test) ===");
        driver.get(BASE_URL);
    }

    @Test
    public void testLoginValidUser() {
        System.out.println("--- @Test: Thực hiện test login với user hợp lệ ---");
        loginPage.login("standard_user", "secret_sauce");
        Assert.assertEquals(driver.getCurrentUrl(), INVENTORY_URL, "Login failed!");
    }

    @Test
    public void testLoginInvalidUser() {
        System.out.println("--- @Test: Thực hiện test login với user không hợp lệ ---");
        loginPage.login("locked_out_user", "secret_sauce");
        // Không kiểm tra URL vì user này bị lock, chỉ để demo annotation
    }

    @AfterMethod
    public void afterMethod() {
        System.out.println("=== @AfterMethod: Chạy sau mỗi test case (@Test) ===");
    }

    @AfterClass
    public void afterClass() {
        System.out.println("=== @AfterClass: Đóng Driver - Chạy sau tất cả các method trong class này ===");
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterTest
    public void afterTest() {
        System.out.println("=== @AfterTest: Chạy sau các class trong cùng 1 thẻ <test> trong xml ===");
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("=== @AfterSuite: Chạy cuối cùng, sau tất cả các test trong suite ===");
    }
}
