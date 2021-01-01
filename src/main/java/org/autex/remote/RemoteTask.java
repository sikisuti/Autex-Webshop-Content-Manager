package org.autex.remote;

import org.apache.http.client.HttpClient;
import org.autex.model.Product;

import java.util.List;
import java.util.concurrent.Callable;

public abstract class RemoteTask implements Callable<List<Product>> {
    protected String getProductURL;
    protected HttpClient httpClient;
    protected List<Product> products;
    protected String authHeader;
    protected RemoteService parentService;
}
