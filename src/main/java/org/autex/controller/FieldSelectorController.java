package org.autex.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import org.autex.util.Serializer;

import java.util.HashMap;
import java.util.Map;

public class FieldSelectorController {
    @FXML private VBox selectorList;
    private Map<String, BooleanProperty> selectedFields = new HashMap<>();

    public void initialize() {
        for (Map.Entry<String, String> field : Serializer.AVAILABLE_FIELDS.entrySet()) {
            BooleanProperty isSelected = new SimpleBooleanProperty(false);
            selectedFields.put(field.getKey(), isSelected);
            CheckBox chk = new CheckBox(field.getValue());
            chk.setId(field.getKey());
            isSelected.bindBidirectional(chk.selectedProperty());
            selectorList.getChildren().add(chk);
        }
    }

    @FXML
    private void upload() {
        int i = 0;
    }
}
