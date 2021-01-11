package org.autex.supplier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.*;
import org.autex.exception.DuplicateSkuException;
import org.autex.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComplexSupplierTask extends SupplierTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComplexSupplierTask.class);

    private final File masterDataFile;
    private final File stockFile;

    public ComplexSupplierTask(File masterDataFile, File stockFile) {
        this.masterDataFile = masterDataFile;
        this.stockFile = stockFile;
    }

    @Override
    protected ObservableList<Product> doJob() throws Exception {
        return mergeMasterDataWithStock(loadMasterData());
    }

    private Map<String, XSSFRow> loadMasterData() throws IOException {
        Map<String, XSSFRow> itemList = new HashMap<>();
        updateTitle("1/4 Complex cikktörzs betöltése");
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
                updateProgress(rowIndex, sheet.getLastRowNum());
            }
        }

        updateTitle(null);
        return itemList;
    }

    private ObservableList<Product> mergeMasterDataWithStock(Map<String, XSSFRow> masterData) throws IOException {
        updateTitle("2/4 Complex készlet betöltés");
        try (InputStream stockStream = new FileInputStream(stockFile)) {
            ObservableList<Product> content = FXCollections.observableArrayList();
            XSSFWorkbook wb = new XSSFWorkbook(stockStream);
            DataFormatter df = new DataFormatter();
            XSSFSheet sheet = wb.getSheetAt(0);
            Set<String> processedItems = new HashSet<>();
            updateTitle("3/4 Complex készlet-cikktörzs illesztés");
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                XSSFRow row = sheet.getRow(rowIndex);
                XSSFCell firstCell = row.getCell(0);
                String id = df.formatCellValue(firstCell);
                if (firstCell == null) {
                    break;
                }

                XSSFRow rowFromAllItems = masterData.get(id);
                if (rowFromAllItems != null) {
                    XSSFCell skuCell = rowFromAllItems.getCell(0);
                    if (skuCell != null) {
                        Product product = new Product(df.formatCellValue(skuCell));
                        if (processedItems.contains(product.getSku())) {
                            throw new DuplicateSkuException(product.getSku() + " row: " + rowIndex);
                        } else {
                            processedItems.add(product.getSku());
                        }

                        product.setField(Product.NAME, id);
                        Optional.ofNullable(rowFromAllItems.getCell(4)).ifPresent(cell -> product.setField(Product.PRICE, df.formatCellValue(cell)));
                        Optional.ofNullable(row.getCell(1)).ifPresent(cell -> product.setField(Product.BRAND, df.formatCellValue(cell)));
                        Optional.ofNullable(row.getCell(3)).ifPresent(cell -> product.setField(Product.STOCK_QUANTITY, Integer.parseInt(df.formatCellValue(cell))));

                        content.add(product);
                    }
                }

                updateProgress(rowIndex, sheet.getLastRowNum());
            }

            return content;
        }
    }
}
