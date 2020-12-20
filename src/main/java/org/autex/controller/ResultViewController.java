package org.autex.controller;

import com.opencsv.CSVWriter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ResultViewController {
    @FXML Region veil;
    @FXML ProgressIndicator progressIndicator;
    @FXML TableView tvResults;
    List<String[]> rawData;

    public void convert(Task<List<String[]>> task) {
        veil.visibleProperty().bind(task.runningProperty());
        progressIndicator.visibleProperty().bind(task.runningProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());
        task.valueProperty().addListener((observableValue, strings, t1) -> {
            rawData = observableValue.getValue();
            renderData();
        });
        new Thread(task).start();
    }

    public void renderData() {
        setHeaders(rawData.get(0));
        fillTable(rawData);
    }

    private void setHeaders(String[] headers) {
        for (String header : headers) {
            TableColumn<Map, String> col = new TableColumn<>(header);
            col.setCellValueFactory(new MapValueFactory<>(header));
            tvResults.getColumns().add(col);
        }
    }

    private void fillTable(List<String[]> tabularData) {
        ObservableList<Map<String, String>> items = FXCollections.observableArrayList();
        for (int rowIndex = 1; rowIndex < tabularData.size(); rowIndex++) {
            String[] csvRow = tabularData.get(rowIndex);
            Map<String, String> tableRow = new HashMap<>();
            for (int columnIndex = 0; columnIndex < csvRow.length; columnIndex++) {
                tableRow.put(tabularData.get(0)[columnIndex], csvRow[columnIndex]);
            }

            items.add(tableRow);
        }

        tvResults.getItems().addAll(items);
    }

    @FXML
    private void save() throws IOException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(tvResults.getScene().getWindow());
        if (file != null) {
            generateAndSaveCSV(file);
        }
    }

    private void generateAndSaveCSV(File file) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(stringify(rawData));
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
