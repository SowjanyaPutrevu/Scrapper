package com.icecat.brandora.de;

import com.icecat.Scrapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sowjanya on 3/6/2017.
 */
public class Brandora extends Scrapper {

    private List<String> getProductList(){
        List<String> products = new LinkedList<>();
        for(int i = 0; i <= 64; i+=32) {
            String url = Constants.PRODUCT_URL.replace("%data%",i+"");
            String html = get_html(url);
            Document document = parse_html(html);
            Elements elements = document.getElementsByClass("productGalleryInnerTable");
            for(Element element : elements){
                Element element1 = element.getElementsByClass("galleryTD").get(0);
                String productsUrl = element1.getElementsByTag("a").attr("href");
                products.add(productsUrl);
            }
        }
        System.out.println(products.get(0));
        return products;
    }
    private Map<String,String> details(){
        Map<String,String> details =  new HashMap<>();
        //for(int i = 0; i < getProductList().size();i++) {
            String url = getProductList().get(1);
            String html = get_html(url);
            Document document = parse_html(html);
            Elements elements = document.getElementsByClass("factTR");
            for(Element element : elements){
                String key = element.getElementsByClass("factTDLeft").text();
                String value = element.getElementsByClass("factTDRight").text();
                details.put(key,value);
            }
        //}
        System.out.println(details);
        return details;
    }
    private List<String> imageList(){
        List<String> images = new LinkedList<>();
        //for(int i = 0; i < getProductList().size(); i++){
            String url = getProductList().get(1);
            String html = get_html(url);
            Document document = parse_html(html);
            String elements = document.getElementById("PSheetImg").attr("src");
            images.add(Constants.BASE_URL+elements);
        //}
        System.out.println(url);
        System.out.println(images);
        return images;
    }
    private String description(){
        //for(int i = 0; i < getProductList().size(); i++){
        String url = getProductList().get(1);
        String html = get_html(url);
        Document document = parse_html(html);
        String elements = document.getElementsByClass("descriptionDiv").get(0).getElementsByTag("p").text();
        System.out.println(elements);
        return elements;
        //}
    }
    public static void main(String[] args) {
        Brandora brandora = new Brandora();
        //brandora.details();
        brandora.imageList();
    }
}
