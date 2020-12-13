package org.autex.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.autex.exception.GeneralException;
import org.autex.supplyer.Autex;

import java.io.*;

public class AutexController extends SupplierController {
    @FXML private Label lbSourcePath;

    FileChooser fileChooser;
    File sourceFile;

    public AutexController() {
        supplier = new Autex();
        fileChooser = new FileChooser();
        fileChooser.setTitle("Válassz fájlt");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel files", "*.xls", "*.xlsx"), new FileChooser.ExtensionFilter("All Files", "*.*"));
    }

    public void selectFile() {
        sourceFile = fileChooser.showOpenDialog(lbSourcePath.getScene().getWindow());
        lbSourcePath.setText(sourceFile.getAbsolutePath());
    }

    @Override
    public void convert() {
        try (InputStream is = new FileInputStream(sourceFile)) {
            supplier.convert(is);
        } catch (IOException e) {
            throw new GeneralException("Fájl betöltés sikertelen.");
        }
    }
}
