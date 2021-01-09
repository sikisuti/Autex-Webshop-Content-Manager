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
                    Product product = new Product();
                    Optional.ofNullable(rowFromAllItems.getCell(0)).ifPresent(cell -> product.setField(Product.SKU, df.formatCellValue(cell)));
                    if (product.getField(Product.SKU) != null && !product.getField(Product.SKU).isEmpty()) {
                        if (processedItems.contains(product.getField(Product.SKU))) {
                            throw new DuplicateSkuException(product.getField(Product.SKU) + " row: " + rowIndex);
                        } else {
                            processedItems.add(product.getField(Product.SKU));
                        }
                    } else {
                        continue;
                    }

                    product.setField(Product.NAME, id);
                    Optional.ofNullable(rowFromAllItems.getCell(4)).ifPresent(cell -> product.setField(Product.PRICE, df.formatCellValue(cell)));
                    Optional.ofNullable(row.getCell(1)).ifPresent(cell -> product.setField(Product.BRAND, df.formatCellValue(cell)));
                    Optional.ofNullable(row.getCell(3)).ifPresent(cell -> product.setField(Product.STOCK_QUANTITY, df.formatCellValue(cell)));

                    content.add(product);
                    LOGGER.info("Product merged: {}", product.getField(Product.SKU));
                }

                updateProgress(rowIndex, sheet.getLastRowNum());
            }

            updateTitle("4/4 Szinkronizálás a webshop-al");
            updateProgress(0, 1);
//            syncProducts(content);

            updateTitle(null);
            return content;
        }
    }
}
