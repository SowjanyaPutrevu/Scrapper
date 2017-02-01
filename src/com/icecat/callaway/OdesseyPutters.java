package com.icecat.callaway;

import com.icecat.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Sowjanya on 1/31/2017.
 */
public class OdesseyPutters extends Scrapper {
    private List<String> getBrandUrls() {
        List<String> brandUrls = new ArrayList<>();

        for (int start = 0; ; start += 12) {
            String url = Constants.ODESSEY_CATEGORY_URL.replace("%data%", start + "");
            String html = get_html(url);
            Document document = parse_html(html);
            Elements spans = document.getElementsByTag("a"); //git test
            if (spans.size() == 0)
                break;

            for (Element element : spans) {
                String names = element.attr("href");
                brandUrls.add(Constants.ODESSEY_BASE_URL + names);
            }
        }
        return brandUrls;
    }
    private List<String> getBrandImages(Document document) {
        Elements images = document.getElementsByTag("img");
        Elements productImages = new Elements();
        for(int i=0; i<images.size(); i++){
            if(images.get(i).hasClass("rsTmb"))
                productImages.add(images.get(i));
        }
        return Utils.getImageUrls(productImages);
    }

    private String getBrandName(String brandUrl) {
        String[] tokens = brandUrl.split("/");
        return tokens[tokens.length - 1].replace(".html", "");
    }

    private String getSpecsUrl(String brandUrl) {
        return Constants.PRODUCT_SPECS_URL.replace("%data%", getBrandName(brandUrl) );
    }

    public BrandSpecs getBrandSpecs(String brandUrl) {
        String brandName = getBrandName(brandUrl);
        return getBrandSpecs(brandUrl, brandName);
    }

    private BrandSpecs getBrandSpecs(String brandUrl, String brandName) {
        BrandSpecs brandSpecs = new BrandSpecs();
        brandSpecs.setName(brandName);

        String brandHtml = get_html(brandUrl);
        Document brandDocument = parse_html(brandHtml);
        List<String> images = getBrandImages(brandDocument);

        if (images != null && !images.isEmpty()) {
            brandSpecs.setImagesList(images);
        }

        Element description = brandDocument.getElementById(Constants.BRAND_DESCRIPTION_ID);
        if (description != null)
            brandSpecs.setDescription(description.text());

        Map<String, List<String>> fdescription = new HashMap<>();
        List<String> flist = new LinkedList<>();

        Elements tags = brandDocument.getElementsByClass("product-description-container");

        int j = 0;
        for (Element element : tags) {
            Elements feature = element.getElementsByTag("h5");
            String ftext = feature.text();
            feature.remove();
            Elements dtag = element.getElementsByTag("p");
            for(Element text : dtag) {

                    String dtext = text.text();
                    flist.add(dtext);

            }
            fdescription.put(ftext, flist);
        }
        brandSpecs.setAfeatures(fdescription);

        Set<String> videos = new HashSet<>();
        Elements videoClass = brandDocument.getElementsByClass(Constants.VIDEO_CLASS);
        for (Element video : videoClass) {
            String videoUrl = video.attr("data-url");
            videos.add(videoUrl);
        }

        List<String> youtube = new ArrayList<>();
        for (String video : videos) {
            String html = get_html(video);
            Document document = parse_html(html);
            Elements classes = document.getElementsByClass("mk-video-container");
            for (Element url : classes) {
                Element iframe = url.getElementsByTag("iframe").first();
                String videoUrls = iframe.attr("src");
                youtube.add(videoUrls);
            }
        }
        brandSpecs.setVideos(youtube);

        List<String> colors = new ArrayList<>();
        String html = get_html(brandUrl);
        Document document = parse_html(html);
        Elements classes = document.getElementsByClass("ajax-input-text");
        for(Element i : classes){
            String color = i.outerHtml();
            colors.add(color);
        }
        brandSpecs.setColors(colors);

        String specsUrl = getSpecsUrl(brandUrl);
        String specsHtml = get_html(specsUrl);
        Document specsDocument = parse_html(specsHtml);
        Elements element = specsDocument.getElementsByClass("table-responsive");

        Map<String,List<Specification>> map = new HashMap<>();

        for(Element element1 : element) {
            List<List<Specification>> tableData = parseTable(element1);
            for (List<Specification> row : tableData) {
                String k = row.get(0).getValues();
                map.put(k,row);
            }
        }
        brandSpecs.setGeneralSpecs(map);
        return brandSpecs;
    }


    public Set<String> getSKUs(String brandUrl) {
        String brandHtml = get_html(brandUrl);
        Document document = parse_html(brandHtml);
        return getSKUs(document);
    }

    private Set<String> getSKUs(Document brandDocument) {
        Elements metaTags = brandDocument.getElementsByTag("meta");

        Set<String> skuList = new HashSet<>();
        for (Element element : metaTags) {
            if (element.attr("itemprop").equals("sku")) {
                skuList.add(element.attr("content"));
            }
        }
        return skuList;
    }


    private List<List<Specification>> parseTable(Element element ) {

        Elements tables = element.getElementsByTag("table");
        Element toi =  tables.last();
        return Utils.getTableData(toi);
    }

    public static void writeToFile(BrandSpecs brand, String filePath ) {
        if (brand == null) {
            System.out.println("Null brand passed");
            return;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            bw.write("\"key\",\"value\"");
            bw.newLine();
            String imagesList = "";
            if (brand.getImagesList() != null) {
                for (String imageUrl : brand.getImagesList()) {

                    imagesList += "\"" + Utils.formatForCSV(imageUrl) + "\",";
                }
            }
            String videosList = "";
            if(brand.getVideos() != null){
                for(String video : brand.getVideos()) {
                    videosList += "\"" + Utils.formatForCSV(video) + "\"," ;
                }
            }
            bw.newLine();
            bw.write("\"images\", " + imagesList);
            bw.newLine();
            bw.write("\"name\",\"" + Utils.formatForCSV(brand.getName()) + "\"");
            bw.newLine();
            bw.write("\"description\",\"" + Utils.formatForCSV(brand.getDescription()) + "\"");
            bw.newLine();
            bw.write("\"video\"," +  videosList );
            bw.newLine();
            int i =1;
            for(Map.Entry<String,List<String>> entry : brand.getAfeatures().entrySet()) {
                List<String> features = entry.getValue();
                for(i = 0; i <features.size();i++) {
                    bw.write("\"RTB" + i + "\","  +"\""+ features.get(i)+"\",");
                    bw.newLine();
                }
            }
            for(Map.Entry<String,List<Specification>> entry : brand.getGeneralSpecs().entrySet()){
                List<Specification> specifications = entry.getValue();
                for( i = 0; i < specifications.size();i++){
                    // "\""+a+"\",b"
                    bw.write("\""+specifications.get(i).getName()+"\","+"\""+specifications.get(i).getValues() + "\",");
                    bw.newLine();
                }
            }

            bw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        OdesseyPutters scrapper = new OdesseyPutters();

        String filePath = "C:\\Users\\Sowjanya\\Documents\\OdesseyPutters";
        List<String> brandUrls =  scrapper.getBrandUrls();
       // String[] brandUrls = {"http://de.callawaygolf.com/golfschl%C3%A4ger/putters/odyssey-works/putters-2016-works-marxman-fang-versa.html"};
        for(int i=0;i<brandUrls.size();i++) {
        BrandSpecs brandSpecs = scrapper.getBrandSpecs(brandUrls.get(i));
        System.out.println(filePath+File.separator+brandSpecs.getName()+".csv");
        String path = filePath+File.separator+brandSpecs.getName()+".csv";
        scrapper.writeToFile(brandSpecs,path);
        }
        /*for(String brandUrl : brandUrls){
            System.out.println(scrapper.getBrandSpecs(brandUrl));
        }*/
    }
}

