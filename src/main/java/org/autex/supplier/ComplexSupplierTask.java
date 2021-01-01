package org.autex.supplier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.*;
import org.autex.exception.DuplicateSkuException;
import org.autex.model.MetaData;
import org.autex.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComplexSupplierTask extends Task<ObservableList<Product>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComplexSupplierTask.class);

    private final File masterDataFile;
    private final File stockFile;

    public ComplexSupplierTask(File masterDataFile, File stockFile) {
        this.masterDataFile = masterDataFile;
        this.stockFile = stockFile;
    }

    @Override
    protected ObservableList<Product> call() throws Exception {
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
                    Optional.ofNullable(rowFromAllItems.getCell(0)).ifPresent(cell -> product.setSku(df.formatCellValue(cell)));
                    if (product.getSku() != null && !product.getSku().isEmpty()) {
                        if (processedItems.contains(product.getSku())) {
                            throw new DuplicateSkuException(product.getSku() + " row: " + rowIndex);
                        } else {
                            processedItems.add(product.getSku());
                        }
                    } else {
                        continue;
                    }

                    product.setName(id);
                    Optional.ofNullable(rowFromAllItems.getCell(4)).ifPresent(cell -> product.setPrice(df.formatCellValue(cell)));
                    Optional.ofNullable(row.getCell(1)).ifPresent(cell -> {
                        MetaData metaData = new MetaData();
                        product.getMeta_data().add(metaData);
                        metaData.setKey("_brand");
                        metaData.setValue(df.formatCellValue(cell));
                    });
                    Optional.ofNullable(row.getCell(3)).ifPresent(cell -> product.setStock_quantity(Integer.parseInt(df.formatCellValue(cell))));

                    content.add(product);
                    LOGGER.info("Product merged: {}", product.getSku());
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
