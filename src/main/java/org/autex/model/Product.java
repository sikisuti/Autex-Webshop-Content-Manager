package org.autex.model;

import javafx.beans.property.*;
import org.autex.util.Translator;

import java.util.*;

public class Product {
    public static final String BRAND = "_brand";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PRICE = "price";
    public static final String SKU = "sku";
    public static final String STOCK_QUANTITY = "stock_quantity";
    public static final String WEIGHT = "weight";

    private final Map<String, SimpleStringProperty> data = new HashMap<>();

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

    public StringProperty idProperty() {
        if (!data.containsKey(ID)) {
            data.put(ID, new SimpleStringProperty());
        }

        return data.get(ID);
    }

    public StringProperty nameProperty() {
        return data.get(NAME);
    }

    public StringProperty skuProperty() {
        return data.get(SKU);
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

    public Status getStatus() {
        return status.get();
    }
    public ObjectProperty<Status> statusProperty() {
        return status;
    }

    public enum Status {
        UNKNOWN,
        EXISTS,
        NEW,
        ACCESS_FAILURE;

        @Override
        public String toString() {
            return Translator.translate(this.name());
        }
    }
}
