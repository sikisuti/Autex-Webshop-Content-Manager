package org.autex.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import org.autex.App;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainViewController {
    @FXML private BorderPane rootPane;
    @FXML private ComboBox<String> cmbSupplierPicker;
    Map<String, SupplyerDTO> suppliers = new HashMap<>();

    public MainViewController() throws IOException {
        suppliers.put("Autex", loadFXML("autex"));
        suppliers.put("Complex", loadFXML("complex"));
    }

    private SupplyerDTO loadFXML(String name) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/" + name + ".fxml"));
        return new SupplyerDTO(fxmlLoader.load(), fxmlLoader.getController());
    }

    @FXML
    private void update() {
        try {
            suppliers.get(cmbSupplierPicker.getValue()).controller.convert();
        } catch (Exception e) {
            NotificationController.notify(e.getMessage());
        }
    }

    @FXML
    private void supplyerSelected(ActionEvent event) {
        rootPane.setCenter(suppliers.get(((ComboBox<String>) event.getSource()).getValue()).node);
    }

    private static class SupplyerDTO {
        private final Node node;
        private final SupplierController controller;

        private SupplyerDTO(Node node, SupplierController controller) {
            this.node = node;
            this.controller = controller;
        }
    }
}
