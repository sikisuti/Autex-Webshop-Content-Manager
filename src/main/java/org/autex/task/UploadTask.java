package org.autex.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.autex.exception.CalloutException;
import org.autex.model.Product;
import org.autex.service.RemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

public class UploadTask extends RemoteTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadTask.class);

    private final Set<String> selectedFields;
    private final ObjectMapper objectMapper;

    public UploadTask(HttpClient httpClient, List<Product> products, String newProductURL, String authHeader, RemoteService parentService, Set<String> selectedFields, ObjectMapper objectMapper) {
        super(httpClient, products, newProductURL, authHeader, parentService);
        this.selectedFields = selectedFields;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Product> call() {
        HttpPost newProductRequest = new HttpPost();
        try {
            URI uri = new URIBuilder(url).build();
            newProductRequest.setURI(uri);
            newProductRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            newProductRequest.setHeader(HttpHeaders.HOST, uri.getHost());
            ObjectNode requestObject = objectMapper.createObjectNode();
            ArrayNode createArray = objectMapper.createArrayNode();
            ArrayNode updateArray = objectMapper.createArrayNode();
            for (Product product : products) {
                if (product.getStatus() == Product.Status.NEW) {
                    createArray.add(product.toJsonObject(objectMapper));
                } else if (product.getStatus() == Product.Status.EXISTS) {
                    updateArray.add(product.toJsonObject(selectedFields, objectMapper));
                }
            }

            if (!createArray.isEmpty()) {
                requestObject.set("create", createArray);
            }

            if (!updateArray.isEmpty()) {
                requestObject.set("update", updateArray);
            }

            LOGGER.info(requestObject.toPrettyString());

            StringEntity requestEntity = new StringEntity(objectMapper.writeValueAsString(requestObject), ContentType.APPLICATION_JSON);
            newProductRequest.setEntity(requestEntity);
            HttpResponse response = httpClient.execute(newProductRequest);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode > 399) {
                throw new CalloutException(statusCode, response.getStatusLine().getReasonPhrase(), EntityUtils.toString(entity));
            }

            EntityUtils.consumeQuietly(entity);
            products.forEach(p -> p.setStatus(Product.Status.EXISTS));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            products.forEach(p -> p.setStatus(Product.Status.ACCESS_FAILURE));
        }

        parentService.updateProgress(products.size());
        return products;
    }
}
