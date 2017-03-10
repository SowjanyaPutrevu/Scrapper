package com.icecat.hypertec;

import com.icecat.Scrapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Created by Sowjanya on 3/7/2017.
 */
public class Hypertec extends Scrapper {

    private List<String> getCategory(){
        List<String> category = new LinkedList<>();
        String url = Constants.CATEGORY_URL;
        String html = get_html(url);
        Document document = parse_html(html);
        Elements nav = document.getElementsByClass("frontpage-block");
        for(Element element1 :nav){
            Element navigation = element1.getElementsByTag("a").get(0);
            String href = navigation.attr("href");
            if(href.contains(Constants.CATEGORY_URL)){
            }else{
                String pUrl = Constants.CATEGORY_URL+href+"?limit=30&p=%data%";
                category.add(pUrl);
            }
        }
        //System.out.println(category);
        return category;
    }
    private List<String> page(){
        List<String> pages = new LinkedList<>();
        //for(int i = 0; i < getCategory().size(); i++ ) {
            for (int page = 1; page <= 10; page++) {
                String url = "http://www.hypertec.co.uk/products/accessories-13?limit=30&p=%data%".replace("%data%",page+"");
                String html = get_html(url);

                Document document = parse_html(html);
                Elements elements = document.getElementsByClass("products-grid");
                for(Element element : elements) {
                    pages.add(element.getElementsByTag("a").attr("href"));
                }
                }

        //}
        //System.out.println(pages);
        return pages;
    }
    private String getName(){
        String text = null;
        //for(int i = 0 ; i < page().size(); i++) {
            String url =  page().get(1);
            String html = get_html(url);
            Document document = parse_html(html);
            text = document.getElementsByClass("product-name").get(0).getElementsByTag("h1").get(0).text();
            System.out.println(text);
        //}
        return text;
    }
    private String getDescription(){
        String text = null;
        for(int i = 0 ; i < page().size(); i++) {
           String url =  page().get(i);
           String html = get_html(url);
           Document document = parse_html(html);
           text = document.getElementsByClass("std").get(0).text();
        }
        return text;
    }
    private List<String> getImageList(){
        List<String> images = new LinkedList<>();
        //for(int i = 0 ; i < page().size(); i++) {
            String url =  page().get(1);
            String html = get_html(url);
            Document document = parse_html(html);
            Elements elements = document.getElementsByClass("product-image");
            for(Element element : elements) {
                Elements elements1 = element.getElementsByTag("a");
                for (Element element1 : elements1) {
                    String text = element1.attr("href");
                    images.add(text);
                }
            }
                System.out.println(images);
                return images;
    }
    private Map<String,String> specs() {
        Map<String, String> map = new HashMap<>();
        String key = null;
        String value = null;
        //for(int i = 0 ; i < page().size(); i++) {
        String url = page().get(1);
        String html = get_html(url);
        Document document = parse_html(html);

        Elements even = document.getElementsByClass("even");
        Elements odd = document.getElementsByClass("odd");
        if(document.hasClass("even")){
        for(Element element : even){
            key = element.getElementsByClass("label").text();
            value = element.getElementsByClass("data").text();
            map.put(key,value);
            }
        }else{
            for(Element element : odd){
                key = element.getElementsByClass("label").text();
                value = element.getElementsByClass("data").text();
                map.put(key,value);
            }
        }
       // }
        System.out.println(map);
        return map;

    }

    public static void main(String[] args) {
        Hypertec hypertec = new Hypertec();
        hypertec.specs();
    }
}
