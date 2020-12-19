package org.autex.supplyer;

import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class AutexTask extends SupplierTask {
    private final File cobraFile;

    public AutexTask(File cobraFile) {
        this.cobraFile = cobraFile;
    }

    /*@Override
    protected List<String[]> build(InputStream... inputStream) throws IOException {
        List<String[]> content = getTemplate();
        HSSFWorkbook wb = new HSSFWorkbook(inputStream[0]);
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFDataFormatter df = new HSSFDataFormatter(new Locale("hu"));
        for (int rowIndex = 7; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            HSSFRow row = sheet.getRow(rowIndex);
            if (row.getCell(0) == null) {
                break;
            }

            String[] csvRow = getRowTemplate(content.get(0).length);
            Optional.ofNullable(row.getCell(0)).ifPresent(cell -> csvRow[2] = df.formatCellValue(cell));
            Optional.ofNullable(row.getCell(3)).ifPresent(cell -> csvRow[3] = df.formatCellValue(cell));
            Optional.ofNullable(row.getCell(8)).ifPresent(cell -> {
                csvRow[14] = df.formatCellValue(cell);
                csvRow[13] = "0".equals(csvRow[14]) ? "0" : "1";
            });
            Optional.ofNullable(row.getCell(15)).ifPresent(cell -> csvRow[25] = df.formatCellValue(cell));
            content.add(csvRow);
        }

        return content;
    }*/

    @Override
    protected List<String[]> call() throws Exception {
        try (InputStream cobraStream = new FileInputStream(cobraFile)) {
            List<String[]> content = getTemplate();
            HSSFWorkbook wb = new HSSFWorkbook(cobraStream);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFDataFormatter df = new HSSFDataFormatter(new Locale("hu"));
            for (int rowIndex = 7; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                HSSFRow row = sheet.getRow(rowIndex);
                if (row.getCell(0) == null) {
                    break;
                }

                String[] csvRow = getRowTemplate(content.get(0).length);
                Optional.ofNullable(row.getCell(0)).ifPresent(cell -> csvRow[2] = df.formatCellValue(cell));
                Optional.ofNullable(row.getCell(3)).ifPresent(cell -> csvRow[3] = df.formatCellValue(cell));
                Optional.ofNullable(row.getCell(8)).ifPresent(cell -> {
                    csvRow[14] = df.formatCellValue(cell);
                    csvRow[13] = "0".equals(csvRow[14]) ? "0" : "1";
                });
                Optional.ofNullable(row.getCell(15)).ifPresent(cell -> csvRow[25] = df.formatCellValue(cell));
                content.add(csvRow);
            }

            return content;
        }
    }
}
