package org.autex.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.*;
import org.autex.util.Translator;

import java.util.*;

public class Product {
    public static final String BRAND = "_brand";
    public static final String NAME = "name";
    public static final String PRICE = "price";
    public static final String STOCK_QUANTITY = "stock_quantity";
    public static final String WEIGHT = "weight";

    private final Map<String, SimpleStringProperty> data = new HashMap<>();

    private StringProperty id = new SimpleStringProperty();
    private StringProperty sku = new SimpleStringProperty();
    private final ObjectProperty<Status> status = new SimpleObjectProperty<>(Status.UNKNOWN);

    public void setField(String name, String value) {
        if (data.containsKey(name)) {
            data.get(name).set(value);
        } else {
            data.put(name, new SimpleStringProperty(value));
        }
    }

    public String getField(String name) {
        if (!data.containsKey(name)) {
            data.put(name, new SimpleStringProperty());
        }

        return data.get(name).get();
    }

    public Set<String> getAllFields() {
        return data.keySet();
    }

    public StringProperty nameProperty() {
        return data.get(NAME);
    }

    public StringProperty priceProperty() {
        return data.get(PRICE);
    }

    public StringProperty stockQuantityProperty() {
        return data.get(STOCK_QUANTITY);
    }

    public StringProperty weightProperty() {
        return data.get(WEIGHT);
    }

    public StringProperty brandProperty() {
        return data.get(BRAND);
    }

    public String getId() {
        return this.id.get();
    }
    public void setId(String id) {
        this.id.set(id);
    }
    public StringProperty idProperty() {
        return id;
    }

    public String getSku() {
        return this.sku.get();
    }
    public void setSku(String sku) {
        this.sku.set(sku);
    }
    public StringProperty skuProperty() {
        return sku;
    }

    public Status getStatus() {
        return status.get();
    }
    public void setStatus(Status status) {
        this.status.set(status);
    }
    public ObjectProperty<Status> statusProperty() {
        return status;
    }

    public ObjectNode toJsonObject(Set<String> selectedFields, ObjectMapper objectMapper) {
        ObjectNode jsonObject = objectMapper.createObjectNode();

        for (String selectedField : selectedFields) {
            if (data.containsKey(selectedField)) {
                jsonObject.put(selectedField, data.get(selectedField).get());
            }
        }

        return jsonObject;
    }

    public enum Status {
        ACCESS_FAILURE,
        CREATED,
        EXISTS,
        NEW,
        UNKNOWN,
        UPDATED,
        UPLOADED;

        @Override
        public String toString() {
            return Translator.translate(this.name());
        }
    }
}
