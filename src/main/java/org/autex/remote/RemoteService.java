package org.autex.remote;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.commons.collections4.ListUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.autex.util.Configuration;
import org.autex.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class RemoteService extends Task<ObservableList<Product>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteService.class);
    protected ObservableList<Product> products;
    private long count = 0;
    Class<? extends RemoteTask> clazz;
    private final String authHeader;


    public RemoteService(ObservableList<Product> products, Class<? extends RemoteTask> clazz, String authHeader) {
        this.products = products;
        this.clazz = clazz;
        this.authHeader = authHeader;
    }

    @Override
    protected ObservableList<Product> call() throws Exception {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        Integer allowedThreads = Configuration.getInstance().getIntegerProperty("noOfCallThreads");
        connectionManager.setDefaultMaxPerRoute(allowedThreads);
        ExecutorService service = null;
        try (CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build()) {
            service = Executors.newFixedThreadPool(allowedThreads);
            RemoteTaskFactory taskFactory = new RemoteTaskFactory(httpClient, authHeader);
            List<List<Product>> groupedProducts = ListUtils.partition(products, 10);
            service.invokeAll(groupedProducts.stream().map(productGroup -> taskFactory.getTask(clazz, productGroup, this)).collect(Collectors.toList()));
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (service != null) {
                service.shutdown();
            }
        }

        return products;
    }

    public void updateProgress(int increment) {
        synchronized (this) {
            count += increment;
            super.updateProgress(count, products.size());
        }
    }
}
