package day03;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class UploadFileTest {
    public static void main(String[] args) throws InterruptedException {
        // Khởi tạo WebDriver
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        // Mở trang web
        driver.get("https://demo.guru99.com/test/upload/");

        // Đường dẫn tệp cần tải lên
        String filePath = "/Users/dohieu/Desktop/DEV-PRO/AutoTest/demo/Hello World_Selenium.docx";
        //String filePath = new File("Hello World_Selenium.docx").getAbsolutePath();

        // Tìm phần tử input type="file" và gửi đường dẫn tệp
        WebElement uploadField = driver.findElement(By.id("uploadfile_0"));
        uploadField.sendKeys(filePath);

        // Đồng ý với các điều khoản
        WebElement termsCheckbox = driver.findElement(By.id("terms"));
        termsCheckbox.click();

        // Nhấp vào nút "Upload"
        WebElement uploadButton = driver.findElement(By.id("submitbutton"));
        uploadButton.click(); //11.0.27

        // Đợi để kiểm tra kết quả (nếu cần)
        Thread.sleep(5000);

        // Lấy thông báo sau khi upload
        WebElement resultMessage = driver.findElement(By.id("res"));
        System.out.println("Kết quả tải lên: " + resultMessage.getText());

        // Đóng trình duyệt
        //driver.quit();
    }
}
