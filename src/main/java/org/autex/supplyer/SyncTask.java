package org.autex.supplyer;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.autex.Configuration;
import org.autex.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SyncTask extends Task<ObservableList<Product>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncTask.class);
    ObservableList<Product> products;
    private long count = 0;

    protected void updateProgress() {
        synchronized (this) {
            super.updateProgress(++count, products.size());
        }
    }

    public SyncTask(ObservableList<Product> products) {
        this.products = products;
    }

    @Override
    protected ObservableList<Product> call() throws Exception {
        String getProductURL = Configuration.getInstance().getProperty("host") + Configuration.getInstance().getProperty("productsPath");
        String username = Configuration.getInstance().getCredentialsProperty("username");
        String password = Configuration.getInstance().getCredentialsProperty("password");
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        Integer allowedThreads = Configuration.getInstance().getIntegerProperty("noOfCallThreads");
        connectionManager.setDefaultMaxPerRoute(allowedThreads);
        ExecutorService service = null;
        try (CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build()) {
            service = Executors.newFixedThreadPool(allowedThreads);
            service.invokeAll(products.stream().map(product -> new SyncProductTask(httpClient, product, getProductURL, authHeader, this)).collect(Collectors.toList()));
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (service != null) {
                service.shutdown();
            }
        }

        return products;
    }
}
