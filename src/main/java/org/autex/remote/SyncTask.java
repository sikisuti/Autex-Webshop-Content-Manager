package org.autex.remote;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.autex.exception.CalloutException;
import org.autex.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.autex.model.Product.*;

public class SyncTask extends RemoteTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncTask.class);

    public SyncTask(HttpClient httpClient, List<Product> products, String getProductURL, String authHeader, RemoteService parentService) {
        super(httpClient, products, getProductURL, authHeader, parentService);
    }

    @Override
    public List<Product> call() {
        HttpGet getProductRequest = new HttpGet();
        try {
            getProductRequest.setURI(new URIBuilder(getProductURL)
                    .addParameter(SKU, products.stream().map(p -> p.getField(SKU)).collect(Collectors.joining(",")))
                    .build());
            getProductRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            HttpResponse response = httpClient.execute(getProductRequest);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode > 399) {
                EntityUtils.consumeQuietly(entity);
                throw new CalloutException(statusCode, response.getStatusLine().getReasonPhrase());
            }

            try (InputStream responseStream = entity.getContent()) {
                parseResponse(responseStream, products);
            }

            EntityUtils.consumeQuietly(entity);
            products.stream().filter(p -> p.statusProperty().get() == Product.Status.UNKNOWN).forEach(p -> p.statusProperty().set(Product.Status.NEW));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            for (Product product : products) {
                product.statusProperty().set(Product.Status.ACCESS_FAILURE);
            }
        }

        parentService.updateProgress(products.size());
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
            Optional<Product> product = products.stream().filter(p -> sku.equals(p.getField(SKU))).findFirst();
            if (product.isPresent()) {
                product.get().statusProperty().set(Product.Status.EXISTS);
                if (product.get().getField(WEIGHT) == null && weight != null) {
                    product.get().setField(WEIGHT, weight);
                }

                if (product.get().getField(ID) == null && id != null) {
                    product.get().setField(ID, String.valueOf(id));
                }
            }
        }
    }
}
