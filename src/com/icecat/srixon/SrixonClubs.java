package com.icecat.srixon;

import com.icecat.*;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by Sowjanya on 2/17/2017.
 */
public class SrixonClubs extends Scrapper {

    private List<String> getProductUrl(){
        List<String> productUrl = new ArrayList<>();

        String url = Constants.CATEGORY_URL;
        String html = get_html(url);
        Document document = parse_html(html);
        Elements elements = document.getElementsByClass(Constants.PRODUCT_CLASS);
        for (Element element : elements) {
            String pUrl = element.attr("href");
            productUrl.add(Constants.BASE_URL + pUrl);
        }


        /*for(int index = 0 ; index < productUrl.size();index++){
            System.out.println(productUrl.get(index))
        }*/
        // System.out.println(productUrl.size());
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
    private List<String> getSkus(){
        List<String> sku = new ArrayList<>();
        String [] hands = {"Left","Right"};
        for(String hand : hands) {
            String url = Constants.PRODUCT_CONFIG_URL.replace("%data%", hand);
            String html = get_html(url);
            Document document = parse_html(html);
            Element element = document.getElementById("va-loftBounce");
            Elements options = element.getElementsByTag("option");

            for (Element option : options) {
                try {
                    String pUrl = option.attr("value");
                    //System.out.println(pUrl);
                    String pHtml = get_html(pUrl);
                    Document pdocument = parse_html(pHtml);
                    String skus = pdocument.getElementsByClass("b-product-number").get(0).text();
                    sku.add(skus);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(sku.size());
        return sku;
    }
    private List<List<Specification>> parseTable(Element element ) {

        Elements tables = element.getElementsByTag("table");
        Element toi =  tables.first();
        return Utils.getTableData(toi);

    }

    private BrandSpecs getBrandSpecs(String url, boolean writeToFile, String filePath ){
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
            if(tagImages.size() > j) {
                Elements feImgs = tagImages.get(j).getElementsByTag("img");
                fImages.put(ftext, feImgs.get(0).attr("src"));
                j++;
            }
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

        List<List<Specification>> table = parseTable(brandDocument);

        Map<String, List<Specification>> specs = new HashMap<>();

        Map<String, Integer> colIndices = new HashMap<>();
        int k = 0;
        for (Specification s : table.get(0)) {
            colIndices.put(s.getName().toLowerCase(), k);
            k++;
        }
        for (List<Specification> row : table) {
            if (row.size() < k) {
                continue;
            }
            String key = "";
            Integer loft = colIndices.get("loft");
            Integer design = colIndices.get("design");
            Integer model = colIndices.get("model");
            Integer length = colIndices.get("length");
            Integer lie = colIndices.get("lie");
            if (model != null && length != null) {
                key = row.get(model).getValues() + " " + row.get(length).getValues();
            } else if (loft != null && design != null) {
                key = row.get(loft).getValues() + " " + row.get(design).getValues();
            } else if (lie != null && length != null) {
                key = row.get(lie).getValues() + " " + row.get(loft).getValues();
            }
            if (!key.equals(""))
                specs.put(key.replace("°", ".0"), row);
        }



        brandSpecs.setGeneralSpecs(specs);


        Map<String, Map<String, String>> products = new HashMap<>(); // Our data structure for collecting products we fetched along with its specs.
        Set<String> specIdentifiers = new HashSet<>();

        Elements productVariations = brandDocument.getElementsByClass("js-product-variations");
        if (productVariations != null) {
            Elements form = productVariations.first().getElementsByClass("b-attribute");
            //We have a list of uls which can have multiple values or options
            //avoid wasteful recursive calls by stopping when we find sku number! :D
            //if(brandDocument.hasClass("b-swatches-li")) {
            Elements hands = brandDocument.getElementsByClass("b-swatches-li"); //Hand
            if (hands.size() > 1) {
                for (Element hand : hands) {
                    Element anchor = hand.getElementsByTag("a").first();
                    String link = anchor.attr("href");
                    fetchProducts(link, 1, products, specIdentifiers);
                }
            } else {
                Element field = form.get(1);
                if (field.hasClass("attribute")) {
                    //A select field
                    Elements options = field.getElementsByTag("option");
                    for (Element option : options) {
                        fetchProducts(option.attr("value"), 2, products, specIdentifiers);
                    }
                } else {
                    System.out.println("Got an unknown input field!, not proceeding further");
                    System.out.println(field);
                }
            }

        } else {
            //No Product Variations found! :( Reverting to default way.
            List<String> skus = getSkus();
            brandSpecs.setSkus(skus);
        }

        if (writeToFile) {
            writeToFile(brandSpecs, filePath + File.separator + brandSpecs.getBrand_name() + "-new.csv", products, specIdentifiers, colIndices.keySet());
        }


        return brandSpecs;
    }

    public void fetchProducts(String link, int index, Map<String, Map<String, String>> products, Set<String> specIdentifiers){
        //This can fail miserably, because we are assuming there will always be this class and a span inside it! :D

        if(link == null || link == "") {
            return;
        }

        String html = get_html(link + "&Quantity=1&format=ajax&productlistid=undefined");
        Document document = parse_html(html);
        String sku = document.getElementsByClass("b-product-number").first().getElementsByTag("span").first().text();
        Elements form = document.getElementsByClass("b-attribute");
        if( sku.matches("^\\d+$")  || index >= form.size() ) { //is all numbers ^ means beginning, $ means ending, \d means number and \d+ means minimum 1 number
            //We got a product!
            Map<String, String> specs = new HashMap<>();
            for(Element field : form){
                if(field.hasClass("b-hand")){
                    if(field.getElementsByClass("selected").size() > 0 ) {
                        String value = field.getElementsByClass("selected").first().text();
                        specs.put("hand", value);
                    } else{
                        String value = field.getElementsByClass("available").first().text();
                        specs.put("hand", value);
                    }
                } else {
                    String label = field.getElementsByClass("b-attribute-label").first().text().toLowerCase();
                    specIdentifiers.add(label);
                    specs.put(label, field.getElementsByAttribute("selected").first().text());
                }
            }
            products.put(sku, specs);
            return;
        }
        Element field = form.get(index);
        if(field.hasClass("attribute")){
            //A select field
            Elements options = field.getElementsByTag("option");
            for(Element option : options) {
                fetchProducts(option.attr("value"), index + 1, products, specIdentifiers);
            }
        } else{
            System.out.println("Got an unknown input field!, not proceeding further");
            System.out.println(field);
        }
    }


    public void  writeToFile(BrandSpecs brandSpecs, String filePath, Map<String, Map<String, String>> products, Set<String> specIdentifiers, Set<String> modelKeys){
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            String headers = "\"sku\","
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
                    + "\"rtb image 6\","

                    +"\"Hand\",";

            Object[] sIObjects = specIdentifiers.toArray();
            String[] sIs= new String[sIObjects.length];
            int p = 0;
            for(Object si : sIObjects){
                headers += "\""+si+"\",";
                sIs[p] = si.toString();
                p++;
            }

            String[] mKeys = new String [ modelKeys.size() ];
            mKeys = modelKeys.toArray(mKeys);

            for(String mk : mKeys){
                headers += "\""+mk+"\",";
            }

            bw.write(headers);
            bw.newLine();
            int i = 0;
            if(products!=null)
                for(String sku : products.keySet()) {
                    bw.write("\"" + Utils.formatForCSV(sku) + "\",");

                    bw.write("\"Srixon\",");
                    bw.write("\"" + Utils.formatForCSV(brandSpecs.getBrand_name()) + "\",");

                    i = 0;
                    //pictures 12
                    List<String> images = brandSpecs.getImagesList();
                    if (images != null)
                        for (String image : images) {
                            bw.write("\"" + Utils.formatForCSV(image) + "\",");
                            i++;
                            if (i == 12) break;
                        }
                    while (i < 12) {
                        bw.write(",");
                        i++;
                    }
                    i = 0;
                    List<String> videos = brandSpecs.getVideos();
                    if (videos != null)
                        for (String video : videos) {
                            bw.write("\"" + Utils.formatForCSV(video) + "\",");
                            i++;
                            if (i == 12) break;
                        }
                    while (i < 12) {
                        bw.write(",");
                        i++;
                    }
                    if(brandSpecs.getShortDescription()!= null) {
                        bw.write("\"" + Utils.formatForCSV(brandSpecs.getShortDescription()) + "\",");
                        bw.write("\"" + Utils.formatForCSV(brandSpecs.getDescription()) + "\",");

                    }
                    i = 0;
                    if(brandSpecs.getFeatures() != null) {
                        for (Map.Entry<String, String> entry : brandSpecs.getFeatures().entrySet()) {
                            bw.write("\"" + Utils.formatForCSV(entry.getKey()) + "\",\"" + Utils.formatForCSV(entry.getValue()) + "\"," +
                                    "\"" + Utils.formatForCSV(brandSpecs.getFeatureImages().get(entry.getKey())) + "\",");
                            i++;
                            if (i == 6) break;
                        }
                    }

                    while (i < 6) {
                        bw.write("\"\",\"\",\"\",");
                        i++;
                    }

                    Map<String, String> specMap = products.get(sku);
                    bw.write("\"" + Utils.formatForCSV(specMap.get("hand")) + "\",");
                    String gSpec = "";
                    for (String si : sIs) {
                        bw.write("\"" + Utils.formatForCSV(specMap.get(si)) + "\",");
                        if (brandSpecs.getGeneralSpecs() != null && gSpec == "") {
                            Set<String> gSpecs = brandSpecs.getGeneralSpecs().keySet();
                            for (String s : gSpecs) {
                                if (specMap.get(si).contains(s)) {
                                    gSpec = s;
                                    break;
                                }
                            }
                        }
                    }

                    if( brandSpecs.getGeneralSpecs() != null ) {
                        List<Specification> generalSpecs = brandSpecs.getGeneralSpecs().get(gSpec);
                        if(generalSpecs != null) {
                            Map<String, String> conversions = new HashMap<>();
                            for (Specification specification : generalSpecs) {
                                conversions.put(specification.getName().toLowerCase(), specification.getValues());
                            }
                            for (String key : mKeys) {
                                bw.write("\"" + Utils.formatForCSV(conversions.get(key)) + "\",");
                            }
                        } else {
                            System.out.println("No spec matched here, that means its not just model<>design or loft<>length or whatever combinations");
                        }
                    }
                    bw.newLine();
                }
            bw.flush();
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SrixonClubs clubs = new SrixonClubs();


        // clubs.getProductUrl();
        String filePath = "C:\\Users\\Sowjanya\\Documents\\SrixonClubs";
       // clubs.getBrandSpecs("http://www.clevelandgolf.com/en/clubs/womens/wedges/womens-588-rtx-cb-2.0-black-satin/MW588RTXCB2.html",true,filePath );
        //  clubs.getBrandSpecs("http://www.clevelandgolf.com/en/putters-/tfi-2135-elevado/MTFI2135ELEV.html",true,filePath );
        //clubs.getSkus();

        List<String> brandUrls =  clubs.getProductUrl();
       // clubs.getBrandSpecs("http://www.clevelandgolf.com/en/wedges-/588-rtx-2.0-tour-satin/M588RTX2TS.html",true,filePath);

       for(int i = brandUrls.size()-1 ; i > 0 ; i--) {
            BrandSpecs brandSpecs = clubs.getBrandSpecs(brandUrls.get(i), true, filePath);
            //clubs.writeToFile(brandSpecs,filePath+ File.separator+brandSpecs.getBrand_name()+".csv");
        }
    }
}
