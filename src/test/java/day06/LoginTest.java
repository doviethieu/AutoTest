package day06;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import action.LoginPage;

public class LoginTest {
    private static final String BASE_URL = "https://www.saucedemo.com/";
    private static final String INVENTORY_URL = BASE_URL + "inventory.html";
    private static final String PAGE_TITLE = "Swag Labs";

    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        loginPage = new LoginPage(driver);
        System.out.println("BeforeClass được chạy");
        // driver.get(BASE_URL); // Bật dòng này nếu muốn mở URL ở BeforeClass.
    }

    @BeforeMethod
    public void openLoginPage() {
        driver.get(BASE_URL); // Tắt dòng này khi chuyển lệnh mở URL lên BeforeClass.
        System.out.println("BeforeMethod được chạy");
    }

    @Test
    public void testLoginFunctionality() {
        // Kiểm tra trang đăng nhập.
        Assert.assertEquals(driver.getTitle(), PAGE_TITLE, "Page title is incorrect!");
        Assert.assertEquals(driver.getCurrentUrl(), BASE_URL, "Page URL is incorrect!");

        loginPage.login("standard_user", "secret_sauce");

        // Kiểm tra trang sản phẩm sau khi đăng nhập.
        Assert.assertEquals(driver.getCurrentUrl(), INVENTORY_URL,
                "Login failed - Inventory page not loaded!");
        Assert.assertTrue(loginPage.isLogoDisplayed(),
                "App logo is not displayed on inventory page!");
        Assert.assertTrue(loginPage.getInventoryItemCount() > 0,
                "No products displayed on inventory page!");
    }

    @Test
    public void testLoginFunctionalityAgain() {
        // Lặp lại cùng kịch bản để quan sát trang bắt đầu của test tiếp theo.
        Assert.assertEquals(driver.getTitle(), PAGE_TITLE, "Page title is incorrect!");
        Assert.assertEquals(driver.getCurrentUrl(), BASE_URL, "Page URL is incorrect!");

        loginPage.login("standard_user", "secret_sauce");

        Assert.assertEquals(driver.getCurrentUrl(), INVENTORY_URL,
                "Login failed - Inventory page not loaded!");
        Assert.assertTrue(loginPage.isLogoDisplayed(),
                "App logo is not displayed on inventory page!");
        Assert.assertTrue(loginPage.getInventoryItemCount() > 0,
                "No products displayed on inventory page!");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
