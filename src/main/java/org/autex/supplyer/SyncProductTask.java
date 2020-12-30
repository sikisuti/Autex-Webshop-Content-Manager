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
import java.util.Optional;
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

            products.stream().filter(p -> p.getStatus() == Product.Status.UNKNOWN).forEach(p -> p.setStatus(Product.Status.NEW));
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        parentTask.updateProgress(products.size());
        return products;
    }

    void parseResponse(InputStream contentStream, List<Product> products) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        try (JsonParser jsonParser = jsonFactory.createParser(contentStream)) {
            while (jsonParser.nextToken() != null) {
                if (jsonParser.currentToken() == JsonToken.START_OBJECT) {
                    parseProduct(jsonParser, products);
                }
            }
        }
    }

    void parseProduct(JsonParser jsonParser, List<Product> products) throws IOException {
        String weight = null;
        Long id = null;
        String sku = null;
        int level = 0;

        while (jsonParser.nextToken() != JsonToken.END_OBJECT || level > 0) {
            String fieldName = jsonParser.getCurrentName();
            if ("id".equals(fieldName)) {
                id = jsonParser.nextLongValue(0);
            } else if ("weight".equals(fieldName)) {
                weight = jsonParser.nextTextValue();
            } else if ("sku".equals(fieldName)) {
                sku = jsonParser.nextTextValue();
            } else if (jsonParser.currentToken() == JsonToken.START_OBJECT) {
                level++;
            } else if (jsonParser.currentToken() == JsonToken.END_OBJECT) {
                level--;
            }
        }

        adjustProduct(products, weight, id, sku);
    }

    private void adjustProduct(List<Product> products, String weight, Long id, String sku) {
        if (sku != null && !sku.isEmpty()) {
            Optional<Product> product = products.stream().filter(p -> sku.equals(p.getSku())).findFirst();
            if (product.isPresent()) {
                product.get().setStatus(Product.Status.EXISTS);
                if (product.get().getWeight() == null && weight != null) {
                    product.get().setWeight(weight);
                }

                if (product.get().getId() == null && id != null) {
                    product.get().setId(id);
                }
            }
        }
    }
}
