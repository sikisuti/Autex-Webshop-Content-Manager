package org.autex.dialog;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.StackPane;

public class PasswordInputDialog extends Dialog<String> {
    public PasswordInputDialog() {
        setTitle("Add meg a jelszót");
        setHeaderText(null);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, new ButtonType("mégse", ButtonBar.ButtonData.CANCEL_CLOSE));
        PasswordField passwordField = new PasswordField();
        StackPane pane = new StackPane(passwordField);
        pane.setPadding(new Insets(20));
        getDialogPane().setContent(pane);
        Platform.runLater(passwordField::requestFocus);
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return passwordField.getText();
            }

            return null;
        });
    }
}
