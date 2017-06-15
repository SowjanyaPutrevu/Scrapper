package com.icecat.toshiba;

import com.icecat.BrandSpecs;
import com.icecat.Scrapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.security.interfaces.ECKey;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sowjanya on 6/12/2017.
 */
public class Toshiba extends Scrapper {
    /*private List<String> productList(){
        List<String> list = new LinkedList<>();
        String url = Constants.SATELLITEPRO;
        String html = get_html(url);
        Document document = parse_html(html);
        Element nav = document.getElementById("items");
        Elements tags = nav.getElementsByTag("article");
        for(Element tag : tags){
            Element pClass = tag.getElementsByClass("gallery-link").get(0);
            String href = pClass.attr("href");
            href = href.replace("gallery","filter");
            list.add(href);

        }
        list.add("http://www.toshiba.co.uk/laptops/product-filter/?sFamily=satellite-pro-a&sSeries=satellite-pro-a50-c");
        //System.out.println(list);
        return list;
    }
    private List<String> pList(){
        List<String> list = new LinkedList<>();
        List<String> products = productList();
        for(int i =  0 ; i < products.size() ; i++ ){
            String url = productList().get(i);
            String html = get_html(url);
            Document document = parse_html(html);
            Element element = document.getElementById("items");
            Elements tags = element.getElementsByTag("article");
            for(Element tag : tags){
                Element htag = tag.getElementsByTag("h4").get(0);
                String href = htag.getElementsByTag("a").get(0).attr("href");
                list.add(href);
            }
        }
        System.out.println(list);
        return list;
    }*/
    private List<String> productList(){
        List<String> list = new LinkedList<>();
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-a50-d/satellite-pro-a50-d-12x/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-a50-d/satellite-pro-a50-d-12p/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-r40-c/satellite-pro-r40-c-13k/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-r40-c/satellite-pro-r40-c-12h/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-r40-c/satellite-pro-r40-c-12w/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-r40-c/satellite-pro-r40-c-10r/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-r50-c/satellite-pro-r50-c-17c/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-r50-c/satellite-pro-r50-c-179/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-r50-c/satellite-pro-r50-c-15w/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-r50-c/satellite-pro-r50-c-12n/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-r50-c/satellite-pro-r50-c-12e/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-r50-c/satellite-pro-r50-c-11m/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-a50-c/satellite-pro-a50-c-24w/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-a50-c/satellite-pro-a50-c-23p/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-a50-c/satellite-pro-a50-c-207/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-a50-c/satellite-pro-a50-c-204/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-a50-c/satellite-pro-a50-c-1gw/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-a50-c/satellite-pro-a50-c-1mw/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-a50-c/satellite-pro-a50-c-1gh/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-a50-c/satellite-pro-a50-c-1gd/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-a50-c/satellite-pro-a50-c-1gc/");
        list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-a50-c/satellite-pro-a50-c-1g9/");


        return list;
    }
    private String getTitle(Document document){
        String title = document.getElementsByTag("h1").get(0).text();
        return title;
    }
    private String getPartNo(Document document){
        Element element = document.getElementsByTag("hgroup").get(0);
        String partNo = element.getElementsByTag("h2").get(0).text();
        partNo = partNo.replace("Part number: ","");
        return partNo;
    }
    private List<String> getDescription(Document document){
        List<String> text = new LinkedList<>();
        Element element = document.getElementsByClass("productIntroText").get(0);
        Elements tags = element.getElementsByTag("li");
        for(Element tag : tags){
            text.add(tag.text());
        }
        return text;
    }
    private List<String> getImages(Document document){
        List<String> img = new LinkedList<>();
        Element element = document.getElementsByClass("productIntroMedia").get(0);
        Elements tags = element.getElementsByTag("img");
        for(Element tag : tags){
            String href = tag.attr("src");
            href = Constants.BASE_URL+href;
            img.add(href);
        }
        return img;
    }
    private Map<String,String> details(Document document){
        Map<String,String> map = new HashMap<>();
        Element element = document.getElementById("specification");
        Elements elements = element.getElementsByTag("tr");
        for(Element tag : elements){
            String key = tag.getElementsByTag("td").get(0).text();
            String value = tag.getElementsByTag("td").get(1).text();
            map.put(key,value);
        }
        return map;
    }

    private String seriesOverview(Document document){


        Element pClass = document.getElementsByClass("productTabRow").get(0);
        String seriesOverview = pClass.getElementsByTag("a").get(1).attr("href");
        return seriesOverview;
    }
    private String accessories(Document document){
        Element pClass = document.getElementsByClass("productTabRow").get(0);
        String accessories = pClass.getElementsByTag("a").get(2).attr("href");
        return accessories;
    }
    private Map<String,String> series(Document document){
        Map<String,String> map = new HashMap<>();
        List<String> list = new LinkedList<>();
        String url = seriesOverview(document);
        String html = get_html(url);
        Document document1 = parse_html(html);
        Elements elements = document1.getElementsByClass("productOverviewRow");
        for(Element element : elements){
            String title = element.getElementsByTag("h2").get(0).text();
            String desc = element.getElementsByTag("p").get(0).text();
            String image = element.getElementsByTag("img").get(0).attr("src");

        }
        return map;
    }


    private BrandSpecs brandSpecs(String url){
        BrandSpecs brandSpecs = new BrandSpecs();
        Document brandDocument = parse_html(get_html(url));
        String title = getTitle(brandDocument);
        brandSpecs.setName(title);
        String partno = getPartNo(brandDocument);
        brandSpecs.setBrand_name(partno);
        List<String> desc = getDescription(brandDocument);
        brandSpecs.setSpecUrl(desc);
        List<String> img = getImages(brandDocument);
        brandSpecs.setImagesList(img);
        Map<String,String> map = details(brandDocument);
        brandSpecs.setDetails(map);
        System.out.println(brandSpecs);
        return brandSpecs;
    }

    public static void main(String[] args) {
        Toshiba toshiba = new Toshiba();
        List<String> list = toshiba.productList();
        for (int i = 0 ; i < list.size() ; i++){
            toshiba.brandSpecs(list.get(i));
        }
    }

}
