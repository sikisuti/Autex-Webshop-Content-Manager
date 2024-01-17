package org.autex.controller;

import static java.util.Optional.ofNullable;
import static javafx.beans.binding.Bindings.and;

import java.io.*;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.autex.model.Product;
import org.autex.supplier.ComplexSupplierTask;
import org.autex.util.Configuration;

public class ComplexController extends SupplierController {
  @FXML public Label lbAllItemsPath;
  @FXML public Label lbInventoryPath;

  FileChooser fileChooser;

  File masterDataFile;
  File stockFile;

  @FXML
  public void initialize() {
    fileChooser = new FileChooser();
    ofNullable(Configuration.getStringProperty("defaultPath"))
        .map(File::new)
        .filter(File::exists)
        .ifPresent(initDir -> fileChooser.setInitialDirectory(initDir));

    fileChooser
        .getExtensionFilters()
        .addAll(
            new FileChooser.ExtensionFilter("Excel files", "*.xls", "*.xlsx"),
            new FileChooser.ExtensionFilter("All Files", "*.*"));

    isReadyProperty.bind(
        and(
            lbAllItemsPath.textProperty().isNotEmpty(),
            lbInventoryPath.textProperty().isNotEmpty()));
  }

  public void selectFile(ActionEvent e) {
    if (((Node) e.getSource()).getId().equals("btnAllItems")) {
      fileChooser.setTitle("Válassz cikktörzs fájlt");
      masterDataFile = fileChooser.showOpenDialog(lbAllItemsPath.getScene().getWindow());
      lbAllItemsPath.setText(ofNullable(masterDataFile).map(File::getAbsolutePath).orElse(null));
    } else {
      fileChooser.setTitle("Válassz készlet fájlt");
      stockFile = fileChooser.showOpenDialog(lbAllItemsPath.getScene().getWindow());
      lbInventoryPath.setText(ofNullable(stockFile).map(File::getAbsolutePath).orElse(null));
    }
  }

  @Override
  public String getDescription() {
    return "Feltöltés Complex fájlokból";
  }

  @Override
  public Task<ObservableList<Product>> getConversionTask() {
    return new ComplexSupplierTask(masterDataFile, stockFile);
  }
}
