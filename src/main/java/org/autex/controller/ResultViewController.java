package org.autex.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import java.io.Writer;

public class ResultViewController {
    @FXML TableView<String> tvResults;

    public void setResult(Writer writer) {
        txtResult.setText(writer.toString());
    }
}
