package org.autex.dialog;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.autex.exception.InvalidCredentials;
import org.autex.util.Configuration;

public class PasswordInputDialog extends Dialog<Boolean> {
    private final Mode mode;
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField passwordFieldVerify = new PasswordField();

    public PasswordInputDialog(Mode mode) {
        this.mode = mode;
        setTitle("Add meg a jelszót");
        setHeaderText(null);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, new ButtonType("mégse", ButtonBar.ButtonData.CANCEL_CLOSE));
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(20));
        Label label = new Label("jelszó");
        GridPane.setColumnIndex(passwordField, 1);
        pane.getChildren().addAll(label, passwordField);
        getDialogPane().setContent(pane);
        if (mode == Mode.CREATE) {
            setTitle("Új jelszó létrehozása");
            Label labelVerify = new Label("még egyszer");
            GridPane.setRowIndex(labelVerify, 1);
            GridPane.setRowIndex(passwordFieldVerify, 1);
            GridPane.setColumnIndex(passwordFieldVerify, 1);
            pane.getChildren().addAll(labelVerify, passwordFieldVerify);
        }

        Button btnOK = (Button) getDialogPane().lookupButton(ButtonType.OK);
        btnOK.addEventFilter(ActionEvent.ACTION, this::checkInput);

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Configuration.getInstance().init(passwordField.getText());
                return true;
            }

            return false;
        });

        Platform.runLater(passwordField::requestFocus);
    }

    private void checkInput(ActionEvent event) {
            if (mode == Mode.CREATE) {
                if (!passwordField.getText().equals(passwordFieldVerify.getText())) {
                    showInvalidCredentialsAlert();
                    passwordField.clear();
                    passwordFieldVerify.clear();
                    Platform.runLater(passwordField::requestFocus);
                    event.consume();
                }
            } else {
                if (passwordField.getText() == null || passwordField.getText().isEmpty()) {
                    showInvalidCredentialsAlert();
                    event.consume();
                } else {
                    try {
                        Configuration.getInstance().init(passwordField.getText());
                    } catch (Exception e) {
                        showInvalidCredentialsAlert();
                        passwordField.clear();
                        Platform.runLater(passwordField::requestFocus);
                        event.consume();
                    }
                }
            }
    }

    private void showInvalidCredentialsAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText("Hibás jelszó");
        alert.showAndWait();
    }

    public enum Mode {
        PROVIDE,
        CREATE
    }
}
