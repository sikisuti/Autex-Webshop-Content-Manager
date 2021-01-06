package org.autex.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import org.autex.util.Serializer;
import org.autex.util.Translator;

import java.util.HashMap;
import java.util.Map;

public class FieldSelectorController {
    @FXML private VBox selectorList;
    private Map<String, BooleanProperty> selectedFields = new HashMap<>();

    public void initialize() {
        for (String field : Serializer.AVAILABLE_FIELDS) {
            BooleanProperty isSelected = new SimpleBooleanProperty(false);
            selectedFields.put(field, isSelected);
            CheckBox chk = new CheckBox(Translator.translate(field));
            chk.setId(field);
            isSelected.bindBidirectional(chk.selectedProperty());
            selectorList.getChildren().add(chk);
        }
    }

    @FXML
    private void upload() {
        int i = 0;
    }
}
