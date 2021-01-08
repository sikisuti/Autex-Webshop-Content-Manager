package org.autex.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.autex.util.Translator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Product {
    private Long id;
    private String sku;
    private StringProperty name = new SimpleStringProperty();
    private String price;
    private Integer stock_quantity;
    private String weight;
    private List<MetaData> meta_data = new ArrayList<>();
    public ObjectProperty<Status> status = new SimpleObjectProperty<>(Status.UNKNOWN);

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getStock_quantity() {
        return stock_quantity;
    }

    public void setStock_quantity(Integer stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public List<MetaData> getMeta_data() {
        return meta_data;
    }

    public void setMeta_data(List<MetaData> meta_data) {
        this.meta_data = meta_data;
    }

    public String getBrand() {
        Optional<MetaData> metaDataBrand = getMeta_data().stream().filter(metaData -> metaData.getKey().equals("_brand")).findFirst();
        return metaDataBrand.map(MetaData::getValue).orElse(null);
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
