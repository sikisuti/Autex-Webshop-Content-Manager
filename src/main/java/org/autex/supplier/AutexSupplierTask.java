package org.autex.supplier;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FractionFormat;
import org.autex.exception.GeneralException;
import org.autex.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class AutexSupplierTask extends SupplierTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutexSupplierTask.class);
    private final File cobraFile;

    private static final String COL_NAME_SKU = "Cikkszám";
    private static final String COL_NAME_NAME = "Megnevezés";
    private static final String COL_NAME_STOCK_QUANTITY = "Készlet";
    private static final String COL_NAME_PRICE = "Nyilv.Ár";

    HSSFDataFormatter df = new HSSFDataFormatter(new Locale("en"));
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
                String sku = df.formatCellValue(skuCell);
                if (skuCell == null || sku.isEmpty()) {
                    break;
                }

                Product product = new Product(sku);
                Optional.ofNullable(row.getCell(columnLocations.get(COL_NAME_NAME))).ifPresent(cell -> product.setField(Product.NAME, df.formatCellValue(cell)));
                int finalRowIndex = rowIndex;
                Optional.ofNullable(row.getCell(columnLocations.get(COL_NAME_STOCK_QUANTITY))).ifPresent(cell -> {
                    double cellRawValue = cell.getNumericCellValue();
                    String cellValue;
                    if(cellRawValue == (int) cellRawValue) {
                        cellValue = Long.toString((long) cellRawValue);
                    } else {
                        cellValue = Double.toString(cellRawValue);
                    }

                    try {
                        product.setField(Product.STOCK_QUANTITY, cellValue);
                    } catch (Exception e) {
                        LOGGER.info("Error in row {}", finalRowIndex);
                    }
                });

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
