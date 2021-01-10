package org.autex.task;

import org.apache.http.client.HttpClient;
import org.autex.model.Product;
import org.autex.service.RemoteService;

import java.util.List;
import java.util.concurrent.Callable;

public abstract class RemoteTask implements Callable<List<Product>> {
    protected String url;
    protected HttpClient httpClient;
    protected List<Product> products;
    protected String authHeader;
    protected RemoteService parentService;

    protected RemoteTask(HttpClient httpClient, List<Product> products, String url, String authHeader, RemoteService parentService) {
        this.httpClient = httpClient;
        this.products = products;
        this.url = url;
        this.authHeader = authHeader;
        this.parentService = parentService;
    }
}
