package org.autex.service;

import javafx.collections.ObservableList;
import org.apache.commons.collections4.ListUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.autex.model.Product;
import org.autex.task.SyncTask;
import org.autex.util.Configuration;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class SyncService extends RemoteService {
    public SyncService(ObservableList<Product> products, String authHeader) {
        super(products, authHeader);
    }

    @Override
    protected void runTask(CloseableHttpClient httpClient, ExecutorService service) throws InterruptedException {
        updateTitle("Szinkronizálás a Webshop-al");
        List<List<Product>> groupedProducts = ListUtils.partition(products, 10);
        String getProductURL = Configuration.getStringProperty("host") + Configuration.getStringProperty("productsPath");
        service.invokeAll(groupedProducts.stream().map(productGroup -> new SyncTask(httpClient, productGroup, getProductURL, authHeader, this)).collect(Collectors.toList()));
    }
}
