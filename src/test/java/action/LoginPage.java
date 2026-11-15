package action;

import org.openqa.selenium.WebDriver;

import UI.LoginPageUI;

public class LoginPage {
    private final WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void enterUsername(String username) {
        driver.findElement(LoginPageUI.USERNAME_FIELD).sendKeys(username);
    }

    public void enterPassword(String password) {
        driver.findElement(LoginPageUI.PASSWORD_FIELD).sendKeys(password);
    }

    public void clickLogin() {
        driver.findElement(LoginPageUI.LOGIN_BUTTON).click();
    }

    // Gộp các thao tác đăng nhập để test gọi lại.
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public boolean isErrorMessageDisplayed() {
        return driver.findElement(LoginPageUI.ERROR_MESSAGE).isDisplayed();
    }

    public String getErrorMessage() {
        return driver.findElement(LoginPageUI.ERROR_MESSAGE).getText();
    }

    public boolean isLogoDisplayed() {
        return driver.findElement(LoginPageUI.APP_LOGO).isDisplayed();
    }

    public int getInventoryItemCount() {
        return driver.findElements(LoginPageUI.INVENTORY_ITEM).size();
    }
}
