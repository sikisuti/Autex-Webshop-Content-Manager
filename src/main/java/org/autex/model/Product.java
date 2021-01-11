package org.autex.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.*;
import org.autex.util.Translator;

import java.util.*;

public class Product {
    public static final String BRAND = "_brand";
    public static final String CATEGORY = "category";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PRICE = "regular_price";
    public static final String SKU = "sku";
    public static final String STOCK_QUANTITY = "stock_quantity";
    public static final String WEIGHT = "weight";

    private final Map<String, StringProperty> stringData = new HashMap<>();
    private final Map<String, ObjectProperty<Integer>> integerData = new HashMap<>();

    private final ObjectProperty<Long> idField = new SimpleObjectProperty<>();
    private final String skuField;
    private final ObjectProperty<Status> status = new SimpleObjectProperty<>(Status.UNKNOWN);

    public Product(String skuField) {
        this.skuField = skuField;
    }

    public void setField(String name, String value) {
        if (stringData.containsKey(name)) {
            stringData.get(name).set(value);
        } else {
            stringData.put(name, new SimpleStringProperty(value));
        }
    }

    public void setField(String name, int value) {
        if (integerData.containsKey(name)) {
            integerData.get(name).set(value);
        } else {
            integerData.put(name, new SimpleObjectProperty<>(value));
        }
    }

    public String getField(String name) {
        return getField(name, String.class);
    }

    public <T> T getField(String name, Class<T> type) {
        if (type.equals(String.class)) {
            if (!stringData.containsKey(name)) {
                stringData.put(name, new SimpleStringProperty());
            }

            return type.cast(stringData.get(name).get());
        } else {
            if (!integerData.containsKey(name)) {
                integerData.put(name, new SimpleObjectProperty<>());
            }

            return type.cast(integerData.get(name).get());
        }
    }

    public Set<String> getAllFieldNames() {
        Set<String> fieldNames = new HashSet<>();
        fieldNames.addAll(stringData.keySet());
        fieldNames.addAll(integerData.keySet());
        return fieldNames;
    }

    public StringProperty brandProperty() {
        return stringData.get(BRAND);
    }

    public StringProperty categoryProperty() {
        return stringData.get(CATEGORY);
    }

    public StringProperty nameProperty() {
        return stringData.get(NAME);
    }

    public StringProperty priceProperty() {
        return stringData.get(PRICE);
    }

    public ObjectProperty<Integer> stockQuantityProperty() {
        return integerData.get(STOCK_QUANTITY);
    }

    public StringProperty weightProperty() {
        return stringData.get(WEIGHT);
    }

    public Long getIdField() {
        return this.idField.get();
    }
    public void setIdField(Long idField) {
        this.idField.set(idField);
    }
    public ObjectProperty<Long> idProperty() {
        return idField;
    }

    public String getSku() {
        return this.skuField;
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

    public ObjectNode toJsonObject(ObjectMapper objectMapper) {
        return toJsonObject(getAllFieldNames(), objectMapper);
    }

    public ObjectNode toJsonObject(Set<String> selectedFields, ObjectMapper objectMapper) {
        ObjectNode jsonObject = objectMapper.createObjectNode();

        if (getStatus() == Status.EXISTS) {
            jsonObject.put(ID, getIdField());
        } else if (getStatus() == Status.NEW) {
            jsonObject.put(SKU, getSku());
        }

        ArrayNode metaData = objectMapper.createArrayNode();
        ObjectNode gwsMetasValue = objectMapper.createObjectNode();
        for (String selectedField : selectedFields) {
            if (stringData.containsKey(selectedField) && stringData.get(selectedField).get() != null && !stringData.get(selectedField).get().isBlank()) {
                if (BRAND.equals(selectedField)) {
                    addBrand(objectMapper, metaData, gwsMetasValue);
                } else {
                    jsonObject.put(selectedField, stringData.get(selectedField).get());
                }
            } else if (integerData.containsKey(selectedField) && integerData.get(selectedField).get() != null) {
                if (STOCK_QUANTITY.equals(selectedField)) {
                    jsonObject.put(STOCK_QUANTITY, integerData.get(STOCK_QUANTITY).get());
                    jsonObject.put("manage_stock", true);
                } else {
                    jsonObject.put(selectedField, integerData.get(selectedField).get());
                }
            }
        }

        if (!gwsMetasValue.isEmpty()) {
            ObjectNode gwsMetas = objectMapper.createObjectNode();
            gwsMetas.put("key", "_gws_es_metas");
            gwsMetas.set("value", gwsMetasValue);
            metaData.add(gwsMetas);
        }

        if (!metaData.isEmpty()) {
            jsonObject.set("meta_data", metaData);
        }

        return jsonObject;
    }

    private void addBrand(ObjectMapper objectMapper, ArrayNode metaData, ObjectNode gwsMetasValue) {
        ObjectNode brandObject = objectMapper.createObjectNode();
        brandObject.put("key", BRAND);
        brandObject.put("value", stringData.get(BRAND).getValue());
        metaData.add(brandObject);
        gwsMetasValue.put("brand", stringData.get(BRAND).getValue());
    }

    public boolean isReadyToUpload() {
        return getStatus() == Status.NEW || getStatus() == Status.EXISTS;
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
