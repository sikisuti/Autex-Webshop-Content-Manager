package org.autex.controller;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.autex.model.Product;

public interface SupplierController {
    Task<ObservableList<Product>> getConversionTask();
}
