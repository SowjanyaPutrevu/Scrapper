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

    private List<Specification> specifications;

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

    public List<Specification> getSpecifications() {
        return specifications;
    }
}
