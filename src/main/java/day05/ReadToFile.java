package day05;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadToFile {
    public static void main(String[] args) throws Exception {
        try (InputStream file =
                     ReadToFile.class.getResourceAsStream("/dataLogin.xlsx")) {

            if (file == null) {
                throw new IllegalStateException(
                        "Không tìm thấy dataLogin.xlsx trong src/main/resources"
                );
            }

            try (Workbook workbook = new XSSFWorkbook(file)) {
                Sheet sheet = workbook.getSheetAt(0);
                DataFormatter formatter = new DataFormatter();

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) {
                        continue; // Bỏ qua dòng tiêu đề
                    }

                    String username =
                            formatter.formatCellValue(row.getCell(0)).trim();
                    String password =
                            formatter.formatCellValue(row.getCell(1)).trim();

                    if (!username.isEmpty() || !password.isEmpty()) {
                        System.out.println(
                                "Username: " + username
                                        + " | Password: " + password
                        );
                    }
                }
            }
        }
    }
}