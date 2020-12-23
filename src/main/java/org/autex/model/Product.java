package org.autex.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Product {
    private Long id;
    private String sku;
    private String name;
    private String price;
    private Integer stock_quantity;
    private String weight;
    private List<MetaData> meta_data = new ArrayList<>();

    public Long getId() {
        return id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
