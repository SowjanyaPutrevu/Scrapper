package com.icecat;

import java.util.List;
import java.util.Map;

/**
 * Created by Sowji on 21/01/2017.
 * Product specifications
 *
 */
public class ProductSpecs {

    private List<String> imagesList;

    private String description;

    private String name;

    private String price;

    private String brand;

    private String sourceId;

    private String model;

    private List<Specification> specifications;

    public BrandSpecs brandSpecs;


    //GETTERS AND SETTERE


    public BrandSpecs getBrandSpecs() {
        return brandSpecs;
    }

    public void setBrandSpecs(BrandSpecs brandSpecs) {
        this.brandSpecs = brandSpecs;
    }

    public List<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<String> imagesList) {
        this.imagesList = imagesList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSpecifications(List<Specification> specifications) {
        this.specifications = specifications;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<Specification> getSpecifications() {
        return specifications;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }


    @Override
    public String toString() {
        return "ProductSpecs{" +
                "imagesList=" + imagesList +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", brand='" + brand + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", model='" + model + '\'' +
                ", specifications=" + specifications +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductSpecs that = (ProductSpecs) o;

        return sourceId != null ? sourceId.equals(that.sourceId) : that.sourceId == null;

    }

    @Override
    public int hashCode() {
        return sourceId != null ? sourceId.hashCode() : 0;
    }
}
