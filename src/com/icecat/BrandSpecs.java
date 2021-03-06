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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    private Set<String> productImages;

    private List<String> specUrl;

    public List<String> getSpecUrl() {
        return specUrl;
    }

    private String isbn;

    public void setSpecUrl(List<String> specUrl) {
        this.specUrl = specUrl;
    }
    private String pdf;

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public Set<String> getProductImages() {

        return productImages;

    }

    public void setProductImages(Set<String> productImages) {
        this.productImages = productImages;
    }

    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPieces() {
        return pieces;
    }

    public void setPieces(String pieces) {
        this.pieces = pieces;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getCategory() {

        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String code;

    private String age;

    private String pieces;

    private String vip;

    private String name;

    private String brand_name;

    private String category;

    private Map<String,String> features;

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    private Map<String, String> featureImages;

    private Map<String, List<Specification>> modelSpecs;

    private Map<String, List<Specification>> generalSpecs;

    private Map<String, List<String>> afeatures;

    private List<String> videos;

    private List<String> colors;

    private List<String> threeD;

    private String shortDescription;

    private List<String> skus;

    public String getAuthorDesc() {
        return authorDesc;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setAuthorDesc(String authorDesc) {
        this.authorDesc = authorDesc;
    }


    Map<String,String> details ;

    private String authorDesc;

    private String publisher;



    public List<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<String> imagesList) {
        this.imagesList = imagesList;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
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

    public List<String> getThreeD() {
        return threeD;
    }

    public void setThreeD(List<String> threeD) {
        this.threeD = threeD;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public List<String> getSkus() {
        return skus;
    }

    public void setSkus(List<String> skus) {
        this.skus = skus;
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
                ", 3D view="+threeD+
                ", brand-name="+brand_name+
                ",productImages="+productImages+
                ",specsUrl="+specUrl+
                ",shortDescription="+shortDescription+
                ",skus="+skus+
                ",details="+details+
                ",publisher="+publisher+
                ",authorDesc="+authorDesc+
                ",code="+code+
                ",isbn="+isbn+
                ",category="+category+
                '}';
    }
}
