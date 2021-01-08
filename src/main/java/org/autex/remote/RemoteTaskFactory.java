package org.autex.remote;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.autex.util.Configuration;
import org.autex.model.Product;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RemoteTaskFactory {
    private final HttpClient httpClient;
    private String getProductURL;
    protected final String authHeader;

    public RemoteTaskFactory(HttpClient httpClient, String authHeader) {
        this.httpClient = httpClient;
        this.authHeader = authHeader;
    }

    public RemoteTask getTask(Class<? extends RemoteTask> clazz, List<Product> products, RemoteService parentService) {
        if (clazz.equals(SyncTask.class)) {
            return new SyncTask(httpClient, products, getGetProductURL(), authHeader, parentService);
        } else if (clazz.equals(CreateTask.class)) {
            return new CreateTask(httpClient, products, getGetProductURL(), authHeader, parentService);
        }

        return null;
    }

    protected String getGetProductURL() {
        if (getProductURL == null) {
            getProductURL = Configuration.getStringProperty("host") + Configuration.getStringProperty("productsPath");
        }

        return getProductURL;
    }
}
