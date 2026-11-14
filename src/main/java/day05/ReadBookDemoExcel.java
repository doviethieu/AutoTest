package day05;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ReadBookDemoExcel {
    private static final String URL =
            "https://saucelabs.com/request-demo";
    private static final String THANK_YOU_URL =
            "https://saucelabs.com/thank-you-demo";
    private static final String EXCEL_FILE = "/BookDemoData.xlsx";
    private static final String DEFAULT_USE_CASE = "AI Test Authoring";

    private static final By EMAIL = By.id("Business-Email");
    private static final By FIRST_NAME = By.name("First Name");
    private static final By LAST_NAME = By.name("Last Name");
    private static final By COMPANY = By.id("Company");
    private static final By PHONE = By.id("Phone");
    private static final By COUNTRY = By.id("Country");
    private static final By STATE = By.id("State");
    private static final By USE_CASE = By.id("Use-Case");
    private static final By COMMENTS = By.id("Comments");

    private static final By REQUEST_BUTTON = By.xpath(
            "//button[contains(translate(normalize-space(.),"
                    + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                    + "'abcdefghijklmnopqrstuvwxyz'),"
                    + "'request a demo')]"
    );
    private static final By SUCCESS_MESSAGE = By.xpath(
            "//*[normalize-space()='Thanks for reaching out!']"
    );
    private static final By SUBMISSION_ERROR = By.xpath(
            "//*[contains(text(),'Something went wrong while submitting the form')]"
    );
    private static final By BUSINESS_EMAIL_ERROR = By.xpath(
            "//*[contains(text(),'Please enter a valid business email')]"
    );

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static JavascriptExecutor js;
    private static int passed;
    private static int failed;

    public static void main(String[] args) throws IOException {
        List<Map<String, String>> testCases = readTestCases();

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        js = (JavascriptExecutor) driver;

        try {
            for (Map<String, String> testCase : testCases) {
                runTestCase(testCase);
            }
            System.out.println("\nTổng kết: "
                    + passed + " PASS, " + failed + " FAIL");
        } finally {
            driver.quit();
        }
    }

    private static List<Map<String, String>> readTestCases()
            throws IOException {
        // Đọc Excel từ resources, dùng được trên máy khác.
        InputStream input = ReadBookDemoExcel.class
                .getResourceAsStream(EXCEL_FILE);
        if (input == null) {
            throw new IOException(
                    "Không tìm thấy " + EXCEL_FILE
                            + " trong src/main/resources"
            );
        }

        List<Map<String, String>> testCases = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (input; Workbook workbook = new XSSFWorkbook(input)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IOException("Sheet1 không có hàng tiêu đề.");
            }

            Map<Integer, String> headers = new LinkedHashMap<>();
            for (int column = 0;
                 column < headerRow.getLastCellNum();
                 column++) {
                String header = formatter.formatCellValue(
                        headerRow.getCell(
                                column,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        )
                ).trim();
                if (!header.isEmpty()) {
                    headers.put(column, normalize(header));
                }
            }

            for (int rowIndex = 1;
                 rowIndex <= sheet.getLastRowNum();
                 rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                Map<String, String> values = new LinkedHashMap<>();
                for (Map.Entry<Integer, String> header : headers.entrySet()) {
                    String cellValue = formatter.formatCellValue(
                            row.getCell(
                                    header.getKey(),
                                    Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                            )
                    ).trim();
                    values.put(header.getValue(), cellValue);
                }

                if (!value(values, "Test Case").isBlank()) {
                    testCases.add(values);
                }
            }
        }
        return testCases;
    }

    private static void runTestCase(Map<String, String> data) {
        String testName = value(data, "Test Case");
        try {
            // Mở lại form cho mỗi dòng dữ liệu Excel.
            openFormAndFill(data);

            if (testName.startsWith("TC02")) {
                checkEmailFormat(testName, data, "valueMissing");
            } else if (testName.startsWith("TC03")
                    || testName.startsWith("TC04")
                    || testName.startsWith("TC05")) {
                checkEmailFormat(testName, data, "typeMismatch");
            } else if (testName.startsWith("TC06")) {
                checkGmailBlocked(testName, data);
            } else if (testName.startsWith("TC13")) {
                checkDefaultUseCase(testName, data);
            } else if (testName.startsWith("TC14")) {
                checkUseCaseOptions(testName, data);
            } else if (testName.startsWith("TC15")) {
                checkCommentsOptional(testName, data);
            } else if (testName.startsWith("TC16")) {
                checkCommentsText(testName, data);
            } else if (testName.startsWith("TC17")) {
                checkCommentsMaxLength(testName, data);
            } else if (testName.startsWith("TC18")) {
                checkCompanyRecovery(testName, data);
            } else if (testName.startsWith("TC19")) {
                checkValidState(testName, data);
            } else if (testName.startsWith("TC20")) {
                checkStateAfterCountryChange(testName, data);
            } else if (testName.startsWith("TC01")) {
                submitHappyCase(testName, data);
            } else {
                checkMissingField(testName, data);
            }
        } catch (Exception e) {
            report(false, testName, value(data, "Expected Result"),
                    "Lỗi: " + e.getMessage());
        }
    }

    private static void openFormAndFill(Map<String, String> data) {
        driver.get(URL);
        enter(wait.until(
                ExpectedConditions.visibilityOfElementLocated(EMAIL)
        ), value(data, "Business Email"));

        // Rời ô email để kích hoạt hiển thị các trường còn lại.
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(COMPANY)
        ).click();

        String testName = value(data, "Test Case");
        if (testName.startsWith("TC02") || testName.startsWith("TC03")
                || testName.startsWith("TC04") || testName.startsWith("TC05")
                || testName.startsWith("TC06")) {
            return;
        }

        enter(wait.until(
                ExpectedConditions.visibilityOfElementLocated(FIRST_NAME)
        ), value(data, "First name"));
        enter(wait.until(
                ExpectedConditions.visibilityOfElementLocated(LAST_NAME)
        ), value(data, "Last name"));
        enter(wait.until(
                ExpectedConditions.visibilityOfElementLocated(COMPANY)
        ), value(data, "Company"));
        enter(wait.until(
                ExpectedConditions.visibilityOfElementLocated(PHONE)
        ), value(data, "Phone number"));

        String country = value(data, "Country");
        if (!country.isBlank()) {
            new Select(wait.until(
                    ExpectedConditions.visibilityOfElementLocated(COUNTRY)
            )).selectByVisibleText(country);
        }

        if ("United States".equalsIgnoreCase(country)) {
            WebElement state = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(STATE)
            );
            String stateValue = value(data, "State");
            if (!stateValue.isBlank()) {
                new Select(state).selectByVisibleText(stateValue);
            }
        }

        String useCase = value(data, "Use Case");
        if (!useCase.isBlank()) {
            new Select(wait.until(
                    ExpectedConditions.visibilityOfElementLocated(USE_CASE)
            )).selectByVisibleText(useCase);
        }

        enter(wait.until(
                ExpectedConditions.visibilityOfElementLocated(COMMENTS)
        ), value(data, "Comments"));
    }

    private static void checkEmailFormat(
            String testName,
            Map<String, String> data,
            String validityState) {
        WebElement email = wait.until(
                ExpectedConditions.visibilityOfElementLocated(EMAIL)
        );
        boolean expectedError = validityState(email, validityState);
        report(expectedError, testName, value(data, "Expected Result"),
                "Email validity." + validityState + " = " + expectedError);
    }

    private static void checkGmailBlocked(
            String testName,
            Map<String, String> data) {
        WebElement button = wait.until(
                ExpectedConditions.visibilityOfElementLocated(REQUEST_BUTTON)
        );
        boolean emailErrorVisible = driver.findElements(BUSINESS_EMAIL_ERROR)
                .stream().anyMatch(WebElement::isDisplayed);
        boolean buttonDisabled = !button.isEnabled();

        report(emailErrorVisible && buttonDisabled, testName,
                value(data, "Expected Result"),
                "Email business error hiển thị = " + emailErrorVisible
                        + "; nút bị khóa = " + buttonDisabled);
    }

    private static void checkMissingField(
            String testName,
            Map<String, String> data) {
        By target = missingFieldLocator(testName);
        if (target == null) {
            report(false, testName, value(data, "Expected Result"),
                    "Không xác định được trường cần kiểm tra");
            return;
        }

        WebElement targetElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(target)
        );
        boolean targetMissing = validityState(targetElement, "valueMissing");
        // Các trường khác phải hợp lệ để tránh PASS sai lý do.
        boolean othersValid = allRequiredFieldsValid(
                value(data, "Country"), target
        );

        report(targetMissing && othersValid, testName,
                value(data, "Expected Result"),
                "Trường mục tiêu bị thiếu = " + targetMissing
                        + "; các trường khác hợp lệ = " + othersValid);
    }

    private static void checkDefaultUseCase(
            String testName,
            Map<String, String> data) {
        // Dữ liệu Use Case trống: giữ lựa chọn mặc định.
        String actual = selectedText(USE_CASE);
        boolean otherFieldsValid = allRequiredFieldsValid(
                value(data, "Country"), null
        );
        boolean correctDefault = DEFAULT_USE_CASE.equals(actual);

        report(correctDefault && otherFieldsValid, testName,
                value(data, "Expected Result"),
                "Use Case mặc định = " + actual
                        + "; các trường khác hợp lệ = " + otherFieldsValid);
    }

    private static void checkUseCaseOptions(
            String testName,
            Map<String, String> data) {
        Select select = new Select(wait.until(
                ExpectedConditions.visibilityOfElementLocated(USE_CASE)
        ));
        List<String> options = select.getOptions().stream()
                .map(option -> option.getText().trim())
                .toList();
        String selected = select.getFirstSelectedOption().getText().trim();
        boolean result = options.contains(DEFAULT_USE_CASE)
                && DEFAULT_USE_CASE.equals(selected);

        report(result, testName, value(data, "Expected Result"),
                "Các lựa chọn: " + options + "; đang chọn: " + selected);
    }

    private static void checkCommentsOptional(
            String testName,
            Map<String, String> data) {
        WebElement comments = wait.until(
                ExpectedConditions.visibilityOfElementLocated(COMMENTS)
        );
        WebElement button = wait.until(
                ExpectedConditions.visibilityOfElementLocated(REQUEST_BUTTON)
        );

        boolean blankAndOptional = value(data, "Comments").isBlank()
                && !isRequired(comments);
        boolean otherFieldsValid = allRequiredFieldsValid(
                value(data, "Country"), null
        );
        boolean result = blankAndOptional
                && otherFieldsValid
                && button.isEnabled();

        report(result, testName, value(data, "Expected Result"),
                "Comments trống không chặn form; nút được bật = "
                        + button.isEnabled());
    }

    private static void checkCommentsText(
            String testName,
            Map<String, String> data) {
        WebElement comments = wait.until(
                ExpectedConditions.visibilityOfElementLocated(COMMENTS)
        );
        String expectedText = value(data, "Comments");
        String actualText = comments.getDomProperty("value");

        report(expectedText.equals(actualText), testName,
                value(data, "Expected Result"), actualText);
    }

    private static void checkCommentsMaxLength(
            String testName,
            Map<String, String> data) {
        WebElement comments = wait.until(
                ExpectedConditions.visibilityOfElementLocated(COMMENTS)
        );
        String maxLengthText = comments.getAttribute("maxlength");
        int maxLength = maxLengthText == null
                ? -1 : Integer.parseInt(maxLengthText);

        if (maxLength < 0) {
            report(true, testName, value(data, "Expected Result"),
                    "Textarea không khai báo maxlength; không áp dụng");
            return;
        }

        comments.clear();
        // Nhập vượt giới hạn một ký tự để kiểm tra maxlength.
        comments.sendKeys("A".repeat(maxLength + 1));
        String actualText = comments.getDomProperty("value");
        boolean withinLimit = actualText.length() <= maxLength;

        report(withinLimit, testName, value(data, "Expected Result"),
                "maxlength = " + maxLength
                        + "; số ký tự nhập được = " + actualText.length());
    }

    private static void checkCompanyRecovery(
            String testName,
            Map<String, String> data) {
        WebElement company = wait.until(
                ExpectedConditions.visibilityOfElementLocated(COMPANY)
        );
        company.clear();
        boolean invalidWhenEmpty = validityState(company, "valueMissing");
        enter(company, value(data, "Company"));

        boolean validAfterCorrection = isValid(company);
        boolean otherFieldsValid = allRequiredFieldsValid(
                value(data, "Country"), null
        );
        WebElement button = wait.until(
                ExpectedConditions.visibilityOfElementLocated(REQUEST_BUTTON)
        );

        boolean result = invalidWhenEmpty && validAfterCorrection
                && otherFieldsValid && button.isEnabled();
        report(result, testName, value(data, "Expected Result"),
                "Company sai khi trống = " + invalidWhenEmpty
                        + "; hợp lệ sau khi nhập lại = " + validAfterCorrection);
    }

    private static void checkValidState(
            String testName,
            Map<String, String> data) {
        WebElement state = wait.until(
                ExpectedConditions.visibilityOfElementLocated(STATE)
        );
        String actualState = selectedText(STATE);
        boolean formValid = allRequiredFieldsValid(value(data, "Country"), null);
        boolean result = "United States".equals(selectedText(COUNTRY))
                && !value(data, "State").isBlank()
                && actualState.equals(value(data, "State"))
                && isRequired(state) && isValid(state) && formValid;

        report(result, testName, value(data, "Expected Result"),
                "State đang chọn = " + actualState + "; form hợp lệ = " + formValid);
    }

    private static void checkStateAfterCountryChange(
            String testName,
            Map<String, String> data) {
        WebElement state = wait.until(
                ExpectedConditions.visibilityOfElementLocated(STATE)
        );
        boolean initialSelectionValid = "United States".equals(selectedText(COUNTRY))
                && !value(data, "State").isBlank()
                && value(data, "State").equals(selectedText(STATE))
                && isValid(state);

        // Đổi quốc gia để kiểm tra State được ẩn và không còn bắt buộc.
        new Select(driver.findElement(COUNTRY)).selectByVisibleText("Vietnam");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(STATE));
        boolean stateNotRequired = driver.findElements(STATE).stream()
                .noneMatch(element -> element.isEnabled() && isRequired(element));
        WebElement form = driver.findElement(EMAIL)
                .findElement(By.xpath("./ancestor::form"));
        boolean formValid = isValid(form);
        boolean result = initialSelectionValid
                && "Vietnam".equals(selectedText(COUNTRY))
                && stateNotRequired && formValid;

        report(result, testName, value(data, "Expected Result"),
                "Ban đầu chọn State đúng = " + initialSelectionValid
                        + "; Country = " + selectedText(COUNTRY)
                        + "; State đã ẩn; không còn bắt buộc = " + stateNotRequired
                        + "; form hợp lệ = " + formValid);
    }

    private static void submitHappyCase(
            String testName,
            Map<String, String> data) {
        WebElement button = wait.until(
                ExpectedConditions.visibilityOfElementLocated(REQUEST_BUTTON)
        );
        boolean formValid = allRequiredFieldsValid(
                value(data, "Country"), null
        );

        if (!formValid || !button.isEnabled()) {
            report(false, testName, value(data, "Expected Result"),
                    "Form chưa hợp lệ hoặc nút Request a Demo đang bị khóa");
            return;
        }

        button.click();
        // Kiểm tra URL và tiêu đề trang cảm ơn sau khi gửi.
        String outcome = wait.until(d -> {
            boolean successVisible = THANK_YOU_URL.equals(d.getCurrentUrl())
                    && d.findElements(SUCCESS_MESSAGE)
                    .stream().anyMatch(WebElement::isDisplayed);
            if (successVisible) {
                return "SUCCESS";
            }
            boolean errorVisible = d.findElements(SUBMISSION_ERROR)
                    .stream().anyMatch(WebElement::isDisplayed);
            return errorVisible ? "ERROR" : null;
        });

        if ("SUCCESS".equals(outcome)) {
            String actual = driver.findElements(SUCCESS_MESSAGE).stream()
                    .filter(WebElement::isDisplayed)
                    .findFirst().orElseThrow().getText().trim();
            report(true, testName, value(data, "Expected Result"),
                    driver.getCurrentUrl() + " | " + actual);
        } else {
            String actual = driver.findElements(SUBMISSION_ERROR).stream()
                    .filter(WebElement::isDisplayed)
                    .findFirst().orElseThrow().getText().trim();
            report(false, testName, value(data, "Expected Result"), actual);
        }
    }

    private static By missingFieldLocator(String testName) {
        if (testName.startsWith("TC07")) return FIRST_NAME;
        if (testName.startsWith("TC08")) return LAST_NAME;
        if (testName.startsWith("TC09")) return COMPANY;
        if (testName.startsWith("TC10")) return PHONE;
        if (testName.startsWith("TC11")) return COUNTRY;
        if (testName.startsWith("TC12")) return STATE;
        return null;
    }

    private static boolean allRequiredFieldsValid(
            String country,
            By excludedField) {
        // State bắt buộc khi chọn United States.
        List<By> fields = new ArrayList<>(List.of(
                EMAIL, FIRST_NAME, LAST_NAME, COMPANY,
                PHONE, COUNTRY, USE_CASE
        ));
        if ("United States".equalsIgnoreCase(country)) {
            fields.add(STATE);
        }

        for (By locator : fields) {
            if (locator.equals(excludedField)) {
                continue;
            }
            WebElement element = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(locator)
            );
            if (!isValid(element)) {
                return false;
            }
        }
        return true;
    }

    // Đọc kết quả validation HTML của trường.
    private static boolean isValid(WebElement element) {
        return (Boolean) js.executeScript(
                "return arguments[0].checkValidity();", element
        );
    }

    private static boolean validityState(
            WebElement element,
            String state) {
        return (Boolean) js.executeScript(
                "return arguments[0].validity[arguments[1]];",
                element,
                state
        );
    }

    private static boolean isRequired(WebElement element) {
        return (Boolean) js.executeScript(
                "return arguments[0].required;", element
        );
    }

    private static String selectedText(By locator) {
        return new Select(wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        )).getFirstSelectedOption().getText().trim();
    }

    private static void enter(WebElement element, String text) {
        element.clear();
        if (!text.isBlank()) {
            element.sendKeys(text);
        }
    }

    private static String value(
            Map<String, String> data,
            String columnName) {
        return data.getOrDefault(normalize(columnName), "");
    }

    private static String normalize(String text) {
        return text.trim().toLowerCase(Locale.ROOT);
    }

    private static void report(
            boolean result,
            String testName,
            String expected,
            String actual) {
        if (result) {
            passed++;
            System.out.println("PASS: " + testName
                    + " | Mong đợi: " + expected
                    + " | Thực tế: " + actual);
        } else {
            failed++;
            System.out.println("FAIL: " + testName
                    + " | Mong đợi: " + expected
                    + " | Thực tế: " + actual);
        }
    }
}
