package day07;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import UI.LoginPageUI;
import action.LoginPage;
import utils.ExcelUtils;
import utils.Hook;

public class DataDrivenTest extends Hook {
    private final String excelFilePath = "dataLogin.xlsx";
    private LoginPage loginPage;
    private WebDriverWait wait;

    @BeforeMethod
    public void setupPage() {
        // Hook đã mở trình duyệt; phần kiểm tra dùng explicit wait.
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        loginPage = new LoginPage(driver);
        wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageUI.USERNAME_FIELD));
    }

    @DataProvider(name = "loginData", parallel = false)
    public Object[][] loginData() {
        List<Map<String, String>> excelData = ExcelUtils.readExcelData(excelFilePath, "Sheet1");
        Object[][] data = new Object[excelData.size()][1];
        for (int i = 0; i < excelData.size(); i++) {
            Map<String, String> row = excelData.get(i);
            if (!row.keySet().containsAll(List.of("Username", "Password", "ExpectedResult")))
                throw new IllegalArgumentException("Excel phải có các cột Username, Password, ExpectedResult");
            data[i][0] = row;
        }
        return data;
    }

    @Test(dataProvider = "loginData", description = "Đăng nhập với dữ liệu Excel")
    public void testLogin(Map<String, String> rowData) {
        String username = rowData.get("Username");
        String password = rowData.get("Password");
        String expectedResult = rowData.get("ExpectedResult").trim();
        Reporter.log("Username: " + username + " | ExpectedResult: " + expectedResult, true);

        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();

        if ("pass".equalsIgnoreCase(expectedResult)) {
            wait.until(ExpectedConditions.urlToBe("https://www.saucedemo.com/inventory.html"));
            Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/inventory.html",
                    "Login failed unexpectedly!");
        } else if ("fail".equalsIgnoreCase(expectedResult)) {
            WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageUI.ERROR_MESSAGE));
            Assert.assertTrue(error.isDisplayed(), "Expected error message not displayed!");
            Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/",
                    "Đăng nhập sai phải ở lại trang Login!");
        } else {
            Assert.fail("ExpectedResult trong Excel phải là 'pass' hoặc 'fail'");
        }
    }
}
