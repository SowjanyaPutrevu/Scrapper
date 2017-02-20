package com.icecat.cleveland;

import com.icecat.BrandSpecs;
import com.icecat.Scrapper;
import com.icecat.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sowjanya on 2/17/2017.
 */
public class Cleveland extends Scrapper {

    private List<String> getProductUrl(){
        List<String> productUrl = new ArrayList<>();
        for(int index = 0; index < 35; index = index +12) {
            String url = Constants.CATEGORY_URL.replace("%data%", "index");
            String html = get_html(url);
            Document document = parse_html(html);
            Elements elements = document.getElementsByClass(Constants.PRODUCT_CLASS);
            for (Element element : elements) {
                String pUrl = element.attr("href");
                productUrl.add(Constants.BASE_URL + pUrl);
            }
        }

        /*for(int index = 0 ; index < productUrl.size();index++){
            System.out.println(productUrl.get(index));
        }
        System.out.println(productUrl.size());*/
        return productUrl;
    }

    private String getDescription(Document document ){
        Element element = document.getElementById(Constants.TAB_ID);
        String description = element.getElementsByTag("p").get(0).text();
       // System.out.println(description);
        return description;
    }
    private String getProductName(Document document){
       String productName = document.getElementsByTag("h1").get(0).text();
       //System.out.println(productName);
       return productName;
    }
    private String getShortDescription(Document document){
        Element element = document.getElementsByClass("b-short-description").get(0);
        String shortDesc = element.getElementsByTag("p").text();
        //System.out.println(shortDesc);
        return shortDesc;
    }

    private List<String> getVideos(Document document){
        List<String> videos = new ArrayList<>();
        Element element = document.getElementById("tab1");
        Elements elements = element.getElementsByTag("iframe");
            for(Element element1 : elements) {
                String video = element1.attr("src");
                videos.add(video);
            }
        //System.out.println(videos);
        return videos;
    }

    private List<String> getImageList(Document document){
        List<String> images = new ArrayList<>();
        Elements elements = document.getElementsByClass("productthumbnail");
        for(Element element : elements) {
            String img = element.attr("data-lgimg");
            JSONObject jsonObject = new JSONObject(img);
            String imageUrl = jsonObject.getString("url");
            images.add(imageUrl);
        }
        return  images;
    }

    private BrandSpecs getBrandSpecs(String url ){
        BrandSpecs brandSpecs = new BrandSpecs();
        Document brandDocument =parse_html(get_html(url));
        String desc = getDescription(brandDocument);
        brandSpecs.setDescription(desc);


        Map<String,String> fdescription = new HashMap<>();
        Map<String, String> fImages     = new HashMap<>();

        Elements tags = brandDocument.getElementsByClass(Constants.RTB_TITLE_CLASS);
        Elements tagImages = brandDocument.getElementsByClass(Constants.RTB_IMAGE_CLASS);

        int j = 0;
        for(Element element : tags){
            Elements feature =  element.getElementsByTag("h2");
            String ftext = feature.text();
            feature.remove();
            String dtext = element.text();
            fdescription.put(ftext,dtext);
            Elements feImgs = tagImages.get(j).getElementsByTag("img");
            fImages.put(ftext, feImgs.get(0).attr("src"));
            j++;
        }
        brandSpecs.setFeatures(fdescription);
        brandSpecs.setFeatureImages(fImages);

        String productName = getProductName(brandDocument);
        brandSpecs.setBrand_name(productName);

        List<String> videos = getVideos(brandDocument);
        brandSpecs.setVideos(videos);

        List<String> images = getImageList(brandDocument);
        brandSpecs.setImagesList(images);


        String shortDesc = getShortDescription(brandDocument);
        brandSpecs.setShortDescription(shortDesc);
        System.out.println(brandSpecs);
        return brandSpecs;
    }

    public void  writeToFile(BrandSpecs brandSpecs, String filePath){
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            bw.write("\"sku\","
                    + "\"brand\","
                    + "\"productName\","
                    + "\"picture 1\","
                    + "\"picture 2\","
                    + "\"picture 3\","
                    + "\"picture 4\","
                    + "\"picture 5\","
                    + "\"picture 6\","
                    + "\"picture 7\","
                    + "\"picture 8\","
                    + "\"picture 9\","
                    + "\"picture 10\","
                    + "\"picture 11\","
                    + "\"picture 12\","
                    + "\"video 1\","
                    + "\"video 2\","
                    + "\"video 3\","
                    + "\"video 4\","
                    + "\"video 5\","
                    + "\"video 6\","
                    + "\"video 7\","
                    + "\"video 8\","
                    + "\"video 9\","
                    + "\"video 10\","
                    + "\"video 11\","
                    + "\"video 12\","
                    + "\"short description\","
                    + "\"brand description\","

                    + "\"rtb title 1\","
                    + "\"rtb description 1\","
                    + "\"rtb image 1\","

                    + "\"rtb title 2\","
                    + "\"rtb description 2\","
                    + "\"rtb image 2\","

                    + "\"rtb title 3\","
                    + "\"rtb description 3\","
                    + "\"rtb image 3\","

                    + "\"rtb title 4\","
                    + "\"rtb description 4\","
                    + "\"rtb image 4\","

                    + "\"rtb title 5\","
                    + "\"rtb description 5\","
                    + "\"rtb image 5\", "

                    + "\"rtb title 6\","
                    + "\"rtb description 6\","
                    + "\"rtb image 6\",");
            bw.newLine();
            bw.write("\""+Utils.formatForCSV(brandSpecs.getName())+"\",");
            bw.write("\"Cleveland\",");
            bw.write("\""+Utils.formatForCSV(brandSpecs.getBrand_name())+"\",");

            int i = 0;
            //pictures 12
            List<String> images = brandSpecs.getImagesList();
            if(images!=null)
                for(String image : images){
                    bw.write("\"" + Utils.formatForCSV(image) + "\",");
                    i++;
                    if( i == 12 ) break;
                }
            while(i<12){
                bw.write(",");
                i++;
            }
            i=0;
            List<String> videos = brandSpecs.getVideos();
            if(videos != null)
                for(String video : videos){
                    bw.write("\"" + Utils.formatForCSV(video) + "\",");
                    i++;
                    if( i == 12 ) break;
                }
            while(i<12){
                bw.write(",");
                i++;
            }
            bw.write("\""+Utils.formatForCSV(brandSpecs.getShortDescription())+"\",");
            bw.write("\""+Utils.formatForCSV(brandSpecs.getDescription())+"\",");


            i=0;
            for (Map.Entry<String, String> entry : brandSpecs.getFeatures().entrySet()) {
                bw.write("\"" + Utils.formatForCSV(entry.getKey()) + "\",\"" + Utils.formatForCSV(entry.getValue()) + "\"," +
                        "\"" + Utils.formatForCSV(brandSpecs.getFeatureImages().get(entry.getKey())) + "\",");
                i++;
                if(i == 6) break;
            }

            while( i < 6){
                bw.write("\"\",\"\",\"\",");
                i++;
            }
            bw.flush();
            bw.close();


        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Cleveland clubs = new Cleveland();
        //clubs.getProductUrl();
       // clubs.brandSpecs("http://www.clevelandgolf.com/en/wedges-/rtx-3-tour-raw/MRTX3TR.html");
        //clubs.getShortDescription("http://www.clevelandgolf.com/en/wedges-/rtx-3-tour-raw/MRTX3TR.html");
        String filePath = "C:\\Users\\Sowjanya\\Documents\\ClevelandClubs";
        List<String> brandUrls =  clubs.getProductUrl();
        // String[] brandUrls = {"http://www.callawaygolf.com/golf-balls/balls-2017-superhot-70-15pk.html"};
        for(int i=0;i<brandUrls.size();i++) {
            BrandSpecs brandSpecs = clubs.getBrandSpecs(brandUrls.get(i));
            clubs.writeToFile(brandSpecs,filePath+ File.separator+brandSpecs.getBrand_name()+".csv");
        }
    }
}
