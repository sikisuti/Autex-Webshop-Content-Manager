package org.autex.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultViewController {
    @FXML TableView tvResults;

    public void setResult(List<String[]> tabularData) {
        setHeaders(tabularData.get(0));
        fillTable(tabularData);
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
}
