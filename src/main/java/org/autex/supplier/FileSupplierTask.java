package org.autex.supplier;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.autex.exception.DuplicateSkuException;
import org.autex.model.MetaData;
import org.autex.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FileSupplierTask extends Task<ObservableList<Product>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSupplierTask.class);

    private final File sourceFile;

    public FileSupplierTask(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    @Override
    protected ObservableList<Product> call() throws Exception {
            return doJob();
    }

    private ObservableList<Product> doJob() throws IOException {
        updateTitle("Fájl betöltés");
        try (InputStream stockStream = new FileInputStream(sourceFile)) {
            ObservableList<Product> content = FXCollections.observableArrayList();
            XSSFWorkbook wb = new XSSFWorkbook(stockStream);
            Set<String> processedItems = new HashSet<>();
            DataFormatter df = new DataFormatter();
            XSSFSheet sheet = wb.getSheetAt(0);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                XSSFRow row = sheet.getRow(rowIndex);

                Product product = new Product();
                Optional.ofNullable(row.getCell(0)).ifPresent(cell -> product.setSku(df.formatCellValue(cell)));
                if (product.getSku() != null && !product.getSku().isEmpty()) {
                    if (processedItems.contains(product.getSku())) {
                        throw new DuplicateSkuException(product.getSku());
                    } else {
                        processedItems.add(product.getSku());
                    }
                } else {
                    continue;
                }

                Optional.ofNullable(row.getCell(1)).ifPresent(cell -> product.setName(df.formatCellValue(cell)));
                Optional.ofNullable(row.getCell(2)).ifPresent(cell -> product.setPrice(df.formatCellValue(cell)));
                Optional.ofNullable(row.getCell(3)).ifPresent(cell -> product.setStock_quantity(Integer.parseInt(df.formatCellValue(cell))));
                Optional.ofNullable(row.getCell(4)).ifPresent(cell -> product.setWeight(df.formatCellValue(cell)));
                Optional.ofNullable(row.getCell(5)).ifPresent(cell -> {
                    MetaData metaData = new MetaData();
                    metaData.setKey("_brand");
                    metaData.setValue(df.formatCellValue(cell));
                    product.getMeta_data().add(metaData);
                });

                content.add(product);
                updateProgress(rowIndex, sheet.getLastRowNum());
            }

            updateTitle(null);
            return content;
        }
    }
}
