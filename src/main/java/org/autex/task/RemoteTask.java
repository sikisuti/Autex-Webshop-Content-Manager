package org.autex.task;

import org.apache.http.client.HttpClient;
import org.autex.model.Product;
import org.autex.service.RemoteService;

import java.util.List;
import java.util.concurrent.Callable;

public abstract class RemoteTask implements Callable<List<Product>> {
    protected String getProductURL;
    protected HttpClient httpClient;
    protected List<Product> products;
    protected String authHeader;
    protected RemoteService parentService;

    protected RemoteTask(HttpClient httpClient, List<Product> products, String getProductURL, String authHeader, RemoteService parentService) {
        this.httpClient = httpClient;
        this.products = products;
        this.getProductURL = getProductURL;
        this.authHeader = authHeader;
        this.parentService = parentService;
    }
}
