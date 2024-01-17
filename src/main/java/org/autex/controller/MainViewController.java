package org.autex.controller;

import static java.util.Optional.ofNullable;
import static javafx.scene.control.ContentDisplay.BOTTOM;

import java.io.IOException;
import java.net.URL;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.autex.App;
import org.autex.util.Configuration;

public class MainViewController {
  @FXML private BorderPane rootPane;
  @FXML private TilePane tpSupplierPicker;
  @FXML private Label lblMissingCredentials;
  @FXML private Button btnInvoke;

  private final ObjectProperty<SupplyerDTO> selectedSupplyer = new SimpleObjectProperty<>();

  @FXML
  public void initialize() throws IOException {
    var suppliers =
        FXCollections.observableArrayList(
            loadFXML("RetrieveAllProducts"),
            loadFXML("File"),
            loadFXML("Autex"),
            loadFXML("Complex"));

    selectedSupplyer.addListener(
        (observableValue, oldSupplier, newSupplier) -> {
          rootPane.setCenter(newSupplier.view);
          btnInvoke.visibleProperty().unbind();
          btnInvoke.visibleProperty().bind(newSupplier.controller.isReadyProperty);
        });
    suppliers.forEach(supplier -> tpSupplierPicker.getChildren().add(getButton(supplier)));
    lblMissingCredentials.setVisible(Configuration.isCredentialsMissing());
  }

  private Button getButton(SupplyerDTO supplyer) {
    var button = new Button(supplyer.controller.getDescription());
    var img =
        ofNullable(supplyer.imgPath)
            .map(imgPath -> getClass().getResource(imgPath))
            .map(URL::toExternalForm)
            .map(
                imgPath -> {
                  var imgView = new ImageView(imgPath);
                  imgView.setFitHeight(150);
                  imgView.setFitWidth(180);
                  imgView.setPreserveRatio(true);
                  return imgView;
                })
            .orElse(null);
    button.setGraphic(img);
    button.setContentDisplay(BOTTOM);
    button.setPrefWidth(200);
    button.setPrefHeight(200);
    button.setOnAction(actionEvent -> selectedSupplyer.setValue(supplyer));

    return button;
  }

  private SupplyerDTO loadFXML(String name) throws IOException {
    var fxmlLoader = new FXMLLoader(App.class.getResource("view/" + name + ".fxml"));
    return new SupplyerDTO(
        name, fxmlLoader.load(), fxmlLoader.getController(), "/img/" + name + "_icon.png");
  }

  @FXML
  private void openResultView() throws IOException {
    FXMLLoader loader = new FXMLLoader(App.class.getResource("view/resultView.fxml"));
    Stage stage = new Stage();
    stage.setTitle("Generált csv");
    stage.setScene(new Scene(loader.load()));
    stage.initOwner(rootPane.getScene().getWindow());
    stage.initModality(Modality.APPLICATION_MODAL);
    ResultViewController resultViewController = loader.getController();
    resultViewController.convert(selectedSupplyer.get().controller.getConversionTask());
    stage.show();
  }

  @FXML
  private void openConfig() throws IOException {
    FXMLLoader loader = new FXMLLoader(App.class.getResource("view/config.fxml"));
    Stage stage = new Stage();
    stage.setTitle("Beállítások");
    stage.setScene(new Scene(loader.load()));
    stage.initOwner(rootPane.getScene().getWindow());
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.showAndWait();
    lblMissingCredentials.setVisible(Configuration.isCredentialsMissing());
  }

  private static class SupplyerDTO {
    private final String name;
    private final Node view;
    private final SupplierController controller;
    private final String imgPath;

    private SupplyerDTO(String name, Node view, SupplierController controller, String imgPath) {
      this.name = name;
      this.view = view;
      this.controller = controller;
      this.imgPath = imgPath;
    }

    @Override
    public String toString() {
      return name;
    }
  }
}
