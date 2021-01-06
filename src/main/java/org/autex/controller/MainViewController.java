package org.autex.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.autex.App;

import java.io.IOException;

public class MainViewController {
    @FXML private BorderPane rootPane;
    @FXML private ComboBox<SupplyerDTO> cmbSupplierPicker;

    @FXML
    public void initialize() throws IOException {
        ObservableList<SupplyerDTO> suppliers = FXCollections.observableArrayList(
                loadFXML("File"),
                loadFXML("Autex"),
                loadFXML("Complex")
        );
        cmbSupplierPicker.setItems(suppliers);
    }

    private SupplyerDTO loadFXML(String name) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/" + name + ".fxml"));
        return new SupplyerDTO(name, fxmlLoader.load(), fxmlLoader.getController());
    }

    @FXML
    private void openResultView() throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/resultView.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Generált csv");
        stage.setScene(new Scene(loader.load()));
        ResultViewController resultViewController = loader.getController();
        resultViewController.convert(cmbSupplierPicker.getValue().controller.getConversionTask());
        stage.show();
    }

    @FXML
    private void updateSupplierView(ActionEvent event) {
        rootPane.setCenter(cmbSupplierPicker.getValue().view);
    }

    @FXML
    private void openFile() {

    }

    @FXML
    private void openConfig() throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/config.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Beállítások");
        stage.setScene(new Scene(loader.load()));
        stage.initOwner(rootPane.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    private static class SupplyerDTO {
        private final String name;
        private final Node view;
        private final SupplierController controller;

        private SupplyerDTO(String name, Node view, SupplierController controller) {
            this.name = name;
            this.view = view;
            this.controller = controller;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
