package org.autex.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.ObservableList;
import org.apache.commons.collections4.ListUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.autex.model.Product;
import org.autex.task.UploadTask;
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
        List<List<Product>> groupedNewProducts = ListUtils.partition(products, 100);
        String newProductURL = Configuration.getStringProperty("host") + Configuration.getStringProperty("productsPath") + "/batch";
        service.invokeAll(groupedNewProducts.stream().map(productGroup -> new UploadTask(httpClient, productGroup, newProductURL, authHeader, this, selectedFields, objectMapper)).collect(Collectors.toList()));
    }
}
