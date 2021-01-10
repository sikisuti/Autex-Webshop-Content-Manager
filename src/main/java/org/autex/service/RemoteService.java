package org.autex.service;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.autex.util.Configuration;
import org.autex.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class RemoteService extends Task<ObservableList<Product>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteService.class);
    protected ObservableList<Product> products;
    private long count = 0;
    protected final String authHeader;
    private String getProductURL;

    public RemoteService(ObservableList<Product> products, String authHeader) {
        this.products = products;
        this.authHeader = authHeader;
    }

    @Override
    protected ObservableList<Product> call() throws Exception {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        int allowedThreads = Configuration.getIntegerProperty("noOfCallThreads");
        connectionManager.setDefaultMaxPerRoute(allowedThreads);
        ExecutorService service = null;
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(10000)
                        .setConnectionRequestTimeout(10000)
                        .build())
                .build()) {
            service = Executors.newFixedThreadPool(allowedThreads);
            runTask(httpClient, service);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (service != null) {
                service.shutdown();
            }
        }

        return products;
    }

    protected abstract void runTask(CloseableHttpClient httpClient, ExecutorService service) throws Exception;

    public void updateProgress(int increment) {
        synchronized (this) {
            count += increment;
            super.updateProgress(count, products.size());
        }
    }
}
