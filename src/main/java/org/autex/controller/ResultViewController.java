package org.autex.controller;

import com.opencsv.CSVWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.autex.model.MetaData;
import org.autex.model.Product;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;

import java.io.*;
import java.util.List;
import java.util.Optional;

public class ResultViewController {
    @FXML Region veil;
    @FXML ProgressIndicator progressIndicator;
    @FXML TableView<Product> tvResults;
    @FXML TableColumn<Product, String> colBrand;

    public void convert(Task<ObservableList<Product>> task) {
        veil.visibleProperty().bind(task.runningProperty());
        progressIndicator.visibleProperty().bind(task.runningProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());
        tvResults.itemsProperty().bind(task.valueProperty());
        colBrand.setCellValueFactory(productStringCellDataFeatures -> {
            Product product = productStringCellDataFeatures.getValue();
            Optional<MetaData> metaDataBrand = product.getMeta_data().stream().filter(metaData -> metaData.getKey().equals("_brand")).findFirst();
            return metaDataBrand.map(metaData -> new SimpleStringProperty(metaData.getValue())).orElse(null);

        });

        new Thread(task).start();
    }

    @FXML
    private void save() throws IOException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(tvResults.getScene().getWindow());
        if (file != null) {
            saveAsExcel(file);
        }
    }

    private void saveAsExcel(File file) throws IOException {
        // TODO:
        tvResults.getItems();
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet("data");
            Row rowHeader = sheet.createRow(0);
            rowHeader.createCell(0).setCellValue("Cikkszám");
            rowHeader.createCell(1).setCellValue("Név");
            Row rowData = sheet.createRow(1);
            rowData.createCell(0).setCellValue("ABC");
            rowData.createCell(1).setCellValue("Valami");
            CellReference topLeft = new CellReference(sheet.getRow(0).getCell(0));
            CellReference bottomRight = new CellReference(sheet.getRow(1).getCell(1));
            AreaReference tableArea = wb.getCreationHelper().createAreaReference(topLeft, bottomRight);
            XSSFTable table = sheet.createTable(tableArea);
            table.setDisplayName("Table1");
            CTTable ctTable = table.getCTTable();
            ctTable.addNewTableStyleInfo();
            XSSFTableStyleInfo style = (XSSFTableStyleInfo)table.getStyle();
            style.setName("TableStyleMedium2");
            style.setShowColumnStripes(false);
            style.setShowRowStripes(true);
            ctTable.addNewAutoFilter().setRef(tableArea.formatAsString());
//            ctTable.setId(1);
//            ctTable.setName("data_table");
//            ctTable.setRef("A1:B2");
//            CTTableColumns columns = ctTable.addNewTableColumns();
//            columns.setCount(2);
//            CTTableColumn col1 = columns.addNewTableColumn();
//            col1.setId(1);
//            col1.setName("sku");
//            CTTableColumn col2 = columns.addNewTableColumn();
//            col2.setId(2);
//            col1.setName("name");

            try (FileOutputStream fileWriter = new FileOutputStream(file)) {
                wb.write(fileWriter);
            }
        }
    }

    private String stringify(List<String[]> content) throws IOException {
        try (StringWriter stringWriter = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(stringWriter, ',', '"', '"', "\r\n")) {
            csvWriter.writeAll(content, false);
            return stringWriter.toString();
        }
    }
}
