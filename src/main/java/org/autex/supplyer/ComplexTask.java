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

    /*@Override
    protected List<String[]> build(InputStream... inputStream) throws IOException {
        Map<String, XSSFRow> allItems = loadAllItems(inputStream[0]);
        return mergeItems(allItems, inputStream[1]);
    }*/

    private List<String[]> mergeItems(Map<String, XSSFRow> allItems) throws IOException {
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

                XSSFRow rowFromAllItems = allItems.get(id);
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

    private Map<String, XSSFRow> loadAllItems(InputStream inputStream) throws IOException {
        Map<String, XSSFRow> itemList = new HashMap<>();

        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
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

        return itemList;
    }

    @Override
    protected List<String[]> call() throws Exception {
        try (InputStream masterDataStream = new FileInputStream(masterDataFile)) {
            Map<String, XSSFRow> allItems = loadAllItems(masterDataStream);
            return mergeItems(allItems);
        }
    }
}
