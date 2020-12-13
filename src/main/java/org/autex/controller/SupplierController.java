package org.autex.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.autex.App;
import org.autex.supplyer.Supplier;

import java.io.IOException;
import java.io.Writer;

public abstract class SupplierController {
    Supplier supplier;

    abstract void convert();

    protected void openResultView(Writer result) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/resultView.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Generált csv");
        stage.setScene(new Scene(loader.load(), 500, 500));
        ResultViewController resultViewController = loader.getController();
        resultViewController.setResult(result);
        stage.show();
    }
}
