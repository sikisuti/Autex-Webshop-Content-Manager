package org.autex.supplier;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.autex.exception.GeneralException;
import org.autex.model.Product;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class AutexSupplierTask extends SupplierTask {
    private final File cobraFile;

    private static final String COL_NAME_SKU = "Cikkszám";
    private static final String COL_NAME_NAME = "Megnevezés";
    private static final String COL_NAME_STOCK_QUANTITY = "Készlet";
    private static final String COL_NAME_PRICE = "Nyilv.Ár";

    HSSFDataFormatter df = new HSSFDataFormatter(new Locale("hu"));
    private final Map<String, Integer> columnLocations;

    public AutexSupplierTask(File cobraFile) {
        this.cobraFile = cobraFile;

        columnLocations = new HashMap<>();
        columnLocations.put(COL_NAME_SKU, null);
        columnLocations.put(COL_NAME_NAME, null);
        columnLocations.put(COL_NAME_STOCK_QUANTITY, null);
        columnLocations.put(COL_NAME_PRICE, null);
    }

    @Override
    protected ObservableList<Product> doJob() throws Exception {
        try (InputStream cobraStream = new FileInputStream(cobraFile)) {
            ObservableList<Product> products = FXCollections.observableArrayList();
            HSSFWorkbook wb = new HSSFWorkbook(cobraStream);
            HSSFSheet sheet = wb.getSheetAt(0);

            int headerRowIndex = findHeaderRow(sheet);
            findColumns(sheet.getRow(headerRowIndex));

            for (int rowIndex = headerRowIndex + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                HSSFRow row = sheet.getRow(rowIndex);

                HSSFCell skuCell = row.getCell(columnLocations.get(COL_NAME_SKU));
                if (skuCell == null) {
                    break;
                }

                Product product = new Product(df.formatCellValue(skuCell));
                Optional.ofNullable(row.getCell(columnLocations.get(COL_NAME_NAME))).ifPresent(cell -> product.setField(Product.NAME, df.formatCellValue(cell)));
                Optional.ofNullable(row.getCell(columnLocations.get(COL_NAME_STOCK_QUANTITY))).ifPresent(cell -> product.setField(Product.STOCK_QUANTITY, Float.parseFloat(df.formatCellValue(cell).replace(",", "."))));
                Optional.ofNullable(row.getCell(columnLocations.get(COL_NAME_PRICE))).ifPresent(cell -> product.setField(Product.PRICE, df.formatCellValue(cell)));
                products.add(product);
            }

            return products;
        }
    }

    private int findHeaderRow(HSSFSheet sheet) {
        for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            HSSFRow row = sheet.getRow(rowIndex);
            Cell cell = row.getCell(0);
            if (cell != null && columnLocations.containsKey(df.formatCellValue(cell))) {
                return rowIndex;
            }
        }

        throw new GeneralException("Táblázat fejléce nem található");
    }

    private void findColumns(HSSFRow headerRow) {
        for (int colIndex = 0; colIndex <= headerRow.getLastCellNum(); colIndex++) {
            Cell cell = headerRow.getCell(colIndex);
            if (cell != null) {
                String cellValue = df.formatCellValue(cell);
                if (columnLocations.containsKey(cellValue)) {
                    columnLocations.put(cellValue, colIndex);
                }
            }
        }
    }
}
