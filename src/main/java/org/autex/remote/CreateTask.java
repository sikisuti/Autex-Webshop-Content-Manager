package org.autex.remote;

import org.apache.http.client.HttpClient;
import org.autex.model.Product;

import java.util.List;

public class CreateTask extends RemoteTask {

    public CreateTask(HttpClient httpClient, List<Product> products, String getProductURL, String authHeader, RemoteService parentService) {
        super(httpClient, products, getProductURL, authHeader, parentService);
    }

    @Override
    public List<Product> call() throws Exception {
        return null;
    }
}
