package org.autex.dialog;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.autex.util.Translator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FieldSelectorDialog extends Dialog<Set<String>> {
    private final Map<String, BooleanProperty> selectedFields = new HashMap<>();

    public FieldSelectorDialog(Set<String> allFields) {
        setTitle("Frissítésre kiválasztott mezők");
        setHeaderText(null);
        getDialogPane().getButtonTypes().addAll(
                new ButtonType("Webshop frissítése", ButtonBar.ButtonData.OK_DONE),
                new ButtonType("mégse", ButtonBar.ButtonData.CANCEL_CLOSE));
        VBox pane = new VBox();
        pane.setPadding(new Insets(20));
        pane.setSpacing(10);
        for (String field : allFields) {
            BooleanProperty isSelected = new SimpleBooleanProperty(false);
            selectedFields.put(field, isSelected);
            CheckBox cb = new CheckBox(Translator.translate(field));
            cb.setId(field);
            isSelected.bindBidirectional(cb.selectedProperty());
            pane.getChildren().add(cb);
        }

        Button btnOK = (Button) getDialogPane().lookupButton(ButtonType.OK);
        btnOK.addEventFilter(ActionEvent.ACTION, this::checkInput);



        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return selectedFields.entrySet().stream()
                        .filter(f -> f.getValue().get())
                        .map(Map.Entry::getKey).collect(Collectors.toSet());
            }

            return null;
        });
    }

    private void checkInput(ActionEvent event) {
        if (selectedFields.values().stream().noneMatch(ObservableBooleanValue::get)) {
            event.consume();
        }
    }
}
