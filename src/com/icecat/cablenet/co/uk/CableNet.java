package com.icecat.cablenet.co.uk;

import com.icecat.Scrapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Sowjanya on 3/8/2017.
 */
public class CableNet extends Scrapper {
    private List<String>products(){
        List<String> products = new LinkedList<>();
        String url = Constants.BASE_URL;
        String html = get_html(url);
        Document document = parse_html(html);
        Elements elements = document.getElementsByClass("featured");
        for(Element element : elements){
            Elements elements1 = element.getElementsByTag("h2").get(0).getElementsByTag("a");
            for(Element element1 : elements1){
                String text = element1.attr("href");
                products.add(Constants.CATEGORY_URL+text);
                products.remove(Constants.CATEGORY_URL+"support/optical-transceivers.htm");
            }

        }
        //System.out.println(products);
        return products;
    }
    private List<String> category(){
        List<String> category = new LinkedList<>();
        for(int i = 0; i < products().size(); i++) {
            String url = products().get(i);
            String html = get_html(url);
            Document document = parse_html(html);
            Element element = document.getElementById(Constants.NAV_ID);
            Elements elements = element.getElementsByTag("p");
            for(Element element1 : elements){
                String pUrl = element1.getElementsByTag("a").get(0).attr("href");
                category.add(Constants.CATALOG_URL+pUrl);
            }
        }
        System.out.println(category);
        return category;
    }

    public static void main(String[] args) {
        CableNet cableNet = new CableNet();
        cableNet.category();
    }
}
