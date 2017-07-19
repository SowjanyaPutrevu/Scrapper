package com.icecat.toshiba;

import com.icecat.BrandSpecs;
import com.icecat.Scrapper;
import com.icecat.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

import java.util.*;


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
        /*list.add("http://www.toshiba.co.uk/laptops/satellite-pro/satellite-pro-a50-d/satellite-pro-a50-d-12x/");
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
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-a40-c/tecra-a40-c-1kf/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-a40-c/tecra-a40-c-1e5/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-a40-c/tecra-a40-c-1df/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-a40-c/tecra-a40-c-1ht/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-a40-c/tecra-a40-c-18r/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-a40-c/tecra-a40-c-18q/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-a40-c/tecra-a40-c-151/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-a50-c/tecra-a50-c-218/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-a50-c/tecra-a50-c-217/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-a50-c/tecra-a50-c-200/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-a50-c/tecra-a50-c-1zv/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-a50-c/tecra-a50-c-1gg/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-a50-c/tecra-a50-c-1gf/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-a50-c/tecra-a50-c-1ge/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-z40-c/tecra-z40t-c-10m/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-z40-c/tecra-z40t-c-10l/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-z40-c/tecra-z40-c-11x/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-z40-c/tecra-z40-c-136/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-z40-c/tecra-z40-c-12z/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-z40-c/tecra-z40-c-12x/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-z40-c/tecra-z40-c-12e/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-z40-c/tecra-z40-c-11f/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-z40-c/tecra-z40-c-106/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-z40-c/tecra-z40-c-105/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-z50-c/tecra-z50-c-140/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-z50-c/tecra-z50-c-138/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-z50-c/tecra-z50-c-151/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-z50-c/tecra-z50-c-12f/");
        list.add("http://www.toshiba.co.uk/laptops/tecra/tecra-z50-c/tecra-z50-c-10m/");*/
        /*list.add("http://www.toshiba.co.uk/laptops/portege/portege-a30-c/portege-a30-c-1cz/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-a30-c/portege-a30-c-1hf/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-a30-c/portege-a30-c-1he/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-a30-c/portege-a30-c-13e/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-x20w-d/portege-x20w-d-10v/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-x20w-d/portege-x20w-d-10q/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-x30-d/portege-x30-d-10z/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-x30-d/portege-x30-d-10x/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-x30-d/portege-x30-d-10w/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-x30-d/portege-x30-d-10v/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z20t-c/portege-z20t-c-144/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z20t-c/portege-z20t-c-155/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z20t-c/portege-z20t-c-152/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z20t-c/portege-z20t-c-13z/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z20t-c/portege-z20t-c-121/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z20t-c/portege-z20t-c-11q/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z20t-c/portege-z20t-c-11h/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30-c-188/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30-c-1cw/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30-c-1cv/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30-c-16z/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30-c-16p/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30-c-16l/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30-c-16k/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30-c-16j/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30-c-16h/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30t-c-139/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30t-c-133/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30-c-193/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30-c-156/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30-c-155/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30-c-153/");*/
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30-c-152/");
        list.add("http://www.toshiba.co.uk/laptops/portege/portege-z30-c/portege-z30-c-151/");

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
    private String getDescription(Document document){
        Element element = document.getElementsByClass("productIntroText").get(0);
        String text = element.getElementsByTag("ul").text();
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
    private BrandSpecs brandSpecs(String url){
        BrandSpecs brandSpecs = new BrandSpecs();
        Document brandDocument = parse_html(get_html(url));
        String title = getTitle(brandDocument);
        brandSpecs.setName(title);
        String partno = getPartNo(brandDocument);
        brandSpecs.setBrand_name(partno);
        String desc = getDescription(brandDocument);
        brandSpecs.setDescription(desc);
        List<String> img = getImages(brandDocument);
        brandSpecs.setImagesList(img);
        Map<String,String> map = details(brandDocument);
        brandSpecs.setDetails(map);

        String purl = seriesOverview(brandDocument);
        String html = get_html(purl);
        Document document1 = parse_html(html);

        Map<String,String> fdescription = new HashMap<>();
        Map<String,String> fImages     = new HashMap<>();

        Elements tags = document1.getElementsByClass("productOverviewRow");
        Elements tagImages = document1.getElementsByClass("productOverviewMedia");

        int j = 0;
        for(Element element : tags){
            Elements feature =  element.getElementsByTag("h2");
            String ftext = feature.text();
            feature.remove();
            String dtext = element.getElementsByTag("p").text();
            fdescription.put(ftext,dtext);
            Elements feImgs = tagImages.get(j).getElementsByTag("img");
            fImages.put(ftext, Constants.BASE_URL+feImgs.attr("src"));
            j++;
        }
        brandSpecs.setFeatures(fdescription);
        brandSpecs.setFeatureImages(fImages);

        System.out.println(brandSpecs);
        return brandSpecs;
    }
    private Map<String,String> paccessories(Document document){
        Map<String,String> map = new HashMap<>();
        String url = accessories(document);
        String html = get_html(url);
        Document document1 = parse_html(html);

        return map;
    }
    public void writeFile(String filePath){
        Toshiba toshiba = new Toshiba();
        String headers = "\"Title\"," +
                "\"PartNo\"," +
                "\"Description\"," +
                "\"Image 1\"," +
                "\"Image 2\"," +
                "\"Image 3\"," +
                "\"Image 4\"," +

                "\"rtb title 1\"," +
                "\"rtb description 1\","+
                "\"rtb image 1\"," +

                "\"rtb title 2\"," +
                "\"rtb description 2\"," +
                "\"rtb image 2\"," +

                "\"rtb title 3\"," +
                "\"rtb description 3\"," +
                "\"rtb image 3\"," +

                "\"rtb title 4\"," +
                "\"rtb description 4\"," +
                "\"rtb image 4\"," +

                "\"rtb title 5\"," +
                "\"rtb description 5\"," +
                "\"rtb image 5\", " +

                "\"rtb title 6\"," +
                "\"rtb description 6\"," +
                "\"rtb image 6\"," +

                "\"rtb title 7\"," +
                "\"rtb description 7\"," +
                "\"rtb image 7\"," +

                "\"rtb title 8\"," +
                "\"rtb description 8\"," +
                "\"rtb image 8\"," +

                "\"rtb title 9\"," +
                "\"rtb description 9\"," +
                "\"rtb image 9\"," +

                "\"rtb title 10\"," +
                "\"rtb description 10\"," +
                "\"rtb image 10\"," +

                "\"rtb title 11\"," +
                "\"rtb description 11\"," +
                "\"rtb image 11\"," +

                "\"rtb title 12\"," +
                "\"rtb description 12\"," +
                "\"rtb image 12\"," +

                "\"rtb title 13\"," +
                "\"rtb description 13\"," +
                "\"rtb image 13\"," +

                "\"rtb title 14\"," +
                "\"rtb description 14\"," +
                "\"rtb image 14\"," +

                "\"rtb title 15\"," +
                "\"rtb description 15\"," +
                "\"rtb image 15\",";
        List<String> extraHeaders = new ArrayList<>();
        Set<String> extraHeaderSet = new HashSet<>();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            List<String> list = toshiba.productList();
            for (int index = 0; index < list.size() ; index++) {
                String url = list.get(index);
                //String url = "http://www.toshiba.co.uk/laptops/tecra/tecra-z40-c/tecra-z40t-c-10m/";
                BrandSpecs brandSpecs = toshiba.brandSpecs(url);
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getName()) + "\",");
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getBrand_name()) + "\",");
                String description = brandSpecs.getDescription() != null ? brandSpecs.getDescription() : "null";
                bw.write("\"" + Utils.formatForCSV(description) + "\",");

                int i = 0;
                //pictures 3
                List<String> images = brandSpecs.getImagesList();
                if (images != null)
                    for (String image : images) {
                        bw.write("\"" + Utils.formatForCSV(image) + "\",");
                        i++;
                        if (i == 4 ) break;
                    }
                while (i < 4) {
                    bw.write(",");
                    i++;
                }
                i=0;
                for (Map.Entry<String, String> entry : brandSpecs.getFeatures().entrySet()) {
                    bw.write("\"" + Utils.formatForCSV(entry.getKey()) + "\",\"" + Utils.formatForCSV(entry.getValue()) + "\"," +
                            "\"" + Utils.formatForCSV(brandSpecs.getFeatureImages().get(entry.getKey())) + "\",");
                    i++;
                    if(i == 15) break;
                }

                while( i < 15){
                    bw.write("\"\",\"\",\"\",");
                    i++;
                }


                Map<String, String> features = brandSpecs.getDetails();
                for(String key: features.keySet()){
                    if( !extraHeaderSet.contains(key) ){
                        extraHeaders.add(key);
                        extraHeaderSet.add(key);
                    }
                }
                for(String key : extraHeaders) {
                    bw.write( "\"" + Utils.formatForCSV(features.get(key)) + "\",");
                }
                bw.newLine();
               System.out.println("Wrote " + index + " product to file");
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            for(String s : extraHeaders){
                headers += "\"" + s + "\",";
            }
            headers += "\n";

            File mFile = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String result = "";
            String line = "";
            while( (line = br.readLine()) != null){
                result = result + line;
                result += "\n";
            }
            result = headers + result;
            FileOutputStream fos = new FileOutputStream(mFile);
            fos.write(result.getBytes());
            fos.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Toshiba toshiba = new Toshiba();
        String filePath = "C:\\Users\\Sowjanya\\Documents\\Toshiba";
        toshiba.writeFile(filePath + File.separator + "test-protege2"+".csv");
    }

}
