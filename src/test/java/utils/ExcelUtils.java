package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public final class ExcelUtils {
    private ExcelUtils() { }

    public static List<Map<String, String>> readExcelData(String filePath, String sheetName) {
        String resourcePath = filePath.startsWith("/") ? filePath : "/" + filePath;
        List<Map<String, String>> data = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        // Đọc từ resources để không phụ thuộc đường dẫn riêng của từng máy.
        try (InputStream input = ExcelUtils.class.getResourceAsStream(resourcePath)) {
            if (input == null) throw new IllegalArgumentException("Không tìm thấy file trong resources: " + filePath);
            try (Workbook workbook = WorkbookFactory.create(input)) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) throw new IllegalArgumentException("Không tìm thấy sheet: " + sheetName);
                Row header = sheet.getRow(0);
                if (header == null || header.getLastCellNum() <= 0)
                    throw new IllegalArgumentException("Sheet thiếu dòng tiêu đề: " + sheetName);

                List<String> columns = new ArrayList<>();
                for (int c = 0; c < header.getLastCellNum(); c++) {
                    String name = formatter.formatCellValue(header.getCell(c)).trim();
                    if (name.isEmpty() || columns.contains(name))
                        throw new IllegalArgumentException("Tên cột rỗng hoặc trùng: " + name);
                    columns.add(name);
                }

                for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null) continue;
                    Map<String, String> rowData = new LinkedHashMap<>();
                    for (int c = 0; c < columns.size(); c++)
                        rowData.put(columns.get(c), formatter.formatCellValue(row.getCell(c)));
                    if (rowData.values().stream().allMatch(String::isBlank)) continue;
                    data.add(rowData);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Không đọc được file Excel: " + filePath, e);
        }
        if (data.isEmpty()) throw new IllegalArgumentException("Sheet không có dữ liệu test: " + sheetName);
        return data;
    }
}
