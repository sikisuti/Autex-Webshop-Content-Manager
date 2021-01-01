package org.autex.remote;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.autex.Configuration;
import org.autex.model.Product;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RemoteTaskFactory {
    private final HttpClient httpClient;
    private String getProductURL;
    private String authHeader;

    public RemoteTaskFactory(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public RemoteTask getTask(Class<? extends RemoteTask> clazz, List<Product> products, RemoteService parentService) {
        if (clazz.equals(SyncTask.class)) {
            return new SyncTask(httpClient, products, getGetProductURL(), getAuthHeader(), parentService);
        } else if (clazz.equals(CreateTask.class)) {
            return new CreateTask(httpClient, products, getGetProductURL(), getAuthHeader(), parentService);
        }

        return null;
    }

    protected String getGetProductURL() {
        if (getProductURL == null) {
            getProductURL = Configuration.getInstance().getProperty("host") + Configuration.getInstance().getProperty("productsPath");
        }

        return getProductURL;
    }

    protected String getAuthHeader() {
        if (authHeader == null) {
            String username = Configuration.getInstance().getCredentialsProperty("username");
            String password = Configuration.getInstance().getCredentialsProperty("password");
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
            authHeader = "Basic " + new String(encodedAuth);
        }

        return authHeader;
    }
}
