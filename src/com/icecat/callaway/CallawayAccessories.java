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

/**
 * Created by Sowjanya on 1/30/2017.
 */
public class CallawayAccessories extends Scrapper {
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
        Elements title = brandDocument.getElementsByTag("h1");
        for(Element text : title){
            if(text != null)
                brandSpecs.setBrand_name(text.text());
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

        //Get color information
        Element form = brandDocument.getElementById(Constants.BRAND_PRODUCT_CONFIG_FORM_ID);
        String action = Constants.FILTER_ATTRIBUTES_URL;
        String params = "?";
        Elements inputs = form.getElementsByTag("input");
        Map<String, String> initAttributes = new HashMap<>();

        for(Element input : inputs) {
            String name = input.attr("name");
            String value = input.attr("value");
            if(value!=null && !value.isEmpty()) {
                params += "&" + name + "=" + value;
                initAttributes.put(name, "&" + name + "=" + value);
            }
        }

        String json = get_html(action + params);
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("attributes");
        List<String> colors = parseJson(jsonArray);
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

    public Set<ProductSpecs> getProducts(String brandUrl, boolean writeToFile, String filePath, BrandSpecs brandSpecs, Map<String, String> brandConversions){
        String html = get_html(brandUrl);
        Document document = parse_html(html);
        String brandName = getBrandName(brandUrl);
        return getProducts(document, writeToFile, filePath, brandSpecs, brandConversions);
    }
    private Set<ProductSpecs> getProducts(Document brandDocument, boolean writeToFile, String filePath, BrandSpecs brandSpecs, Map<String, String> brandConversions){
        Set<ProductSpecs> products = new HashSet<>();

        //http://www.callawaygolf.com/on/demandware.store/Sites-CG-Site/en_US/ProductConfigurator-FilteredAttributes?format=json&pid=drivers-great-big-bertha-epic-2017&vid=drivers-great-big-bertha-epic-2017&cgid=drivers&qty=1&condition=BNW
        //http://www.callawaygolf.com/on/demandware.store/Sites-CG-Site/en_US/ProductConfigurator-Process?&format=json&pid=drivers-great-big-bertha-epic-2017&vid=drivers-great-big-bertha-epic-2017&cgid=drivers&qty=1&condition=BNW
        Element form = brandDocument.getElementById(Constants.BRAND_PRODUCT_CONFIG_FORM_ID);
        String action = Constants.FILTER_ATTRIBUTES_URL;
        String params = "?";
        Elements inputs = form.getElementsByTag("input");
        Map<String, String> initAttributes = new HashMap<>();

        for(Element input : inputs) {
            String name = input.attr("name");
            String value = input.attr("value");
            if(value!=null && !value.isEmpty()) {
                params += "&" + name + "=" + value;
                initAttributes.put(name, "&" + name + "=" + value);
            }
        }

        String json = get_html(action + params);

        JSONObject jsonObject = new JSONObject(json);

        Map<String, List<Specification>> attributes;

        JSONArray jsonArray = jsonObject.getJSONArray("attributes");

        int step = 0;
        String baseUrl = action + "?" + initAttributes.get("pid") + initAttributes.get("vid") + initAttributes.get("qty")
                + initAttributes.get("cgid") + initAttributes.get("format") + initAttributes.get("condition") ;

        return products;
    }
    private List<String> parseJson(JSONArray jsonArray) {
        List<String> colors = new ArrayList<>();

        for( int i=0; i < jsonArray.length(); i++ ){
            JSONObject object =(JSONObject)jsonArray.get(i);
            JSONArray values = (JSONArray)object.get("values");
            for(Object obj : values) {
                JSONObject value = (JSONObject) obj;
                colors.add(value.getString("displayValue"));
            }
        }
        return colors;
    }

    public void writeToFile(BrandSpecs brandSpecs,String filePath) {
        try {

            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            bw.write("\"SKU\","+
                    "\"ProductName\","+
                    "\"Picture1\", " +
                    "\"Picture2\"," +
                    "\"Picture3\"," +
                    "\"Picture4\"," +
                    "\"Picture5\"," +
                    "\"Picture6\"," +
                    "\"Picture7\"," +
                    "\"Picture8\"," +
                    "\"Picture9\"," +
                    "\"Picture10\"," +
                    "\"Picture11\"," +
                    "\"Picture12\"," +
                    "\"Picture13\"," +
                    "\"Picture14\"," +
                    "\"Picture15\"," +
                    "\"video\"," +
                    "\"Description\","+
                    "\"RTB1\"," +
                    "\"RTB2\"," +
                    "\"RTB3\"," +
                    "\"RTB4\"," +
                    "\"RTB5\"," +
                    "\"RTB6\"," +
                    "\"RTB7\"," +
                    "\"RTB8\"," +
                    "\"RTB9\"," +
                    "\"RTB10\"," +
                    "\"RTB11\"," +
                    "\"RTB12\"," +
                    "\"RTB13\"," +
                    "\"RTB14\"," +
                    "\"RTB15\"," +
                    "\"RTB16\"," +
                    "\"RTB17\"," +
                    "\"Color1\","+
                    "\"Color2\"," +
                    "\"Color3\"," +
                    "\"Color4\"," +
                    "\"Color5\"," +
                    "\"Color6\"," +
                    "\"Color7\"," +
                    "\"Color8\"," );
            bw.newLine();
            bw.write("\""+Utils.formatForCSV(brandSpecs.getName())+"\",");
            bw.write("\""+Utils.formatForCSV(brandSpecs.getBrand_name())+"\",");

            int i = 0;
            //pictures 12
            List<String> images = brandSpecs.getImagesList();
            if(images!=null)
                for(String image : images){
                    bw.write("\"" + Utils.formatForCSV(image) + "\",");
                    i++;
                    if( i == 15 ) break;
                }
            while(i<15){
                bw.write(",");
                i++;
            }
            i=0;
            List<String> videos = brandSpecs.getVideos();
            if(videos != null)
                for(String video : videos){
                    bw.write("\"" + Utils.formatForCSV(video) + "\",");
                    i++;
                    if( i == 1 ) break;
                }
            while(i<1){
                bw.write(",");
                i++;
            }
            bw.write("\""+Utils.formatForCSV(brandSpecs.getDescription())+"\",");
            i=0;
            for(Map.Entry<String,List<String>> entry : brandSpecs.getAfeatures().entrySet()) {
                List<String> features = entry.getValue();
                for(i = 0; i <features.size();i++) {
                    bw.write("\""+ features.get(i)+"\",");
                    i++;
                    if(i==21) break;
                }
                while(i<21){
                    bw.write(",");
                    i++;
                }
            }
            String colorsList = "";
            if(brandSpecs.getColors() != null){
                for(String color : brandSpecs.getColors()) {
                    colorsList += "\"" + Utils.formatForCSV(color) + "\"," ;
                }
            }
            bw.write( colorsList );
            bw.newLine();
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws  Exception {
        CallawayAccessories scrapper = new CallawayAccessories();

        String filePath = "C:\\Users\\Sowjanya\\Documents\\CallawayAccessories";
        List<String> brandUrls =  scrapper.getBrandUrls();
        // String[] brandUrls = {"http://www.callawaygolf.com/golf-balls/balls-2017-superhot-70-15pk.html"};
        for(int i=0;i<brandUrls.size();i++) {
            BrandSpecs brandSpecs = scrapper.getBrandSpecs(brandUrls.get(i));
            scrapper.writeToFile(brandSpecs,filePath+ File.separator+brandSpecs.getName()+".csv");
        }
    }

}




