package org.autex.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.autex.controller.ResultViewController;
import org.autex.model.Product;
import org.autex.service.UploadService;
import org.autex.util.Configuration;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.mockito.Mockito.*;

public class UploadTaskTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadTaskTest.class);

    @Test
    @Ignore
    public void testUpload() throws Exception {
        LOGGER.trace("test started");
        List<Product> data = new ArrayList<>();
        Product product = new Product("abc123");
        product.setField(Product.NAME, new String("teszté".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
        product.setField(Product.STOCK_QUANTITY, 10.0);
        product.setField(Product.PRICE, "0");
        product.setStatus(Product.Status.NEW);
        data.add(product);
        UploadService uploadService = mock(UploadService.class);
        Configuration.init("abc");

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(1);
//        String newProductURL = "http://localhost:15002/echo";
        String newProductURL = Configuration.getStringProperty("host") + Configuration.getStringProperty("productsPath") + "/batch";
//        ExecutorService service = null;
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(10000)
                        .setConnectionRequestTimeout(10000)
                        .build())
                .build()) {
            UploadTask task = new UploadTask(httpClient, data, newProductURL, new ResultViewController().getAuthHeader(), uploadService, new HashSet<>(Arrays.asList(Product.NAME, Product.PRICE, Product.STOCK_QUANTITY)), new ObjectMapper());
            task.call();
        }
    }
}
