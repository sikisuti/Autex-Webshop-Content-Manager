package org.autex.controller;

import static java.util.Optional.ofNullable;

import java.io.*;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.autex.model.Product;
import org.autex.supplier.AutexSupplierTask;
import org.autex.util.Configuration;

public class AutexController extends SupplierController {
  @FXML private Label lbSourcePath;

  FileChooser fileChooser;
  File cobraFile;

  @FXML
  public void initialize() {
    fileChooser = new FileChooser();
    ofNullable(Configuration.getStringProperty("defaultPath"))
        .map(File::new)
        .filter(File::exists)
        .ifPresent(initDir -> fileChooser.setInitialDirectory(initDir));

    fileChooser.setTitle("Válassz fájlt");
    fileChooser
        .getExtensionFilters()
        .addAll(
            new FileChooser.ExtensionFilter("Excel files", "*.xls", "*.xlsx"),
            new FileChooser.ExtensionFilter("All Files", "*.*"));

    isReadyProperty.bind(lbSourcePath.textProperty().isNotEmpty());
  }

  public void selectFile() {
    cobraFile = fileChooser.showOpenDialog(lbSourcePath.getScene().getWindow());
    lbSourcePath.setText(ofNullable(cobraFile).map(File::getAbsolutePath).orElse(null));
  }

  @Override
  public String getDescription() {
    return "Feltöltés Cobra fájlból";
  }

  @Override
  public Task<ObservableList<Product>> getConversionTask() {
    return new AutexSupplierTask(cobraFile);
  }
}
