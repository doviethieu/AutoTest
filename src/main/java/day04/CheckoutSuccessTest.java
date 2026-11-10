package day04;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutSuccessTest {

    private static final String URL = "https://www.saucedemo.com/";
    private static final BigDecimal TAX_RATE = new BigDecimal("0.08");

    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By INVENTORY_LIST = By.className("inventory_list");
    private static final By SORT_DROPDOWN =
            By.className("product_sort_container");
    private static final By PRODUCT_CARDS = By.className("inventory_item");
    private static final By PRODUCT_NAME =
            By.className("inventory_item_name");
    private static final By PRODUCT_PRICE =
            By.className("inventory_item_price");
    private static final By PRODUCT_BUTTON = By.tagName("button");
    private static final By CART_BADGE = By.className("shopping_cart_badge");
    private static final By CART_LINK =
            By.cssSelector("a.shopping_cart_link");
    private static final By PAGE_TITLE = By.cssSelector(
            ".header_secondary_container .title"
    );
    private static final By CART_ITEMS = By.className("cart_item");
    private static final By CART_QUANTITY = By.className("cart_quantity");
    private static final By CART_ITEM_BUTTONS =
            By.cssSelector(".cart_item button");
    private static final By CHECKOUT_BUTTON = By.id("checkout");
    private static final By FIRST_NAME = By.id("first-name");
    private static final By LAST_NAME = By.id("last-name");
    private static final By POSTAL_CODE = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.id("continue");
    private static final By SUBTOTAL =
            By.className("summary_subtotal_label");
    private static final By TAX = By.className("summary_tax_label");
    private static final By TOTAL = By.className("summary_total_label");
    private static final By FINISH_BUTTON = By.id("finish");
    private static final By COMPLETE_HEADER =
            By.className("complete-header");
    private static final By COMPLETE_TEXT = By.className("complete-text");
    private static final By BACK_TO_PRODUCTS = By.id("back-to-products");

    private static int passedChecks = 0;
    private static int failedChecks = 0;

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.ignoring(StaleElementReferenceException.class);

        List<Product> expectedProducts = new ArrayList<>();

        try {
            // Bước 1: Đăng nhập.
            driver.get(URL);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    USERNAME
            )).sendKeys("standard_user");

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    PASSWORD
            )).sendKeys("secret_sauce");

            wait.until(ExpectedConditions.elementToBeClickable(
                    LOGIN_BUTTON
            )).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    INVENTORY_LIST
            ));

            check(
                    "Đăng nhập",
                    driver.getCurrentUrl().contains("inventory.html"),
                    "Mở trang sản phẩm",
                    driver.getCurrentUrl()
            );

            // Bước 2: Sắp xếp giá từ thấp đến cao.
            Select sortSelect = new Select(wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            SORT_DROPDOWN
                    )
            ));
            sortSelect.selectByValue("lohi");

            String selectedSort = new Select(
                    driver.findElement(SORT_DROPDOWN)
            ).getFirstSelectedOption().getText();

            check(
                    "Dropdown chọn giá thấp đến cao",
                    selectedSort.equals("Price (low to high)"),
                    "Price (low to high)",
                    selectedSort
            );

            wait.until(CheckoutSuccessTest::arePricesSortedLowToHigh);

            check(
                    "Danh sách sản phẩm được sắp xếp tăng dần theo giá",
                    arePricesSortedLowToHigh(driver),
                    "Giá tăng dần",
                    "Đã kiểm tra danh sách sản phẩm"
            );

            // Bước 3: Thêm hai sản phẩm đầu tiên sau khi sắp xếp.
            for (int i = 0; i < 2; i++) {
                int productIndex = i;

                WebElement productCard = wait.until(d -> {
                    List<WebElement> cards = d.findElements(PRODUCT_CARDS);

                    if (cards.size() <= productIndex) {
                        return null;
                    }

                    WebElement card = cards.get(productIndex);
                    return card.isDisplayed() ? card : null;
                });

                String productName = productCard.findElement(
                        PRODUCT_NAME
                ).getText();

                String productPrice = productCard.findElement(
                        PRODUCT_PRICE
                ).getText();

                expectedProducts.add(
                        new Product(productName, productPrice)
                );

                WebElement addButton = productCard.findElement(
                        PRODUCT_BUTTON
                );
                String addButtonId = addButton.getAttribute("id");
                String removeButtonId = addButtonId.replace(
                        "add-to-cart-",
                        "remove-"
                );
                addButton.click();

                wait.until(ExpectedConditions.textToBe(
                        By.id(removeButtonId),
                        "Remove"
                ));

                String expectedBadgeCount =
                        String.valueOf(productIndex + 1);

                wait.until(ExpectedConditions.textToBePresentInElementLocated(
                        CART_BADGE,
                        expectedBadgeCount
                ));

                System.out.println(
                        "Đã thêm: " + productName + " - " + productPrice
                );
            }

            String cartBadge = driver.findElement(
                    CART_BADGE
            ).getText();

            check(
                    "Số lượng sản phẩm trên biểu tượng giỏ hàng",
                    cartBadge.equals("2"),
                    "2",
                    cartBadge
            );

            // Bước 4: Mở và kiểm tra Your Cart.
            WebElement cartLink = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            CART_LINK
                    )
            );

            cartLink.click();

            wait.until(ExpectedConditions.urlContains("cart.html"));
            wait.until(ExpectedConditions.textToBe(
                    PAGE_TITLE,
                    "Your Cart"
            ));

            String cartTitle = driver.findElement(PAGE_TITLE).getText();

            check(
                    "Tiêu đề Your Cart",
                    cartTitle.equals("Your Cart"),
                    "Your Cart",
                    cartTitle
            );

            verifyProducts(
                    driver,
                    wait,
                    expectedProducts,
                    "Your Cart"
            );

            List<WebElement> cartButtons = driver.findElements(
                    CART_ITEM_BUTTONS
            );

            int visibleRemoveButtons = 0;

            for (WebElement button : cartButtons) {
                if (button.isDisplayed()
                        && button.getText().trim().equals("Remove")) {
                    visibleRemoveButtons++;
                }
            }

            check(
                    "Your Cart hiển thị 2 nút Remove",
                    visibleRemoveButtons == 2,
                    "2",
                    String.valueOf(visibleRemoveButtons)
            );

            // Bước 5: Nhập thông tin checkout.
            wait.until(ExpectedConditions.elementToBeClickable(
                    CHECKOUT_BUTTON
            )).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    FIRST_NAME
            )).sendKeys("Do");

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    LAST_NAME
            )).sendKeys("Hieu");

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    POSTAL_CODE
            )).sendKeys("700000");

            // Bước 6: Kiểm tra Checkout: Overview.
            wait.until(ExpectedConditions.elementToBeClickable(
                    CONTINUE_BUTTON
            )).click();

            wait.until(ExpectedConditions.textToBe(
                    PAGE_TITLE,
                    "Checkout: Overview"
            ));

            String overviewTitle = driver.findElement(PAGE_TITLE).getText();

            check(
                    "Tiêu đề Checkout: Overview",
                    overviewTitle.equals("Checkout: Overview"),
                    "Checkout: Overview",
                    overviewTitle
            );

            verifyProducts(
                    driver,
                    wait,
                    expectedProducts,
                    "Checkout: Overview"
            );

            String expectedShipping =
                    "Free Pony Express Delivery!";

            By shippingLocator = By.xpath(
                    "//*[contains(@class,'summary_value_label')"
                            + " and normalize-space(.)='"
                            + expectedShipping
                            + "']"
            );

            String actualShipping = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            shippingLocator
                    )
            ).getText();

            check(
                    "Shipping Information",
                    actualShipping.equals(expectedShipping),
                    expectedShipping,
                    actualShipping
            );

            BigDecimal expectedItemTotal = BigDecimal.ZERO;

            for (Product product : expectedProducts) {
                expectedItemTotal = expectedItemTotal.add(
                        product.getPriceAsNumber()
                );
            }

            String itemTotalText = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            SUBTOTAL
                    )
            ).getText();

            BigDecimal displayedItemTotal = readMoney(itemTotalText);

            check(
                    "Item total đúng bằng tổng giá hai sản phẩm",
                    expectedItemTotal.compareTo(displayedItemTotal) == 0,
                    expectedItemTotal.toPlainString(),
                    displayedItemTotal.toPlainString()
            );

            String taxText = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            TAX
                    )
            ).getText();

            BigDecimal displayedTax = readMoney(taxText);

            BigDecimal expectedTax = expectedItemTotal
                    .multiply(TAX_RATE)
                    .setScale(2, RoundingMode.HALF_UP);

            check(
                    "Tax đúng theo mức 8%",
                    expectedTax.compareTo(displayedTax) == 0,
                    expectedTax.toPlainString(),
                    displayedTax.toPlainString()
            );

            String totalText = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            TOTAL
                    )
            ).getText();

            BigDecimal displayedTotal = readMoney(totalText);

            BigDecimal totalFromDisplayedValues =
                    displayedItemTotal.add(displayedTax);

            check(
                    "Total bằng Item total cộng Tax",
                    totalFromDisplayedValues.compareTo(displayedTotal) == 0,
                    totalFromDisplayedValues.toPlainString(),
                    displayedTotal.toPlainString()
            );

            BigDecimal expectedTotal = expectedItemTotal.add(expectedTax);

            check(
                    "Total đúng theo giá sản phẩm và thuế mong đợi",
                    expectedTotal.compareTo(displayedTotal) == 0,
                    expectedTotal.toPlainString(),
                    displayedTotal.toPlainString()
            );

            WebElement finishButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            FINISH_BUTTON
                    )
            );

            check(
                    "Nút Finish hiển thị",
                    finishButton.isDisplayed(),
                    "Có hiển thị",
                    String.valueOf(finishButton.isDisplayed())
            );

            finishButton.click();

            // Bước 7: Kiểm tra trang hoàn tất đơn hàng.
            wait.until(ExpectedConditions.textToBe(
                    PAGE_TITLE,
                    "Checkout: Complete!"
            ));

            String completeTitle = driver.findElement(PAGE_TITLE).getText();

            check(
                    "Tiêu đề Checkout: Complete!",
                    completeTitle.equals("Checkout: Complete!"),
                    "Checkout: Complete!",
                    completeTitle
            );

            String thankYouMessage = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            COMPLETE_HEADER
                    )
            ).getText();

            check(
                    "Thông báo cảm ơn",
                    thankYouMessage.equals("Thank you for your order!"),
                    "Thank you for your order!",
                    thankYouMessage
            );

            String expectedDispatchMessage =
                    "Your order has been dispatched, and will arrive "
                            + "just as fast as the pony can get there!";

            String dispatchMessage = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            COMPLETE_TEXT
                    )
            ).getText();

            check(
                    "Thông báo giao hàng",
                    dispatchMessage.equals(expectedDispatchMessage),
                    expectedDispatchMessage,
                    dispatchMessage
            );

            WebElement backHomeButton = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            BACK_TO_PRODUCTS
                    )
            );

            check(
                    "Nút Back Home hiển thị",
                    backHomeButton.isDisplayed(),
                    "Có hiển thị",
                    String.valueOf(backHomeButton.isDisplayed())
            );

        } catch (Exception e) {
            failedChecks++;
            System.out.println("Có lỗi khi chạy: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println(
                    "Tổng kết: "
                            + passedChecks
                            + " PASS, "
                            + failedChecks
                            + " FAIL"
            );

            driver.quit();
        }
    }

    private static boolean arePricesSortedLowToHigh(WebDriver driver) {
        List<WebElement> productCards = driver.findElements(PRODUCT_CARDS);

        if (productCards.isEmpty()) {
            return false;
        }

        BigDecimal previousPrice = null;

        for (WebElement card : productCards) {
            String priceText = card.findElement(
                    PRODUCT_PRICE
            ).getText();

            BigDecimal currentPrice = readMoney(priceText);

            if (previousPrice != null
                    && previousPrice.compareTo(currentPrice) > 0) {
                return false;
            }

            previousPrice = currentPrice;
        }

        return true;
    }

    private static void verifyProducts(
            WebDriver driver,
            WebDriverWait wait,
            List<Product> expectedProducts,
            String pageName
    ) {
        wait.until(d -> d.findElements(
                CART_ITEMS
        ).size() == expectedProducts.size());

        List<WebElement> cartItems = driver.findElements(CART_ITEMS);

        check(
                pageName + " có đúng 2 sản phẩm",
                cartItems.size() == expectedProducts.size(),
                String.valueOf(expectedProducts.size()),
                String.valueOf(cartItems.size())
        );

        for (Product expectedProduct : expectedProducts) {
            WebElement matchingItem = null;

            for (WebElement cartItem : cartItems) {
                String actualName = cartItem.findElement(
                        PRODUCT_NAME
                ).getText();

                if (actualName.equals(expectedProduct.getName())) {
                    matchingItem = cartItem;
                    break;
                }
            }

            if (matchingItem == null) {
                check(
                        pageName + " tìm thấy "
                                + expectedProduct.getName(),
                        false,
                        expectedProduct.getName(),
                        "Không tìm thấy"
                );
                continue;
            }

            String actualName = matchingItem.findElement(
                    PRODUCT_NAME
            ).getText();

            String actualQuantity = matchingItem.findElement(
                    CART_QUANTITY
            ).getText();

            String actualPrice = matchingItem.findElement(
                    PRODUCT_PRICE
            ).getText();

            check(
                    pageName + " - tên sản phẩm",
                    actualName.equals(expectedProduct.getName()),
                    expectedProduct.getName(),
                    actualName
            );

            check(
                    pageName + " - số lượng " + actualName,
                    actualQuantity.equals("1"),
                    "1",
                    actualQuantity
            );

            check(
                    pageName + " - giá " + actualName,
                    actualPrice.equals(expectedProduct.getPrice()),
                    expectedProduct.getPrice(),
                    actualPrice
            );
        }
    }

    private static BigDecimal readMoney(String text) {
        Matcher matcher = Pattern
                .compile("\\$([0-9]+\\.[0-9]{2})")
                .matcher(text);

        if (!matcher.find()) {
            throw new IllegalArgumentException(
                    "Không tìm thấy số tiền trong: " + text
            );
        }

        return new BigDecimal(matcher.group(1));
    }

    private static void check(
            String testName,
            boolean passed,
            String expected,
            String actual
    ) {
        if (passed) {
            passedChecks++;
        } else {
            failedChecks++;
        }

        System.out.println(
                (passed ? "PASS: " : "FAIL: ")
                        + testName
                        + " | Mong đợi: "
                        + expected
                        + " | Thực tế: "
                        + actual
        );
    }

    private static class Product {
        private final String name;
        private final String price;

        private Product(String name, String price) {
            this.name = name;
            this.price = price;
        }

        private String getName() {
            return name;
        }

        private String getPrice() {
            return price;
        }

        private BigDecimal getPriceAsNumber() {
            return new BigDecimal(price.replace("$", "").trim());
        }
    }
}
