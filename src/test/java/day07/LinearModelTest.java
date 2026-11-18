package day07;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import utils.Hook;

public class LinearModelTest extends Hook {

    @Test(priority = 1, description = "TC01 - Happy case: đúng username và password")
    public void inputValidUserNameAndPassword() {
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Tìm logo trang sản phẩm trước khi kiểm tra URL.
        Assert.assertTrue(driver.findElement(By.className("app_logo")).isDisplayed(),
                "Logo không hiển thị trên trang sản phẩm!");
        Assert.assertEquals(driver.getCurrentUrl(),
                "https://www.saucedemo.com/inventory.html",
                "Đăng nhập thất bại, chưa mở trang Inventory!");
        Assert.assertTrue(driver.findElements(By.className("inventory_item")).size() > 0,
                "Trang Inventory không hiển thị sản phẩm!");
    }

    @Test(priority = 2, description = "TC02 - Unhappy case: đúng username, sai password")
    public void inputValidUserNameAndInvalidPassword() {
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("wrong_password");
        driver.findElement(By.id("login-button")).click();

        WebElement errorMessage = driver.findElement(By.cssSelector("[data-test='error']"));
        Assert.assertTrue(errorMessage.isDisplayed(), "Thông báo lỗi không hiển thị!");
        Assert.assertEquals(errorMessage.getText(),
                "Epic sadface: Username and password do not match any user in this service",
                "Thông báo sai thông tin đăng nhập không chính xác!");
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/",
                "Đăng nhập sai phải ở lại trang Login!");
        Assert.assertTrue(driver.findElement(By.id("login-button")).isDisplayed(),
                "Nút Login không còn hiển thị!");
    }

    @Test(priority = 3, description = "TC03 - Unhappy case: sai username, đúng password")
    public void inputInvalidUserNameAndValidPassword() {
        driver.findElement(By.id("user-name")).sendKeys("invalid_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        WebElement errorMessage = driver.findElement(By.cssSelector("[data-test='error']"));
        Assert.assertTrue(errorMessage.isDisplayed(), "Thông báo lỗi không hiển thị!");
        Assert.assertEquals(errorMessage.getText(),
                "Epic sadface: Username and password do not match any user in this service",
                "Thông báo sai thông tin đăng nhập không chính xác!");
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/",
                "Đăng nhập sai phải ở lại trang Login!");
        Assert.assertTrue(driver.findElement(By.id("login-button")).isDisplayed(),
                "Nút Login không còn hiển thị!");
    }

    @Test(priority = 4, description = "TC04 - Unhappy case: sai cả username và password")
    public void inputInvalidUserNameAndPassword() {
        driver.findElement(By.id("user-name")).sendKeys("invalid_user");
        driver.findElement(By.id("password")).sendKeys("wrong_password");
        driver.findElement(By.id("login-button")).click();

        WebElement errorMessage = driver.findElement(By.cssSelector("[data-test='error']"));
        Assert.assertTrue(errorMessage.isDisplayed(), "Thông báo lỗi không hiển thị!");
        Assert.assertEquals(errorMessage.getText(),
                "Epic sadface: Username and password do not match any user in this service",
                "Thông báo sai thông tin đăng nhập không chính xác!");
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/",
                "Đăng nhập sai phải ở lại trang Login!");
        Assert.assertTrue(driver.findElement(By.id("login-button")).isDisplayed(),
                "Nút Login không còn hiển thị!");
    }
}
