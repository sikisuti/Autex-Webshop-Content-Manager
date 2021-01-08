package org.autex;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.autex.dialog.PasswordInputDialog;
import org.autex.exception.InvalidCredentials;
import org.autex.util.Configuration;

import java.io.IOException;
import java.util.Optional;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        try {
            passwordCheckAndInit();
        } catch (InvalidCredentials e) {
            return;
        }

        Scene scene = new Scene(loadFXML("mainView"));
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void passwordCheckAndInit() {
        PasswordInputDialog.Mode mode = Configuration.isInitialized() ?
                PasswordInputDialog.Mode.PROVIDE :
                PasswordInputDialog.Mode.CREATE;

        Optional<Boolean> isAuthenticated = new PasswordInputDialog(mode).showAndWait();
        if (isAuthenticated.isEmpty() || !isAuthenticated.get()) {
            throw new InvalidCredentials();
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}