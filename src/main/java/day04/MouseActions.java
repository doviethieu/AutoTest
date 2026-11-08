/*package day04;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MouseActions {

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();

            // Dùng WebDriverWait để chờ menu và alert xuất hiện.
            WebDriverWait wait =
                    new WebDriverWait(driver, Duration.ofSeconds(10));

            driver.get(
                    "https://demo.guru99.com/test/simple_context_menu.html"
            );

            Actions actions = new Actions(driver);

            // Tìm phần tử "right click me".
            WebElement rightClickTarget = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.cssSelector("span.context-menu-one")
                    )
            );

            // Nhấn chuột phải để mở menu tùy chọn.
            actions.contextClick(rightClickTarget).perform();

            // Chờ menu hiện ra rồi tìm mục Edit.
            WebElement editOption = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath(
                                "//li[contains(@class,'context-menu-item')"
                                + " and normalize-space()='Edit']"
                            )
                    )
            );

            // Bấm Edit.
            editOption.click();

            // Nếu bấm Edit tạo alert, đọc nội dung rồi đóng alert.
            Alert editAlert = wait.until(
                    ExpectedConditions.alertIsPresent()
            );
            System.out.println(
                    "Thông báo sau khi bấm Edit: " + editAlert.getText()
            );
            editAlert.accept();

            // Tìm nút double-click trên cùng trang.
            WebElement doubleClickButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath(
                                "//button[contains(text(),"
                                + "'Double-Click Me To See Alert')]"
                            )
                    )
            );

            // Double-click vào nút.
            actions.doubleClick(doubleClickButton).perform();

            // Đọc và đóng alert do nút double-click tạo ra.
            Alert doubleClickAlert = wait.until(
                    ExpectedConditions.alertIsPresent()
            );
            System.out.println(
                    "Thông báo sau khi double-click: "
                            + doubleClickAlert.getText()
            );
            doubleClickAlert.accept();
         Thread.sleep(3000);
        } catch (Exception e) {
            System.out.println("Có lỗi khi chạy: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng Chrome sau khi chạy xong.
            driver.quit();
        }
    }
}

*/
/* 

package day04;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MouseActions {

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();

             // Chờ tối đa 10 giây khi tìm phần tử trên trang.
            driver.manage().timeouts()
                    .implicitlyWait(Duration.ofSeconds(10));

             driver.manage().timeouts()
                    .pageLoadTimeout(Duration.ofSeconds(10));

            driver.manage().timeouts()
                    .scriptTimeout(Duration.ofSeconds(10));

        //driver.manage().timeouts().implicitlyWait(Duration.ZERO); // tạm tắt
        //Selenium sẽ đợi tối đa thời gian được chỉ định nếu chưa thấy phần tử ngay.
        //Nếu thấy sớm hơn thì Selenium sẽ tiếp tục chạy mà không đợi đủ thời gian.
        //Implicit wait lại áp dụng cho mọi lệnh findElement()
        //áp dụng cho toàn bộ WebDriver, chỉ cần thiết lập 1 lần




            // Dùng WebDriverWait để chờ menu và alert xuất hiện.
            WebDriverWait wait =
                    new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement firstNameField = wait.until(
                    ExpectedConditions.visibilityofElementLocated(By.id("firstName"))

            driver.get(
                    "https://demo.guru9.com/test/simple_context_menu.html"
            );

            Actions actions = new Actions(driver);

            // Tìm phần tử "right click me".
            WebElement rightClickTarget = driver.findElement(
                    By.cssSelector("span.context-menu-one")
            );

            // Nhấn chuột phải để mở menu.
            actions.contextClick(rightClickTarget).perform();

            // Tìm và bấm mục Edit trong menu.
            WebElement editOption = driver.findElement(
                    By.xpath(
                            "//li[contains(@class,'context-menu-item')"
                                    + " and normalize-space()='Edit']"
                    )
            );
            editOption.click();

            // Lấy nội dung alert xuất hiện sau khi bấm Edit rồi đóng nó.
            Alert editAlert = driver.switchTo().alert();
            System.out.println("Thông báo Edit: " + editAlert.getText());
            editAlert.accept();

            // Tìm nút double-click rồi double-click vào nút.
            WebElement doubleClickButton = driver.findElement(
                    By.xpath("//button[contains(text(),"
                            + "'Double-Click Me To See Alert')]")
            );
            actions.doubleClick(doubleClickButton).perform();

            // Lấy nội dung alert sau double-click rồi đóng nó.
            Alert doubleClickAlert = driver.switchTo().alert();
            System.out.println(
                    "Thông báo double-click: "
                            + doubleClickAlert.getText()
            );
            doubleClickAlert.accept();

        } catch (Exception e) {
            System.out.println("Có lỗi khi chạy: " + e.getMessage());
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}*/


//Selenium tự quyết định (cứ khoảng 0,5 giây lại kiểm tra điều kiện một lần, cho đến khi điều kiện đúng hoặc hết thời gian),Có thể tùy chỉnh
        //Selenium khuyến cáo không trộn implicit wait với explicit wait, vì thời gian chờ có thể cộng dồn và khó đoán.

        
package day04;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MouseActions {

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();

            // Chờ tối đa 10 giây cho từng điều kiện bên dưới.
            WebDriverWait wait =
                    new WebDriverWait(driver, Duration.ofSeconds(10));

            // Mở đúng trang Guru99.
            driver.get(
                    "https://demo.guru99.com/test/simple_context_menu.html"
            );

            Actions actions = new Actions(driver);

            // Chờ phần tử "right click me" xuất hiện và có thể bấm.
            WebElement rightClickTarget = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.cssSelector("span.context-menu-one")
                    )
            );

            // Nhấn chuột phải để mở menu.
            actions.contextClick(rightClickTarget).perform();

            // Chờ mục Edit trong menu rồi bấm vào.
            WebElement editOption = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath(
                                    "//li[contains(@class,'context-menu-item')"
                                            + " and normalize-space()='Edit']"
                            )
                    )
            );
            editOption.click();

            // Chờ alert xuất hiện, in nội dung rồi đóng alert.
            Alert editAlert = wait.until(
                    ExpectedConditions.alertIsPresent()
            );
            System.out.println(
                    "Thông báo Edit: " + editAlert.getText()
            );
            editAlert.accept();

            // Chờ nút double-click xuất hiện và có thể bấm.
            WebElement doubleClickButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath(
                                    "//button[contains(text(),"
                                            + "'Double-Click Me To See Alert')]"
                            )
                    )
            );

            // Double-click vào nút.
            actions.doubleClick(doubleClickButton).perform();

            // Chờ alert xuất hiện, in nội dung rồi đóng alert.
            Alert doubleClickAlert = wait.until(
                    ExpectedConditions.alertIsPresent()
            );
            System.out.println(
                    "Thông báo double-click: "
                            + doubleClickAlert.getText()
            );
            doubleClickAlert.accept();

        } catch (Exception e) {
            System.out.println("Có lỗi khi chạy: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng Chrome kể cả khi chương trình gặp lỗi.
            driver.quit();
        }
    }
}