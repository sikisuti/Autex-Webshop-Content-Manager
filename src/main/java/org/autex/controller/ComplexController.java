package org.autex.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.autex.Configuration;
import org.autex.exception.GeneralException;
import org.autex.supplyer.Complex;

import java.io.*;

public class ComplexController extends SupplierController {
    @FXML public Label lbAllItemsPath;
    @FXML public Label lbInventoryPath;

    FileChooser fileChooser;

    File allItemsSourceFile;
    File inventorySourceFile;

    public ComplexController() {
        supplier = new Complex();
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
            allItemsSourceFile = fileChooser.showOpenDialog(lbAllItemsPath.getScene().getWindow());
            lbAllItemsPath.setText(allItemsSourceFile.getAbsolutePath());
        } else {
            fileChooser.setTitle("Válassz készlet fájlt");
            inventorySourceFile = fileChooser.showOpenDialog(lbAllItemsPath.getScene().getWindow());
            lbInventoryPath.setText(inventorySourceFile.getAbsolutePath());
        }
    }

    @Override
    public void convert() {
        if (allItemsSourceFile == null || inventorySourceFile == null) {
            throw new GeneralException("Válassz forrásfájl(oka)t!");
        }

        try (InputStream is = new FileInputStream(allItemsSourceFile);
            InputStream isInv = new FileInputStream(inventorySourceFile)) {
            supplier.convert(is, isInv);
            openResultView(supplier);
        } catch (IOException e) {
            throw new GeneralException("Fájl betöltés sikertelen.");
        }
    }
}
