package org.autex.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.autex.App;
import org.autex.util.Configuration;

import java.io.IOException;
import java.util.Map;

public class ConfigController {
    @FXML private GridPane simplePropertiesGrid;
    @FXML private GridPane credentialsGrid;
    @FXML private TextField txtKey;
    @FXML private TextField txtSecretKey;
    @FXML private Button btnShowCredentials;

    @FXML
    private void initialize() {
        Map<String, String> simpleProperties = Configuration.getInstance().getSimpleProperties();
        int row = 0;
        for (Map.Entry<String, String> propertyEntry : simpleProperties.entrySet()) {
            Label label = new Label(propertyEntry.getKey());
            simplePropertiesGrid.add(label, 0, row);
            TextField textField = new TextField(propertyEntry.getValue());
            simplePropertiesGrid.add(textField, 1, row);
            row++;
        }

        btnShowCredentials.visibleProperty().bind(credentialsGrid.visibleProperty().not());
    }

    @FXML
    private void close() {

    }

    @FXML
    private void showCredentials() throws IOException {
        TextInputDialog td = new TextInputDialog();
        td.setHeaderText(null);
        td.setTitle("Add meg a jelszót");
        td.showAndWait();
        String a = td.getResult();
        String encryptedPassword = Configuration.getInstance().getProperty("password");
        if (encryptedPassword == null) {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/password.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Jelszó kezelő");
            stage.setScene(new Scene(loader.load()));
            PasswordController passwordController = loader.getController();
            passwordController.setMode(PasswordController.Mode.SET);
            stage.initOwner(simplePropertiesGrid.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        }

        credentialsGrid.setVisible(true);
    }
}
