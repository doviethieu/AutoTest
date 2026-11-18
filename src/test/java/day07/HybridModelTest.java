package day07;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class HybridModelTest extends Hook {
    private static final String EXCEL_FILE = "day07Framework.xlsx";
    private static final String SHEET_NAME = "HybridData";
    private static final String BASE_URL = "https://www.saucedemo.com/";
    private static final String INVENTORY_URL = BASE_URL + "inventory.html";
    private LoginPage loginPage;
    private WebDriverWait wait;

    @BeforeMethod
    public void setupPage() {
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        loginPage = new LoginPage(driver);
        wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageUI.USERNAME_FIELD));
    }

    // DATA-DRIVEN: mỗi dòng Excel là một lần chạy test.
    @DataProvider(name = "hybridData", parallel = false)
    public Object[][] hybridData() {
        List<Map<String, String>> rows = ExcelUtils.readExcelData(EXCEL_FILE, SHEET_NAME);
        Set<String> ids = new HashSet<>();
        Object[][] data = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> row = rows.get(i);
            if (!row.keySet().containsAll(List.of("TestCaseID", "Username", "Password",
                    "ExpectedResult", "ExpectedMessage")))
                throw new IllegalArgumentException("Excel thiếu cột bắt buộc cho Hybrid Model");
            if (row.get("TestCaseID").isBlank() || !ids.add(row.get("TestCaseID")))
                throw new IllegalArgumentException("TestCaseID bị trống hoặc trùng ở dòng " + (i + 2));
            if (!"pass".equalsIgnoreCase(row.get("ExpectedResult").trim())
                    && !"fail".equalsIgnoreCase(row.get("ExpectedResult").trim()))
                throw new IllegalArgumentException("ExpectedResult phải là pass hoặc fail: " + row.get("TestCaseID"));
            if ("fail".equalsIgnoreCase(row.get("ExpectedResult").trim()) && row.get("ExpectedMessage").isBlank())
                throw new IllegalArgumentException("Thiếu ExpectedMessage cho case lỗi: " + row.get("TestCaseID"));
            data[i][0] = row;
        }
        return data;
    }

    @Test(dataProvider = "hybridData", description = "Hybrid Model - Data Driven kết hợp POM")
    public void loginHybrid(Map<String, String> testData) {
        String username = testData.get("Username");
        String password = testData.get("Password");
        String expectedResult = testData.get("ExpectedResult").trim();
        String expectedMessage = testData.get("ExpectedMessage");
        Reporter.getCurrentTestResult().setAttribute("report.title",
                testData.get("TestCaseID") + " - Hybrid Login (" + expectedResult + ")");
        Reporter.log("=== Chạy " + testData.get("TestCaseID") + " với user [" + username + "]", true);

        // POM: thao tác đăng nhập qua Page Object.
        loginPage.login(username, password);

        // POM: lấy thông tin trên trang để kiểm tra kết quả theo Excel.
        if ("pass".equalsIgnoreCase(expectedResult)) {
            wait.until(ExpectedConditions.urlToBe(INVENTORY_URL));
            wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageUI.APP_LOGO));
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(LoginPageUI.INVENTORY_ITEM, 0));
            Assert.assertEquals(driver.getCurrentUrl(), INVENTORY_URL, "Login thất bại ngoài mong đợi!");
            Assert.assertTrue(loginPage.isLogoDisplayed(), "Logo không hiển thị!");
            Assert.assertTrue(loginPage.getInventoryItemCount() > 0, "Không có sản phẩm nào!");
        } else if ("fail".equalsIgnoreCase(expectedResult)) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageUI.ERROR_MESSAGE));
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Không hiển thị thông báo lỗi!");
            Assert.assertTrue(loginPage.getErrorMessage().contains(expectedMessage),
                    "Thông báo lỗi không đúng: " + loginPage.getErrorMessage());
            Assert.assertEquals(driver.getCurrentUrl(), BASE_URL, "Đăng nhập sai phải ở lại trang Login!");
        } else {
            Assert.fail("ExpectedResult trong Excel phải là 'pass' hoặc 'fail'");
        }
    }
}
