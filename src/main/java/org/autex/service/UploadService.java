package org.autex.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.collections.ObservableList;
import org.apache.commons.collections4.ListUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.autex.model.Product;
import org.autex.task.CreateTask;
import org.autex.task.UpdateTask;
import org.autex.util.Configuration;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class UploadService extends RemoteService {
    private final Set<String> selectedFields;

    public UploadService(ObservableList<Product> products, String authHeader, Set<String> selectedFields) {
        super(products, authHeader);
        this.selectedFields = selectedFields;
    }

    @Override
    protected void runTask(CloseableHttpClient httpClient, ExecutorService service) throws InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Product> createList = products.stream().filter(p -> p.getStatus() == Product.Status.NEW).collect(Collectors.toList());
        List<List<Product>> groupedNewProducts = ListUtils.partition(createList, 10);
        String newProductURL = Configuration.getStringProperty("host") + Configuration.getStringProperty("productsPath");
        service.invokeAll(groupedNewProducts.stream().map(productGroup -> new CreateTask(httpClient, productGroup, newProductURL, authHeader, this, selectedFields, objectMapper)).collect(Collectors.toList()));

        List<Product> updateList = products.stream().filter(p -> p.getStatus() == Product.Status.EXISTS).collect(Collectors.toList());
        List<List<Product>> groupedChangedProducts = ListUtils.partition(updateList, 10);
        String changeProductURL = Configuration.getStringProperty("host") + Configuration.getStringProperty("productsPath");
        service.invokeAll(groupedChangedProducts.stream().map(productGroup -> new UpdateTask(httpClient, productGroup, changeProductURL, authHeader, this, selectedFields, objectMapper)).collect(Collectors.toList()));
    }
}
