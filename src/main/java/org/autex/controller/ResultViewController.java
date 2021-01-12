package org.autex.controller;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.autex.dialog.FieldSelectorDialog;
import org.autex.model.Product;
import org.autex.service.RemoteService;
import org.autex.service.SyncService;
import org.autex.service.UploadService;
import org.autex.util.Configuration;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;

public class ResultViewController {
    @FXML StackPane busyVeil;
    @FXML ProgressIndicator progressIndicator;
    @FXML Label lbProgressMessage;
    @FXML TableView<Product> tvResults;
    String supplierName;
    private String authHeader;

    public void convert(Task<ObservableList<Product>> task) {
        busyVeil.visibleProperty().bind(task.runningProperty());
        progressIndicator.visibleProperty().bind(task.runningProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());
        lbProgressMessage.textProperty().bind(task.titleProperty());
        tvResults.itemsProperty().bind(task.valueProperty());
//        task.valueProperty().addListener((observableValue, products, t1) -> startService(new SyncService(tvResults.getItems(), getAuthHeader())));
        supplierName = task.getClass().getName();
        task.exceptionProperty().addListener((observableValue, throwable, t1) -> showAlert(t1.getMessage()));

        new Thread(task).start();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hiba");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void save() throws IOException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(tvResults.getScene().getWindow());
        if (file != null) {
            saveAsExcel(file);
        }
    }

    @FXML
    private void sync() {
        startService(new SyncService(tvResults.getItems(), getAuthHeader()));
    }

    @FXML
    private void upload() {
        if (tvResults.getItems().stream().anyMatch(p -> !p.isReadyToUpload())) {
            showAlert("Szinkronizálatlan elemek vannak a listában");
            return;
        }

        FieldSelectorDialog dialog = new FieldSelectorDialog(tvResults.getItems().get(0).getAllFieldNames());
        Optional<Set<String>> selectedFields = dialog.showAndWait();
        selectedFields.ifPresent(strings -> startService(new UploadService(tvResults.getItems(), getAuthHeader(), strings)));
    }

    private void startService(RemoteService service) {
        busyVeil.visibleProperty().bind(service.runningProperty());
        progressIndicator.visibleProperty().bind(service.runningProperty());
        progressIndicator.progressProperty().bind(service.progressProperty());
        lbProgressMessage.textProperty().bind(service.titleProperty());
        new Thread(service).start();
    }

    private String getAuthHeader() {
        if (authHeader == null) {
            String key = Configuration.getStringProperty("key");
            String secretKey = Configuration.getStringProperty("secretKey");
            String auth = key + ":" + secretKey;
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
            authHeader = "Basic " + new String(encodedAuth);
        }

        return authHeader;
    }

    private void saveAsExcel(File file) throws IOException {
        ObservableList<Product> products = tvResults.getItems();
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet(supplierName);
            Row rowHeader = sheet.createRow(0);
            rowHeader.createCell(0).setCellValue("Cikkszám");
            rowHeader.createCell(1).setCellValue("Név");
            rowHeader.createCell(2).setCellValue("Ár");
            rowHeader.createCell(3).setCellValue("Készlet");
            rowHeader.createCell(4).setCellValue("Súly");
            rowHeader.createCell(5).setCellValue("Gyártó");

            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                Row rowData = sheet.createRow(i + 1);
                rowData.createCell(0).setCellValue(product.getSku());
                rowData.createCell(1).setCellValue(product.getField(Product.NAME));
                rowData.createCell(2).setCellValue(product.getField(Product.PRICE));
                rowData.createCell(3).setCellValue(product.getField(Product.STOCK_QUANTITY, Integer.class));
                rowData.createCell(4).setCellValue(product.getField(Product.WEIGHT));
                rowData.createCell(5).setCellValue(product.getField(Product.BRAND));
            }

            CellReference topLeft = new CellReference(sheet.getRow(0).getCell(0));
            CellReference bottomRight = new CellReference(sheet.getRow(products.size()).getCell(5));
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

            try (FileOutputStream fileWriter = new FileOutputStream(file)) {
                wb.write(fileWriter);
            }
        }
    }
}
