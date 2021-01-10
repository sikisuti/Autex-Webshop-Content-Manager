package org.autex.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.autex.model.Product;
import org.autex.service.RemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class UpdateTask extends RemoteTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateTask.class);

    private final Set<String> selectedFields;
    private final ObjectMapper objectMapper;

    public UpdateTask(HttpClient httpClient, List<Product> products, String getProductURL, String authHeader, RemoteService parentService, Set<String> selectedFields, ObjectMapper objectMapper) {
        super(httpClient, products, getProductURL, authHeader, parentService);
        this.selectedFields = selectedFields;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Product> call() throws Exception {
        for (Product product : products) {
            LOGGER.info(product.serialize(selectedFields, objectMapper));
        }

        return products;
    }
}
