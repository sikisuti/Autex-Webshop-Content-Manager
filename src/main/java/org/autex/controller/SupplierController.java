package org.autex.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.autex.model.Product;

public abstract class SupplierController {
  public final BooleanProperty isReadyProperty = new SimpleBooleanProperty(true);

  public abstract String getDescription();

  public abstract Task<ObservableList<Product>> getConversionTask();
}
