package org.autex.supplier;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.autex.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SupplierTask extends Task<ObservableList<Product>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SupplierTask.class);

    @Override
    protected ObservableList<Product> call() {
        try {
            return doJob();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    protected abstract ObservableList<Product> doJob() throws Exception;
}
