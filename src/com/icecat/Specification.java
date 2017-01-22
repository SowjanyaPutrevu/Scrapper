package com.icecat;

import java.util.List;

/**
 * Created by Sowji on 21/01/2017.
 * Abstract layer for all kinds of features which can be in specs
 */
public class Specification {

    private String name;

    private List<String> attributes;

    private String values;

    private String desc;

    private String type;

    private String productId;

    private String sourceAttributeId;

    private String sourceAttributeValue;

    private String brand;

    private String model;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSourceAttributeId() {
        return sourceAttributeId;
    }

    public void setSourceAttributeId(String sourceAttributeId) {
        this.sourceAttributeId = sourceAttributeId;
    }

    public String getSourceAttributeValue() {
        return sourceAttributeValue;
    }

    public void setSourceAttributeValue(String sourceAttributeValue) {
        this.sourceAttributeValue = sourceAttributeValue;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getAttribute() {
        return attributes;
    }

    public void setAttribute(List<String> attribute) {
        this.attributes = attribute;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Specification{" +
                "name='" + name + '\'' +
                ", attributes=" + attributes +
                ", values='" + values + '\'' +
                ", desc='" + desc + '\'' +
                ", type='" + type + '\'' +
                ", productId='" + productId + '\'' +
                ", sourceAttributeId='" + sourceAttributeId + '\'' +
                ", sourceAttributeValue='" + sourceAttributeValue + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
