package com.icecat.walmart;

import com.icecat.Scrapper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.util.LinkedList;
import java.util.List;



public class Walmart extends Scrapper {
    private List<String> products() {
        List<String> productList = new LinkedList<>();
        for (int i = 1; i <= 1; i++) {
            String url = Constants.SWIMMING_POOLS.replace("%data%", i + "");
            String html = get_html(url);
            Document document = parse_html(html);
            System.out.println(document);
            Element pID = document.getElementById("searchProductResult");
           /* Elements elements = pID.getElementsByClass("tile-content");
            for(Element element : elements){
                String href = element.getElementsByTag("a").get(0).attr("href");
                productList.add(Constants.BASE_URL+href);
            }*/

        }

        System.out.println(productList);
        return productList;
    }

    public static void main(String[] args) {
        Walmart walmart = new Walmart();
        walmart.products();
    }
}
