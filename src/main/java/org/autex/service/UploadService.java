package org.autex.service;

import javafx.collections.ObservableList;
import org.apache.commons.collections4.ListUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.autex.model.Product;
import org.autex.task.CreateTask;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class UploadService extends RemoteService {
    public UploadService(ObservableList<Product> products, String authHeader) {
        super(products, authHeader);
    }

    @Override
    protected void runTask(CloseableHttpClient httpClient, ExecutorService service) throws InterruptedException {
        List<Product> createList = products.stream().filter(p -> p.getStatus() == Product.Status.NEW).collect(Collectors.toList());
        List<List<Product>> groupedNewProducts = ListUtils.partition(createList, 10);
        service.invokeAll(groupedNewProducts.stream().map(productGroup -> new CreateTask(httpClient, products, getGetProductURL(), authHeader, this)).collect(Collectors.toList()));

        List<Product> updateList = products.stream().filter(p -> p.getStatus() == Product.Status.EXISTS).collect(Collectors.toList());
        List<List<Product>> groupedChangedProducts = ListUtils.partition(updateList, 10);
        service.invokeAll(groupedChangedProducts.stream().map(productGroup -> new CreateTask(httpClient, products, getGetProductURL(), authHeader, this)).collect(Collectors.toList()));
    }
}
