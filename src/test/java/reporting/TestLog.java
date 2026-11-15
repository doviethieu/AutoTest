package reporting;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;

public final class TestLog {
    private static final String CHECKS = "reporting.checks";

    private TestLog() {
    }

    public record Check(String status, String label, String expected, String actual) {
    }

    public static void step(String message) {
        record("INFO", message, "", "");
    }

    // Giữ nguyên cơ chế Assert của TestNG và ghi thêm mong đợi/thực tế.
    public static void assertEquals(Object actual, Object expected, String label) {
        try {
            Assert.assertEquals(actual, expected, label);
            record("PASS", label, String.valueOf(expected), String.valueOf(actual));
        } catch (AssertionError error) {
            record("FAIL", label, String.valueOf(expected), String.valueOf(actual));
            throw error;
        }
    }

    public static void assertTrue(boolean actual, String label) {
        assertEquals(actual, true, label);
    }

    public static void assertGreaterThan(int actual, int minimum, String label) {
        try {
            Assert.assertTrue(actual > minimum, label);
            record("PASS", label, "> " + minimum, String.valueOf(actual));
        } catch (AssertionError error) {
            record("FAIL", label, "> " + minimum, String.valueOf(actual));
            throw error;
        }
    }

    private static void record(String status, String label, String expected, String actual) {
        ITestResult result = Reporter.getCurrentTestResult();
        if (result != null) {
            List<Check> checks = new ArrayList<>(checksFor(result));
            checks.add(new Check(status, label, expected, actual));
            result.setAttribute(CHECKS, checks);
        }
        String message = status + ": " + label;
        if (!"INFO".equals(status)) {
            message += " | Mong đợi: " + expected + " | Thực tế: " + actual;
        }
        Reporter.log(message, true);
    }

    static List<Check> checksFor(ITestResult result) {
        Object attribute = result.getAttribute(CHECKS);
        if (attribute instanceof List<?> entries) {
            return entries.stream().filter(Check.class::isInstance)
                    .map(Check.class::cast).toList();
        }
        return List.of();
    }
}
