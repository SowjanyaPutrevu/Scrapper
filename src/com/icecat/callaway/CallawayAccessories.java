package com.icecat.callaway;

import com.icecat.BrandSpecs;
import com.icecat.Scrapper;
import com.icecat.Specification;
import com.icecat.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by Sowjanya on 1/30/2017.
 */
public class CallawayAccessories extends Scrapper{
    private List<String> getBrandUrls() {
        List<String> brandUrls = new ArrayList<>();

        for (int start = 0; ; start += 12) {
            String url = Constants.ACCESORIES_CATEGORY_URL.replace("%data%", start + "");
            String html = get_html(url);
            Document document = parse_html(html);
            Elements spans = document.getElementsByTag("a"); //git test
            if (spans.size() == 0)
                break;

            for (Element element : spans) {
                String names = element.attr("href");
                brandUrls.add(Constants.BASE_URL + names);
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

        Element description = brandDocument.getElementById(Constants.A_BRAND_DESCRIPTION_ID);
        if (description != null)
            brandSpecs.setDescription(description.text());

        Map<String, List<String>> fdescription = new HashMap<>();
        List<String> flist = new LinkedList<>();

        Elements tags = brandDocument.getElementsByClass("product-description-container");

        int j = 0;
        for (Element element : tags) {
            Elements feature = element.getElementsByTag("font");
            String ftext = feature.text();
            feature.remove();
            Elements dtag = element.getElementsByTag("ul");
            for(Element text : dtag) {
                Elements dli = text.getElementsByTag("li");
                for(Element value : dli ){
                    String dtext = value.text();
                    flist.add(dtext);
                }
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
    public void writeToFile(BrandSpecs brandSpecs,String filePath) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            bw.write("\"BrandName\","+"\""+Utils.formatForCSV(brandSpecs.getName())+"\"");
            bw.newLine();
            String imagesList = "";
            if (brandSpecs.getImagesList() != null) {
                for (String imageUrl : brandSpecs.getImagesList()) {
                    imagesList += "\"" + Utils.formatForCSV(imageUrl) + "\",";
                }
            }
            bw.write("\"images\", " + imagesList);
            bw.newLine();
            bw.write("\"Description\","+"\""+Utils.formatForCSV(brandSpecs.getDescription())+"\"");
            bw.newLine();
            String videosList = "";
            if(brandSpecs.getVideos() != null){
                for(String video : brandSpecs.getVideos()) {
                    videosList += "\"" + Utils.formatForCSV(video) + "\"," ;
                }
            }
            bw.write("\"video\"," +  videosList );
            bw.newLine();
            int i =1;
            for(Map.Entry<String,List<String>> entry : brandSpecs.getAfeatures().entrySet()) {
                List<String> features = entry.getValue();
                for(i = 0; i <features.size();i++) {
                    bw.write("\"RTB" + i + "\","  +"\""+ features.get(i)+"\",");
                    bw.newLine();
                }
            }
            String colorsList = "";
            if(brandSpecs.getColors() != null){
                for(String color : brandSpecs.getColors()) {
                    videosList += "\"" + Utils.formatForCSV(color) + "\"," ;
                }
            }
            bw.write("\"colors\"," +  colorsList );
            bw.newLine();


            bw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CallawayAccessories scrapper = new CallawayAccessories();

        String filePath = "C:\\Users\\Sowjanya\\Documents\\CallawayAccessories";
        List<String> brandUrls =  scrapper.getBrandUrls();
       // String[] brandUrls = {"http://www.callawaygolf.com/golf-balls/balls-2017-superhot-70-15pk.html"};
       for(int i=0;i<brandUrls.size();i++) {
            	BrandSpecs brandSpecs = scrapper.getBrandSpecs(brandUrls.get(i));
            	scrapper.writeToFile(brandSpecs,filePath+ File.separator+brandSpecs.getName()+".csv");
        }
		/*for(String brandUrl : brandUrls){
			System.out.println(scrapper.getBrandSpecs(brandUrl));
		}*/
    }

}
