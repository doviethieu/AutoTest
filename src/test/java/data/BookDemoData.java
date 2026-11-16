package data;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import UI.BookDemoUI;

public final class BookDemoData {
    public static final Set<String> CHECKS = Set.of("SUBMIT", "EMAIL_VALIDITY", "BUSINESS_EMAIL",
            "MISSING", "DEFAULT_SELECT", "OPTIONS", "OPTIONAL", "TEXT", "LENGTH", "RECOVERY",
            "VALID", "COUNTRY_CHANGE", "INITIAL_FORM", "REQUIRED_FIELDS", "SELECT_RECOVERY",
            "EMAIL_CHANGE", "COUNTRY_SHOW", "COUNTRY_ROUNDTRIP", "SELECT", "REFRESH");

    private BookDemoData() { }

    public record Case(Map<String, String> values) {
        public String get(String name) { return values.getOrDefault(name, ""); }
        public boolean expectedBoolean() {
            String expected = get("Expected Result");
            if (!expected.equals("true") && !expected.equals("false"))
                throw new IllegalArgumentException("Expected Result phải là true/false: " + this);
            return Boolean.parseBoolean(expected);
        }
        @Override public String toString() { return get("Test Case"); }
    }

    public static Object[][] read() throws IOException {
        InputStream input = BookDemoData.class.getResourceAsStream("/BookDemoDay06Data.xlsx");
        if (input == null) throw new IOException("Không tìm thấy BookDemoDay06Data.xlsx trong resources");
        DataFormatter formatter = new DataFormatter();
        List<Object[]> cases = new ArrayList<>();
        Set<String> names = new HashSet<>();
        // Đóng cả stream và workbook; giữ nguyên khoảng trắng trong dữ liệu nhập.
        try (input; Workbook workbook = WorkbookFactory.create(input)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            if (header == null) throw new IOException("Sheet1 thiếu tiêu đề");
            List<String> columns = new ArrayList<>();
            for (int c = 0; c < header.getLastCellNum(); c++) {
                String name = formatter.formatCellValue(header.getCell(c)).trim();
                if (name.isEmpty() || columns.contains(name)) throw new IOException("Tiêu đề rỗng/trùng: " + name);
                columns.add(name);
            }
            for (String name : List.of("Test Case", "Check", "Target Field", "Validity", "Expected Result",
                    "Next Country", "Input Length", "Next Email", "Submit With", "Initial Email State"))
                if (!columns.contains(name)) throw new IOException("Thiếu cột " + name);
            for (String name : BookDemoUI.FIELDS.keySet())
                if (!columns.contains(name)) throw new IOException("Thiếu cột dữ liệu " + name);
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                Map<String, String> values = new LinkedHashMap<>();
                for (int c = 0; c < columns.size(); c++)
                    values.put(columns.get(c), formatter.formatCellValue(row.getCell(c)));
                if (values.values().stream().allMatch(String::isBlank)) continue;
                Case data = new Case(Map.copyOf(values));
                if (data.toString().isBlank() || !names.add(data.toString()))
                    throw new IOException("Tên test rỗng/trùng ở dòng " + (r + 1));
                if (!CHECKS.contains(data.get("Check"))) throw new IOException("Check không hợp lệ ở " + data);
                if (!data.get("Target Field").isBlank()) BookDemoUI.field(data.get("Target Field"));
                if (data.get("Expected Result").isBlank()) throw new IOException("Thiếu Expected Result: " + data);
                validate(data);
                cases.add(new Object[]{data});
            }
        }
        if (cases.isEmpty()) throw new IOException("Excel không có dữ liệu test");
        return cases.toArray(Object[][]::new);
    }

    private static void validate(Case data) {
        String check = data.get("Check");
        switch (check) {
            case "SUBMIT", "DEFAULT_SELECT", "TEXT", "INITIAL_FORM", "SELECT", "REFRESH" -> { }
            case "LENGTH" -> {
                int count = Integer.parseInt(data.get("Input Length"));
                int expected = Integer.parseInt(data.get("Expected Result"));
                if (count < 0 || count > 10000 || expected < 0 || expected > count)
                    throw new IllegalArgumentException("Độ dài không hợp lệ: " + data);
            }
            default -> data.expectedBoolean();
        }
        if (!Set.of("SUBMIT", "INITIAL_FORM", "REQUIRED_FIELDS", "REFRESH").contains(check)
                && data.get("Target Field").isBlank())
            throw new IllegalArgumentException("Thiếu Target Field: " + data);
        if (check.equals("EMAIL_VALIDITY") && !Set.of("valueMissing", "typeMismatch").contains(data.get("Validity")))
            throw new IllegalArgumentException("Validity không hợp lệ: " + data);
        if (check.equals("EMAIL_CHANGE") && (data.get("Next Email").isBlank()
                || !Set.of("FORMAT_ERROR", "BLOCKED", "ACCEPTED").contains(data.get("Initial Email State"))))
            throw new IllegalArgumentException("Thiếu Next Email hoặc sai Initial Email State: " + data);
        if (Set.of("COUNTRY_CHANGE", "COUNTRY_SHOW", "COUNTRY_ROUNDTRIP").contains(check)
                && data.get("Next Country").isBlank())
            throw new IllegalArgumentException("Thiếu Next Country: " + data);
        if (!Set.of("", "CLICK", "ENTER").contains(data.get("Submit With")))
            throw new IllegalArgumentException("Submit With phải là CLICK hoặc ENTER: " + data);
    }
}
