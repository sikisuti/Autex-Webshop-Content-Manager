package org.autex.supplier;

import javafx.collections.ObservableList;
import org.autex.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetrieveAllProductsSupplierTask extends SupplierTask {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(RetrieveAllProductsSupplierTask.class);

  @Override
  protected ObservableList<Product> doJob() throws Exception {
    return null;
  }
}
