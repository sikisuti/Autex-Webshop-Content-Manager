package org.autex.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.apache.commons.collections4.ListUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.autex.model.Product;
import org.autex.supplyer.SyncTask;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;

import java.io.*;
import java.util.List;

public class ResultViewController {
    @FXML StackPane busyVeil;
    @FXML ProgressIndicator progressIndicator;
    @FXML Label lbProgressMessage;
    @FXML TableView<Product> tvResults;
//    @FXML TableColumn<String, Product> colStatus;
    String supplyerName;

    public void convert(Task<ObservableList<Product>> task) {
        busyVeil.visibleProperty().bind(task.runningProperty());
        progressIndicator.visibleProperty().bind(task.runningProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());
        lbProgressMessage.textProperty().bind(task.titleProperty());
        tvResults.itemsProperty().bind(task.valueProperty());
        supplyerName = task.getClass().getName();
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
        SyncTask task = new SyncTask(tvResults.getItems());
        busyVeil.visibleProperty().bind(task.runningProperty());
        progressIndicator.visibleProperty().bind(task.runningProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());
        lbProgressMessage.textProperty().bind(task.titleProperty());
//        tvResults.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
    }

    @FXML
    private void changeStatus() {
        List<List<Product>> groupedProducts = ListUtils.partition(tvResults.getItems(), 2);

        groupedProducts.get(0).get(0).setStatus(Product.Status.EXISTS);
    }

    private void saveAsExcel(File file) throws IOException {
        ObservableList<Product> products = tvResults.getItems();
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet(supplyerName);
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
