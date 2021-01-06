package org.autex.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.autex.util.Configuration;

import java.util.Map;

public class PasswordController {
    @FXML private Label lblCurrentPassword;
    @FXML private PasswordField pwdCurrentPassword;
    @FXML private Label lblNewPassword;
    @FXML private PasswordField pwdNewPassword;
    @FXML private Label lblNewPasswordVerify;
    @FXML private PasswordField pwdNewPasswordVerify;
    private Mode mode;

    @FXML
    private void initialize() {
    }

    @FXML
    private void close() {

    }

    @FXML
    private void save() {
        /*if (mode == Mode.SET) {
            if (pwdNewPassword.getText() == null || pwdNewPassword.getText().isEmpty() || !pwdNewPassword.getText().equals(pwdNewPasswordVerify.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Hiba");
                alert.setHeaderText(null);
                alert.setContentText("Jelszavak nem egyeznek!");
                alert.showAndWait();
                return;
            }

            Configuration.getInstance().setPassword(pwdNewPassword.getText());
            Configuration.getInstance().setEncryptedProperty("password", pwdNewPassword.getText());
        }*/
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        if (mode == Mode.SET) {
            lblCurrentPassword.setManaged(false);
            pwdCurrentPassword.setManaged(false);
        }
    }

    public enum Mode {
        SET,
        CHANGE,
        VERIFY
    }
}
