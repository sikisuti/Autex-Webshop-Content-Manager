package org.autex.controller;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.autex.model.Product;
import org.autex.supplier.RetrieveAllProductsSupplierTask;

public class RetrieveAllProductsController extends SupplierController {
  @Override
  public String getDescription() {
    return "Összes webshop termék lekérdezése";
  }

  @Override
  public Task<ObservableList<Product>> getConversionTask() {
    return new RetrieveAllProductsSupplierTask();
  }
}
