package org.autex.supplier;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.autex.model.Product;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.Optional;

public class AutexSupplierTask extends Task<ObservableList<Product>> {
    private final File cobraFile;

    public AutexSupplierTask(File cobraFile) {
        this.cobraFile = cobraFile;
    }

    @Override
    protected ObservableList<Product> call() throws Exception {
        try (InputStream cobraStream = new FileInputStream(cobraFile)) {
            ObservableList<Product> products = FXCollections.observableArrayList();
            HSSFWorkbook wb = new HSSFWorkbook(cobraStream);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFDataFormatter df = new HSSFDataFormatter(new Locale("hu"));
            for (int rowIndex = 7; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                HSSFRow row = sheet.getRow(rowIndex);
                if (row.getCell(0) == null) {
                    break;
                }

                Product product = new Product();
                Optional.ofNullable(row.getCell(0)).ifPresent(cell -> product.setSku(df.formatCellValue(cell)));
                Optional.ofNullable(row.getCell(3)).ifPresent(cell -> product.setName(df.formatCellValue(cell)));
                Optional.ofNullable(row.getCell(8)).ifPresent(cell -> product.setStock_quantity(Integer.parseInt(df.formatCellValue(cell))));
                Optional.ofNullable(row.getCell(15)).ifPresent(cell -> product.setPrice(df.formatCellValue(cell)));
                products.add(product);
            }

            return products;
        }
    }
}
