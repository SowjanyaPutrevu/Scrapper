package com.icecat;

import java.util.List;
import java.util.Map;

/**
 * Created by Sowji on 21/01/2017.
 * Could be common for all scrapers
 */
public class BrandSpecs {

    private List<String> imagesList;

    private String description;

    private String name;

    private Map<String,String> features;

    private Map<String, List<Specification>> modelSpecs;

    private Map<String, List<Specification>> generalSpecs;

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

    public Map<String, List<Specification>> getGeneralSpecs() {
        return generalSpecs;
    }

    public void setGeneralSpecs(Map<String, List<Specification>> specifications) {
        this.generalSpecs = specifications;
    }

    public Map<String, List<Specification>> getModelSpecs() {
        return modelSpecs;
    }

    public void setModelSpecs(Map<String, List<Specification>> modelSpecs) {
        this.modelSpecs = modelSpecs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, String> features) {
        this.features = features;
    }

    @Override
    public String toString() {
        return "BrandSpecs{" +
                "imagesList=" + imagesList +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", modelSpecs=" + modelSpecs +
                ", generalSpecs=" + generalSpecs +
                '}';
    }
}
