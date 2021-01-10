package org.autex.task;

import org.apache.http.client.HttpClient;
import org.autex.model.Product;
import org.autex.service.RemoteService;

import java.util.List;
import java.util.Set;

public class UpdateTask extends RemoteTask {
    private final Set<String> selectedFields;

    public UpdateTask(HttpClient httpClient, List<Product> products, String getProductURL, String authHeader, RemoteService parentService, Set<String> selectedFields) {
        super(httpClient, products, getProductURL, authHeader, parentService);
        this.selectedFields = selectedFields;
    }

    @Override
    public List<Product> call() throws Exception {
        return null;
    }
}
