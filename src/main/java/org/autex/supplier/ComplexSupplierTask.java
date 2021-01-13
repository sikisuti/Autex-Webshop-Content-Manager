package org.autex.supplier;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.*;
import org.autex.exception.DuplicateSkuException;
import org.autex.exception.UnrecognizableGroupException;
import org.autex.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComplexSupplierTask extends SupplierTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComplexSupplierTask.class);

    private final File masterDataFile;
    private final File stockFile;
    private final Properties categories;

    public ComplexSupplierTask(File masterDataFile, File stockFile) {
        this.masterDataFile = masterDataFile;
        this.stockFile = stockFile;
        this.categories = new Properties();

        try (FileInputStream fis = new FileInputStream("complex.properties");
             InputStreamReader input = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            this.categories.load(input);
        } catch (Exception ignored) {
        }
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

                XSSFRow rowFromMasterData = masterData.get(id);
                if (rowFromMasterData != null) {
                    XSSFCell skuCell = rowFromMasterData.getCell(0);
                    if (skuCell != null) {
                        Product product = new Product(df.formatCellValue(skuCell));
                        if (processedItems.contains(product.getSku())) {
                            throw new DuplicateSkuException(product.getSku() + " row: " + rowIndex);
                        } else {
                            processedItems.add(product.getSku());
                        }

                        product.setField(Product.NAME, id);
                        Optional.ofNullable(rowFromMasterData.getCell(4)).ifPresent(cell -> product.setField(Product.PRICE, df.formatCellValue(cell)));
                        Optional.ofNullable(row.getCell(1)).ifPresent(cell -> product.setField(Product.BRAND, df.formatCellValue(cell)));
                        Optional.ofNullable(row.getCell(3)).ifPresent(cell -> product.setField(Product.STOCK_QUANTITY, Integer.parseInt(df.formatCellValue(cell))));

                        try {
                            Optional.ofNullable(rowFromMasterData.getCell(5)).ifPresent(cell -> product.setField(Product.CATEGORY, parseCategories(df.formatCellValue(cell))));
                        } catch (UnrecognizableGroupException e) {
                            LOGGER.error(e.getMessage(), e);
                        }

                        content.add(product);
                    }
                }

                updateProgress(rowIndex, sheet.getLastRowNum());
            }

            return content;
        }
    }

    private String parseCategories(String groupCode) {
        List<String> categoryList = new ArrayList<>();
        int i = 0;
        while (i * 2 < groupCode.length()) {
            categoryList.add(parseGroupCode(groupCode.substring(0, (i * 2) + 2)));
            i++;
        }

        return String.join(".", categoryList);
    }

    private String parseGroupCode(String groupCode) {
        String category = categories.getProperty(groupCode);
        if (category != null) {
            return category;
        } else {
            category = categories.getProperty("-" + groupCode.substring(groupCode.length() - 2));
            return Optional.ofNullable(category)
                    .orElseThrow(() -> new UnrecognizableGroupException(groupCode));
        }
    }
}
