package org.autex.controller;

import static java.util.Optional.ofNullable;

import java.io.File;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.autex.model.Product;
import org.autex.supplier.FileSupplierTask;
import org.autex.util.Configuration;

public class FileController implements SupplierController {
    @FXML public Label lbSourcePath;

    FileChooser fileChooser;

    File sourceFile;

  public FileController() {
    fileChooser = new FileChooser();
    ofNullable(Configuration.getStringProperty("defaultPath"))
        .map(File::new)
        .filter(File::exists)
        .ifPresent(initDir -> fileChooser.setInitialDirectory(initDir));

    fileChooser
        .getExtensionFilters()
        .addAll(
            new FileChooser.ExtensionFilter("Excel files", "*.xlsx"),
            new FileChooser.ExtensionFilter("All Files", "*.*"));
  }

    public void selectFile() {
        fileChooser.setTitle("Válassz fájlt");
        sourceFile = fileChooser.showOpenDialog(lbSourcePath.getScene().getWindow());
        lbSourcePath.setText(sourceFile.getAbsolutePath());
    }

    @Override
    public Task<ObservableList<Product>> getConversionTask() {
        return new FileSupplierTask(sourceFile);
    }
}
