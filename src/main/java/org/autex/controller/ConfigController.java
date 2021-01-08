package org.autex.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.autex.util.Configuration;
import org.autex.util.Translator;

import java.util.HashMap;
import java.util.Map;

public class ConfigController {
    @FXML private GridPane simplePropertiesGrid;
    @FXML private GridPane credentialsGrid;
    @FXML private TextField txtKey;
    @FXML private TextField txtSecretKey;
    @FXML private Button btnShowCredentials;
    private final Map<String, SimpleStringProperty> data = new HashMap<>();

    @FXML
    private void initialize() {
        Map<String, String> simpleProperties = Configuration.getSimpleProperties();
        int row = 0;
        for (Map.Entry<String, String> propertyEntry : simpleProperties.entrySet()) {
            Label label = new Label(Translator.translate(propertyEntry.getKey()));
            simplePropertiesGrid.add(label, 0, row);
            SimpleStringProperty property = new SimpleStringProperty(propertyEntry.getValue());
            data.put(propertyEntry.getKey(), property);
            TextField textField = new TextField();
            textField.textProperty().bindBidirectional(property);
            simplePropertiesGrid.add(textField, 1, row);
            row++;
        }

        btnShowCredentials.visibleProperty().bind(credentialsGrid.visibleProperty().not());
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) simplePropertiesGrid.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void save() {
        for (Map.Entry<String, SimpleStringProperty> entry : data.entrySet()) {
            Configuration.setProperty(entry.getKey(), entry.getValue().get());
        }

        Configuration.storeProperties();
    }

    @FXML
    private void showCredentials() {
        String decryptedKey = Configuration.getStringProperty("key");
        SimpleStringProperty keyProperty = new SimpleStringProperty(decryptedKey);
        data.put("key", keyProperty);
        txtKey.textProperty().bindBidirectional(keyProperty);
        String decryptedSecretKey = Configuration.getStringProperty("secretKey");
        SimpleStringProperty secretKeyProperty = new SimpleStringProperty(decryptedSecretKey);
        data.put("secretKey", secretKeyProperty);
        txtSecretKey.textProperty().bindBidirectional(secretKeyProperty);

        credentialsGrid.setVisible(true);
    }
}
