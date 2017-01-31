package com.icecat;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sowji on 21/01/2017.
 * Could be common for all scrapers
 */
public class BrandSpecs {

    private List<String> imagesList;

    private String description;

    private String name;

    private Map<String,String> features;

    private Map<String, String> featureImages;

    private Map<String, List<Specification>> modelSpecs;

    private Map<String, List<Specification>> generalSpecs;

    private Map<String, List<String>> afeatures;

    public List<String> videos;

    public List<String> colors;

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

    public Map<String, String> getFeatureImages() {
        return featureImages;
    }

    public void setFeatureImages(Map<String, String> featureImages) {
        this.featureImages = featureImages;
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }

    public Map<String, List<String>> getAfeatures() {
        return afeatures;
    }

    public void setAfeatures(Map<String, List<String>> afeatures) {
        this.afeatures = afeatures;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    @Override
    public String toString() {
        return "BrandSpecs{" +
                "imagesList=" + imagesList +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", modelSpecs=" + modelSpecs +
                ", generalSpecs=" + generalSpecs +
                ", videos="+videos+
                ", afeatures="+afeatures+
                ", features="+features+
                ", featureImages="+featureImages+
                ", colors="+colors+
                '}';
    }
}
