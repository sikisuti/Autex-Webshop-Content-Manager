package org.autex.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.autex.exception.GeneralException;
import org.autex.supplyer.AutexTask;

import java.io.*;
import java.util.List;

public class AutexController extends SupplierController {
    @FXML private Label lbSourcePath;

    FileChooser fileChooser;
    File cobraFile;

    public AutexController() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Válassz fájlt");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel files", "*.xls", "*.xlsx"), new FileChooser.ExtensionFilter("All Files", "*.*"));
    }

    public void selectFile() {
        cobraFile = fileChooser.showOpenDialog(lbSourcePath.getScene().getWindow());
        lbSourcePath.setText(cobraFile.getAbsolutePath());
    }

    /*@Override
    public void convert() {
        try (InputStream is = new FileInputStream(cobraFile)) {
            conversionTask.convert(is);
        } catch (IOException e) {
            throw new GeneralException("Fájl betöltés sikertelen.");
        }
    }*/

    @Override
    public Task<List<String[]>> getConversionTask() {
        return new AutexTask(cobraFile);
    }
}
