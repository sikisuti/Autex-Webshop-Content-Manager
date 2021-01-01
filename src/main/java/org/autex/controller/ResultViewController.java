package org.autex.controller;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.autex.App;
import org.autex.model.Product;
import org.autex.remote.CreateTask;
import org.autex.remote.RemoteService;
import org.autex.remote.RemoteTask;
import org.autex.remote.SyncTask;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;

import java.io.*;

public class ResultViewController {
    @FXML StackPane busyVeil;
    @FXML ProgressIndicator progressIndicator;
    @FXML Label lbProgressMessage;
    @FXML TableView<Product> tvResults;
    String supplierName;

    public void convert(Task<ObservableList<Product>> task) {
        busyVeil.visibleProperty().bind(task.runningProperty());
        progressIndicator.visibleProperty().bind(task.runningProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());
        lbProgressMessage.textProperty().bind(task.titleProperty());
        tvResults.itemsProperty().bind(task.valueProperty());
        supplierName = task.getClass().getName();
        task.exceptionProperty().addListener((observableValue, throwable, t1) -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba");
            alert.setHeaderText(null);
            alert.setContentText(t1.getMessage());
            alert.showAndWait();
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

    @FXML
    private void sync() {
        startService(SyncTask.class);
    }

    @FXML
    private void upload() throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/fieldSelector.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Mező választó");
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    private void startService(Class<? extends RemoteTask> clazz) {
        RemoteService service = new RemoteService(tvResults.getItems(), clazz);
        busyVeil.visibleProperty().bind(service.runningProperty());
        progressIndicator.visibleProperty().bind(service.runningProperty());
        progressIndicator.progressProperty().bind(service.progressProperty());
        lbProgressMessage.textProperty().bind(service.titleProperty());
        new Thread(service).start();
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
                rowData.createCell(1).setCellValue(product.nameProperty().get());
                rowData.createCell(2).setCellValue(product.getPrice());
                rowData.createCell(3).setCellValue(product.getStock_quantity());
                rowData.createCell(4).setCellValue(product.getWeight());
                rowData.createCell(5).setCellValue(product.getBrand());
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
