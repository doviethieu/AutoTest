package UI;

import java.util.Map;
import org.openqa.selenium.By;

public final class BookDemoUI {
    public static final String URL = "https://saucelabs.com/request-demo";
    public static final By EMAIL = By.id("Business-Email");
    public static final By COMPANY = By.id("Company");
    public static final By STATE = By.id("State");
    public static final By COUNTRY = By.id("Country");
    public static final By USE_CASE = By.id("Use-Case");
    public static final By COMMENTS = By.id("Comments");
    public static final By SUBMIT = By.xpath("//button[contains(translate(normalize-space(.),"
            + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'request a demo')]");
    public static final By EMAIL_ERROR = By.xpath("//*[contains(text(),'Please enter a valid business email')]");
    public static final By SUBMIT_ERROR = By.cssSelector(".w-form-fail");
    public static final By THANK_YOU = By.xpath("//*[normalize-space()='Thanks for reaching out!']");
    public static final Map<String, By> FIELDS = Map.of(
            "Business Email", EMAIL, "First name", By.name("First Name"),
            "Last name", By.name("Last Name"), "Company", COMPANY,
            "Phone number", By.id("Phone"), "Country", COUNTRY, "State", STATE,
            "Use Case", USE_CASE, "Comments", COMMENTS);

    private BookDemoUI() { }

    public static By field(String name) {
        By locator = FIELDS.get(name);
        if (locator == null) throw new IllegalArgumentException("Target Field không hợp lệ: " + name);
        return locator;
    }
}
