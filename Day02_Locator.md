# Day 02: Tìm Locator

## BT1

### 1.1. Checkout: Overview

```xpath
//span[contains(text(),'Checkout')]
//div[contains(text(),'QTY')]
//div[contains(text(),'Des')]
//div[contains(text(),'Sauce Labs Backpack')]
//div[contains(text(),'Sauce Labs Bike Light')]
//div[contains(text(),'Sauce Labs Bolt T-Shirt')]
//div[contains(text(),'Sauce Labs Backpack')]/ancestor::div[@class='cart_item']/descendant::div[text()='1']
//div[contains(text(),'Sauce Labs Bike Light')]/ancestor::div[@class='cart_item']/descendant::div[text()='1']
//div[contains(text(),'Sauce Labs Bolt T-Shirt')]/ancestor::div[@class='cart_item']/descendant::div[text()='1']
//div[contains(text(),'Sauce Labs Backpack')]/ancestor::div[@class='cart_item']/descendant::div[text()='29.99']
//div[contains(text(),'Sauce Labs Bike Light')]/ancestor::div[@class='cart_item']/descendant::div[text()='9.99']
//div[contains(text(),'Sauce Labs Bolt T-Shirt')]/ancestor::div[@class='cart_item']/descendant::div[text()='15.99']
//div[contains(text(),'carry')]
//div[contains(text(),'A red light')]
//div[contains(text(),'Get your')]
//div[contains(text(),'Payment')]
//div[contains(text(),'SauceCard')]
//div[contains(text(),'Shipping')]
//div[contains(text(),'Free')]
//div[contains(text(),'Price')]
//div[contains(text(),'Item')]
//div[contains(text(),'Tax')]
//div[contains(text(),'Total:')]
//button[@id='cancel']
//button[@id='finish']
```

### 1.2. Checkout: Complete

```xpath
//span[contains(text(),'Checkout: Complete')]
//h2[contains(text(),'Thank')]
//div[contains(text(),'Your')]
//button[contains(text(),'Back')]
//button[contains(text(),'Generate')]
```

## BT2. Sauce Labs - Request a Demo

```xpath
//h3[contains(text(),'Request ')]
//input[contains(@id,'Business')]
//input[contains(@id,'Company')]
//select[contains(@id,'Use')]
//select[contains(@id,'Use-Case')]/descendant::option[contains(text(),'AI Test')]
//textarea[contains(@id,'Comment')]
//button[contains(text(),'request')]
//p[contains(text(),'You')]
```
