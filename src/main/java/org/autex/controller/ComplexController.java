package org.autex.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.autex.exception.GeneralException;

import java.io.*;

public class ComplexController extends SupplierController {

    @FXML public Label lbAllItemsPath;
    @FXML public Label lbInventoryPath;

    File allItemsSourceFile;
    File inventorySourceFile;

    @Override
    public Writer convert() {
        try (InputStream is = new FileInputStream(allItemsSourceFile)) {
            return supplier.convert(is);
        } catch (IOException e) {
            throw new GeneralException("Fájl betöltés sikertelen.");
        }
    }
}
