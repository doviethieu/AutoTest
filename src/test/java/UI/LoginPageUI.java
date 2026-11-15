package UI;

import org.openqa.selenium.By;

public class LoginPageUI {
    public static final By USERNAME_FIELD = By.id("user-name");
    public static final By PASSWORD_FIELD = By.id("password");
    public static final By LOGIN_BUTTON   = By.id("login-button");
    public static final By ERROR_MESSAGE  = By.xpath("//h3[@data-test='error']");
    public static final By APP_LOGO       = By.className("app_logo");
    public static final By INVENTORY_ITEM = By.className("inventory_item");
}
