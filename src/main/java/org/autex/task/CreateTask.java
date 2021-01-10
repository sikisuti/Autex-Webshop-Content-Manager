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
import org.apache.http.util.EntityUtils;
import org.autex.exception.CalloutException;
import org.autex.model.Product;
import org.autex.service.RemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class CreateTask extends RemoteTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateTask.class);

    private final Set<String> selectedFields;
    private final ObjectMapper objectMapper;

    public CreateTask(HttpClient httpClient, List<Product> products, String newProductURL, String authHeader, RemoteService parentService, Set<String> selectedFields, ObjectMapper objectMapper) {
        super(httpClient, products, newProductURL, authHeader, parentService);
        this.selectedFields = selectedFields;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Product> call() throws Exception {
        HttpPost newProductRequest = new HttpPost();
        try {
            newProductRequest.setURI(new URIBuilder(url).build());
            newProductRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            newProductRequest.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
            ObjectNode requestObject = objectMapper.createObjectNode();
            ArrayNode createArray = objectMapper.createArrayNode();
            ArrayNode updateArray = objectMapper.createArrayNode();
            for (Product product : products) {
                if (product.getStatus() == Product.Status.NEW) {
                    createArray.add(product.toJsonObject(selectedFields, objectMapper));
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

            /*newProductRequest.setEntity(new StringEntity(requestObject.toString()));
            HttpResponse response = httpClient.execute(newProductRequest);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            EntityUtils.consumeQuietly(entity);
            if (statusCode > 399) {
                throw new CalloutException(statusCode, response.getStatusLine().getReasonPhrase());
            }*/

            products.forEach(p -> p.setStatus(Product.Status.UPLOADED));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            products.forEach(p -> p.setStatus(Product.Status.ACCESS_FAILURE));
        }

        parentService.updateProgress(products.size());
        return products;
    }
}
