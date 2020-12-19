package org.autex.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.autex.Configuration;
import org.autex.exception.GeneralException;
import org.autex.supplyer.ComplexTask;

import java.io.*;
import java.util.List;

public class ComplexController extends SupplierController {
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

    /*@Override
    public void convert() {
        if (masterDataFile == null || stockFile == null) {
            throw new GeneralException("Válassz forrásfájl(oka)t!");
        }

        try (InputStream is = new FileInputStream(masterDataFile);
             InputStream isInv = new FileInputStream(stockFile)) {
            conversionTask.convert(is, isInv);
            openResultView(conversionTask);
        } catch (IOException e) {
            throw new GeneralException("Fájl betöltés sikertelen.");
        }
    }*/

    @Override
    public Task<List<String[]>> getConversionTask() {
        return new ComplexTask(masterDataFile, stockFile);
    }
}
