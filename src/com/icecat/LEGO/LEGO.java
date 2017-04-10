package com.icecat.LEGO;

import com.icecat.Scrapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;


public class LEGO extends Scrapper {
    private List<String> getCategory(){
        List<String> category = new LinkedList<>();
        String url = Constants.SETS_URL;
        String html = get_html(url);
        Document document = parse_html(html);
        Elements elements = document.getElementsByClass(Constants.CATEGORY_CLASS);
        for(Element element : elements){
            String href = element.getElementsByTag("a").get(0).attr("href");
            category.add(Constants.BASE_URL+href);
        }
        //System.out.println(category);
        return category;
    }
    private List<String> catList(){
        List<String> cat = new LinkedList<>();
       // for(int i = 0; i< getCategory().size(); i++ ){
            String url = get_html(getCategory().get(0));
            Document document = parse_html(url);
            Elements elements = document.getElementsByClass(Constants.CAT_CLASS);
            for(Element element : elements){
                String href = element.getElementsByTag("a").get(0).attr("href");
                cat.add(Constants.BASE_URL+href);
            }
       // }
       // System.out.println(cat);
        return cat;
    }
    private List<String> productList(){
        List<String> product = new LinkedList<>();
       // for(int i = 0 ; i < catList().size() ; i++) {
            String url = get_html(catList().get(0));
            Document document = parse_html(url);
            if(document.getElementsByClass("pagination__view-all") != null) {
                Elements viewAll = document.getElementsByClass("pagination__view-all");
                for(Element element1 : viewAll) {
                    String view = element1.attr("href");
                    String phtml = get_html(Constants.BASE_URL + view);
                    Document pdoc = parse_html(phtml);
                    Elements elements = pdoc.getElementsByClass(Constants.PRODUCT_CLASS);
                    for (Element element : elements) {
                        String href = element.getElementsByTag("a").get(0).attr("href");
                        product.add(Constants.BASE_URL + href);
                    }
                }
            }else {
                Elements elements = document.getElementsByClass(Constants.PRODUCT_CLASS);
                for (Element element : elements) {
                    String href = element.getElementsByTag("a").get(0).attr("href");
                    product.add(Constants.BASE_URL + href);
                }
            }
       //}
       System.out.println(product.size());
        return product;
    }
    private String getTitle(){
        String title = null;
        for(int i = 0 ; i < productList().size(); i++) {
            String url = productList().get(i);
            String html = get_html(url);
            Document document = parse_html(html);
            title = document.getElementsByTag("h1").get(0).text();
           // System.out.println(title);
        }
        return title;
    }
    /*private String pdf(){
        String pdf = null;
        String url = "https://shop.lego.com/en-US/Ferrari-FXX-K-Development-Center-75882";
        String phtml = get_html(url);
        Document document = parse_html(phtml);
        Elements elements = document.getElementsByClass("product-features__instructions-container");
        for(Element element : elements){
            String href = element.getElementsByTag("a").get(0).attr("href");
            String html = get_html(href);
            Document document1 = parse_html(html);
            Elements elements1 = document1.getElementsByClass("row");
            for(Element element1 : elements1){
                 Elements tags = element1.getElementsByTag("a");
                for(Element tag : tags){
                    pdf = tag.attr("href");
                }
            }
        }
        System.out.println(pdf);
        return pdf;
    }*/

    public static void main(String[] args) {
        LEGO lego = new LEGO();
        lego.productList();
    }
}
