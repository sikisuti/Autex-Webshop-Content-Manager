package org.autex.supplyer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.*;

public class ComplexTask extends SupplierTask {
    private final File masterDataFile;
    private final File stockFile;

    public ComplexTask(File masterDataFile, File stockFile) {
        this.masterDataFile = masterDataFile;
        this.stockFile = stockFile;
    }

    @Override
    protected List<String[]> call() throws Exception {
            return mergeMasterDataWithStock(loadMasterData());
    }

    private Map<String, XSSFRow> loadMasterData() throws IOException {
        Map<String, XSSFRow> itemList = new HashMap<>();

        try (InputStream masterDataStream = new FileInputStream(masterDataFile)) {
            XSSFWorkbook wb = new XSSFWorkbook(masterDataStream);
            DataFormatter df = new DataFormatter();
            XSSFSheet sheet = wb.getSheetAt(0);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                XSSFRow row = sheet.getRow(rowIndex);
                XSSFCell firstCell = row.getCell(1);
                String id = df.formatCellValue(firstCell);
                if (firstCell == null) {
                    break;
                }

                itemList.put(id, row);
            }
        }

        return itemList;
    }

    private List<String[]> mergeMasterDataWithStock(Map<String, XSSFRow> masterData) throws IOException {
        try (InputStream stockStream = new FileInputStream(stockFile)) {
            List<String[]> content = getTemplate();
            XSSFWorkbook wb = new XSSFWorkbook(stockStream);
            DataFormatter df = new DataFormatter();
            XSSFSheet sheet = wb.getSheetAt(0);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                XSSFRow row = sheet.getRow(rowIndex);
                XSSFCell firstCell = row.getCell(0);
                String id = df.formatCellValue(firstCell);
                if (firstCell == null) {
                    break;
                }

                XSSFRow rowFromAllItems = masterData.get(id);
                if (rowFromAllItems != null) {
                    String[] csvRow = getRowTemplate(content.get(0).length);
                    Optional.ofNullable(rowFromAllItems.getCell(0)).ifPresent(cell -> csvRow[2] = df.formatCellValue(cell));
                    csvRow[3] = id;
                    Optional.ofNullable(rowFromAllItems.getCell(4)).ifPresent(cell -> csvRow[25] = df.formatCellValue(cell));
                    Optional.ofNullable(row.getCell(1)).ifPresent(cell -> csvRow[47] = df.formatCellValue(cell));
                    Optional.ofNullable(row.getCell(3)).ifPresent(cell -> csvRow[14] = df.formatCellValue(cell));

                    content.add(csvRow);
                }
            }

            return content;
        }
    }
}
