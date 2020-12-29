package org.autex.supplyer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.autex.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class SyncProductTask implements Callable<List<Product>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncProductTask.class);
    private static final String SKU = "sku";

    private final String getProductURL;
    private final HttpClient httpClient;
    private final List<Product> products;
    private final String authHeader;
    private final SyncTask parentTask;

    public SyncProductTask(HttpClient httpClient, List<Product> products, String getProductURL, String authHeader, SyncTask parentTask) {
        this.httpClient = httpClient;
        this.products = products;
        this.getProductURL = getProductURL;
        this.authHeader = authHeader;
        this.parentTask = parentTask;
    }

    @Override
    public List<Product> call() {
        HttpGet getProductRequest = new HttpGet();
        try {
            getProductRequest.setURI(new URIBuilder(getProductURL)
                    .addParameter(SKU, products.stream().map(Product::getSku).collect(Collectors.joining(",")))
                    .build());
            getProductRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            HttpResponse resposne = httpClient.execute(getProductRequest);
            HttpEntity entity = resposne.getEntity();
            try (InputStream responseStream = entity.getContent()) {
                parseResponse(responseStream, products);
            }
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        parentTask.updateProgress();
        return products;
    }

    void parseResponse(InputStream contentStream, List<Product> products) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        try (JsonParser jsonParser = jsonFactory.createParser(contentStream)) {
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                parseProduct(jsonParser, product);
            }
        }
    }

    void parseProduct(JsonParser jsonParser, Product product) throws IOException {
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.getCurrentName();
            if ("id".equals(fieldName) && product.getWeight() == null) {
                product.setId(jsonParser.nextLongValue(0));
            } else if ("weight".equals(fieldName) && product.getWeight() == null) {
                product.setWeight(jsonParser.nextTextValue());
            }
        }
    }
}
