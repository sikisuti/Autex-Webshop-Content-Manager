package org.autex.task;

import org.apache.http.client.HttpClient;
import org.autex.model.Product;
import org.autex.service.RemoteService;

import java.util.List;

public class UpdateTask extends RemoteTask {

    public UpdateTask(HttpClient httpClient, List<Product> products, String getProductURL, String authHeader, RemoteService parentService) {
        super(httpClient, products, getProductURL, authHeader, parentService);
    }

    @Override
    public List<Product> call() throws Exception {
        return null;
    }
}
