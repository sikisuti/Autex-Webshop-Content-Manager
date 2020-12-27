package org.autex.supplyer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.autex.model.MetaData;
import org.autex.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

public class FileTask extends SupplyerTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileTask.class);

    private final File sourceFile;

    public FileTask(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    @Override
    protected ObservableList<Product> call() throws Exception {
        updateTitle("Fájl betöltés");
        try (InputStream stockStream = new FileInputStream(sourceFile)) {
            ObservableList<Product> content = FXCollections.observableArrayList();
            XSSFWorkbook wb = new XSSFWorkbook(stockStream);
            DataFormatter df = new DataFormatter();
            XSSFSheet sheet = wb.getSheetAt(0);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                XSSFRow row = sheet.getRow(rowIndex);

                Product product = new Product();
                Optional.ofNullable(row.getCell(0)).ifPresent(cell -> product.setSku(df.formatCellValue(cell)));
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
