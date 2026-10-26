package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class TestDemo {
    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        try {
            driver.manage().window().maximize();
            driver.get("https://www.saucedemo.com/");

            WebElement inputUsername = driver.findElement(By.id("user-name"));
            inputUsername.sendKeys("standard_user");

            WebElement inputPassword = driver.findElement(By.id("password"));
            inputPassword.sendKeys("secret_sauce");

            WebElement buttonLogin = driver.findElement(By.id("login-button"));
            buttonLogin.click();
        } finally {
    try {
        Thread.sleep(5000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
    driver.quit();
}
    }
}
