package org.autex.task;

import org.apache.http.client.HttpClient;
import org.autex.model.Product;
import org.autex.service.RemoteService;

import java.util.List;
import java.util.Set;

public class CreateTask extends RemoteTask {
    private final Set<String> selecedFields;

    public CreateTask(HttpClient httpClient, List<Product> products, String newProductURL, String authHeader, RemoteService parentService, Set<String> selecedFields) {
        super(httpClient, products, newProductURL, authHeader, parentService);
        this.selecedFields = selecedFields;
    }

    @Override
    public List<Product> call() throws Exception {
        return null;
    }
}
