package day06;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import UI.LoginPageUI;
import action.LoginPage;

public class LoginTestVV {
    private static final String BASE_URL = "https://www.saucedemo.com/";
    private static final String INVENTORY_URL = BASE_URL + "inventory.html";
    private static final String PAGE_TITLE = "Swag Labs";

    private WebDriver driver;
    private WebDriverWait wait;
    private LoginPage loginPage;

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        loginPage = new LoginPage(driver);
    }

    @BeforeMethod
    public void openLoginPage() {
        // Mở lại trang đăng nhập trước mỗi test.
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                LoginPageUI.USERNAME_FIELD));
    }

    @Test(description = "TC01 - Happy case: đăng nhập đúng")
    public void testLoginSuccessfully() {
        Assert.assertEquals(driver.getTitle(), PAGE_TITLE, "Page title is incorrect!");
        Assert.assertEquals(driver.getCurrentUrl(), BASE_URL, "Page URL is incorrect!");

        loginPage.login("standard_user", "secret_sauce");

        wait.until(ExpectedConditions.urlToBe(INVENTORY_URL));
        wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageUI.APP_LOGO));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                LoginPageUI.INVENTORY_ITEM, 0));

        Assert.assertEquals(driver.getCurrentUrl(), INVENTORY_URL,
                "Login failed - Inventory page not loaded!");
        Assert.assertTrue(loginPage.isLogoDisplayed(),
                "App logo is not displayed on inventory page!");
        Assert.assertTrue(loginPage.getInventoryItemCount() > 0,
                "No products displayed on inventory page!");
    }

    @Test(description = "TC02 - Unhappy case: sai mật khẩu")
    public void testLoginWithWrongPassword() {
        loginPage.login("standard_user", "wrong_password");

        assertLoginError(
                "Epic sadface: Username and password do not match any user in this service");
    }

    @Test(description = "TC03 - Unhappy case: bỏ trống username")
    public void testLoginWithEmptyUsername() {
        loginPage.login("", "secret_sauce");

        assertLoginError("Epic sadface: Username is required");
    }

    @Test(description = "TC04 - Unhappy case: bỏ trống password")
    public void testLoginWithEmptyPassword() {
        loginPage.login("standard_user", "");

        assertLoginError("Epic sadface: Password is required");
    }

    // Dùng chung cho ba case lỗi: ở lại trang login và hiện đúng thông báo.
    private void assertLoginError(String expectedMessage) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageUI.ERROR_MESSAGE));

        Assert.assertEquals(driver.getCurrentUrl(), BASE_URL,
                "Should stay on login page when login fails!");
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Login error message is not displayed!");
        Assert.assertEquals(loginPage.getErrorMessage(), expectedMessage,
                "Login error message is incorrect!");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
