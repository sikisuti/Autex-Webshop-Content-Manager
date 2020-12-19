package org.autex.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.autex.App;
import org.autex.supplyer.SupplierTask;

import java.io.IOException;
import java.util.List;

public abstract class SupplierController {
    public abstract Task<List<String[]>> getConversionTask();
}
