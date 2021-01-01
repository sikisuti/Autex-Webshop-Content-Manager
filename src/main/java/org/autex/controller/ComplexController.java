package org.autex.controller;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.autex.Configuration;
import org.autex.model.Product;
import org.autex.supplier.ComplexSupplierTask;

import java.io.*;

public class ComplexController implements SupplierController {
    @FXML public Label lbAllItemsPath;
    @FXML public Label lbInventoryPath;

    FileChooser fileChooser;

    File masterDataFile;
    File stockFile;

    public ComplexController() {
        fileChooser = new FileChooser();
        File initDir = new File(Configuration.getInstance().getProperty("defaultPath"));
        if (initDir.exists()) {
            fileChooser.setInitialDirectory(initDir);
        }

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel files", "*.xls", "*.xlsx"), new FileChooser.ExtensionFilter("All Files", "*.*"));
    }

    public void selectFile(ActionEvent e) {
        if (((Node) e.getSource()).getId().equals("btnAllItems")) {
            fileChooser.setTitle("Válassz cikktörzs fájlt");
            masterDataFile = fileChooser.showOpenDialog(lbAllItemsPath.getScene().getWindow());
            lbAllItemsPath.setText(masterDataFile.getAbsolutePath());
        } else {
            fileChooser.setTitle("Válassz készlet fájlt");
            stockFile = fileChooser.showOpenDialog(lbAllItemsPath.getScene().getWindow());
            lbInventoryPath.setText(stockFile.getAbsolutePath());
        }
    }

    @Override
    public Task<ObservableList<Product>> getConversionTask() {
        return new ComplexSupplierTask(masterDataFile, stockFile);
    }
}
