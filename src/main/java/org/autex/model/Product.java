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
    private final Map<String, IntegerProperty> integerData = new HashMap<>();
    private final Map<String, FloatProperty> floatData = new HashMap<>();

    private final ObjectProperty<Long> idField = new SimpleObjectProperty<>();
    private String skuField;
    private final ObjectProperty<Status> status = new SimpleObjectProperty<>(Status.UNKNOWN);

    private Product remoteInstance;

    public Product(String skuField) {
        this.skuField = skuField;
        this.remoteInstance = new Product();
    }

    public Product() {}

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
            integerData.put(name, new SimpleIntegerProperty(value));
        }
    }

    public void setField(String name, float value) {
        if (floatData.containsKey(name)) {
            floatData.get(name).set(value);
        } else {
            floatData.put(name, new SimpleFloatProperty(value));
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
        } else if (type.equals(Float.class)) {
            if (!floatData.containsKey(name)) {
                floatData.put(name, new SimpleFloatProperty());
            }

            return type.cast(floatData.get(name).get());
        } else {
            if (!integerData.containsKey(name)) {
                integerData.put(name, new SimpleIntegerProperty());
            }

            return type.cast(integerData.get(name).get());
        }
    }

    public Set<String> getAllFieldNames() {
        Set<String> fieldNames = new HashSet<>();
        fieldNames.addAll(stringData.keySet());
        fieldNames.addAll(integerData.keySet());
        fieldNames.addAll(floatData.keySet());
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

    public StringProperty stockQuantityProperty() {
        return stringData.get(STOCK_QUANTITY);
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

    public Product getRemoteInstance() {
        return remoteInstance;
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
                } else if (STOCK_QUANTITY.equals(selectedField)) {
                    String value = stringData.get(STOCK_QUANTITY).get();
                    if (value.contains(".")) {
                        jsonObject.put(STOCK_QUANTITY, Float.parseFloat(value));
                    } else {
                        jsonObject.put(STOCK_QUANTITY, Integer.parseInt(value));
                    }

                    jsonObject.put("manage_stock", true);
                } else {
                    jsonObject.put(selectedField, stringData.get(selectedField).get());
                }
            } else if (floatData.containsKey(selectedField) && floatData.get(selectedField) != null) {
                jsonObject.put(selectedField, floatData.get(selectedField).get());
            } else if (integerData.containsKey(selectedField) && integerData.get(selectedField) != null) {
                jsonObject.put(selectedField, integerData.get(selectedField).get());
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
