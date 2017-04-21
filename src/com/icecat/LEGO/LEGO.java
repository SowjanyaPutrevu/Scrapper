package com.icecat.LEGO;

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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class LEGO extends Scrapper {
    private List<String> getCategory(){
        List<String> category = new LinkedList<>();
        String filter = Constants.NO_FILTER;
        //for(int i = 1 ; i <= 54 ; i++) {
            //String url = filter.replace("%data%", i+"");
            String html = get_html(filter);
            Document document = parse_html(html);
            Elements elements = document.getElementsByClass("product-leaf__product");
            for(Element element : elements){
                String href = element.getElementsByTag("a").get(0).attr("href");
                category.add(Constants.BASE_URL+href);
               // System.out.println(category);
            }
       // }
        //System.out.println(category.size());
        return category;
    }

    private String getTitle(Document document){
        return  document.getElementsByTag("h1").get(0).text();
        //System.out.println(title);
    }
    private String getRoot(Document document){
        String root = document.getElementsByClass("brand-link").get(0).text();
        return root;
    }
    private String getVip(Document document){
        String vip = null;
        Elements elements = document.getElementsByClass("product-details__vip-points");
        for(Element element : elements){
            vip = element.text();
        }
        return vip;
    }
    private String getCode(Document document){
        String code = null;
        Elements elements = document.getElementsByClass("product-details__product-code");
        for(Element element : elements){
            code = element.text();
        }
        return code;
    }
    private String getAge(Document document){
        String age = null;
        Elements elements = document.getElementsByClass("product-details__ages");
        for(Element element : elements){
            age = element.text();
        }
        return age;
    }
    private String getPieces(Document document){
        String pieces = null;
        Elements elements = document.getElementsByClass("product-details__piece-count");
        for(Element element : elements){
            pieces = element.text();
        }
        return pieces;
    }
    private Map<String,String> getFeatures(Document document){
        Map<String,String> features = new HashMap<>();
        int i = 1;
        Elements elements = document.getElementsByClass("product-features__description");
        for(Element element : elements){
            Elements tags = element.getElementsByTag("li");
            for(Element tag : tags){
                features.put("RTB"+i+"",tag.text());
                i++;
            }
        }
        return features;
    }
    private String getDescription(Document document){
        String desc = null;
        Elements elements = document.getElementsByClass("product-features__description");
        for(Element element : elements){
            Elements tags = element.getElementsByTag("p");
            for(Element tag : tags){
                desc = tag.text();
            }
        }
        return desc;
    }
    private String getPdf(Document document){
        String url = Constants.PDF_URL.replace("%data%",getCode(document));
        String html = get_html(url);
        JSONObject jsonObject = new JSONObject(html);
        JSONArray jsonArray = jsonObject.getJSONArray("products");
        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
        return jsonObject1.getJSONArray("buildingInstructions").getJSONObject(0).getString("pdfLocation");
    }
    private List<String> getImages(Document document){
        List<String> images = new LinkedList<>();
        String url = Constants.IMAGE_URL.replace("%data%",getCode(document));
        String html = get_html(url);
        html = html.replace("/*jsonp*/s7sdkJSONResponse(","");
        html = html.replace(",\"32453735\");","");
        JSONObject jsonObject = new JSONObject(html);
        JSONObject object = jsonObject.getJSONObject("set");
        JSONArray array = object.getJSONArray("item");
        for(int i = 0 ; i < array.length(); i++){
            JSONObject items = array.getJSONObject(i);
            String iv = items.getString("iv");
            JSONObject item = items.getJSONObject("s");
            String value = item.getString("n");
            String iUrl = Constants.IMAGE.replace("%data%",value);
            iUrl = iUrl.replace("%id%",iv);
            images.add(iUrl);
        }
        return images;
    }

    private BrandSpecs brandSpecs(String url){
        BrandSpecs brandSpecs = new BrandSpecs();
        Document brandDocument =parse_html(get_html(url));
        String title = getTitle(brandDocument);
        if(title != null )
            brandSpecs.setName(title);

        String brand = getRoot(brandDocument);
        if(brand != null)
            brandSpecs.setBrand_name(brand);

        String code = getCode(brandDocument);
        if(code != null)
            brandSpecs.setCode(code);

        String vip = getVip(brandDocument);
        if(vip != null)
            brandSpecs.setVip(vip);

        String age = getAge(brandDocument);
        if(age != null)
            brandSpecs.setAge(age);

        String pieces = getPieces(brandDocument);
        if(pieces != null)
            brandSpecs.setPieces(pieces);

        String desc = getDescription(brandDocument);
        if(desc != null)
            brandSpecs.setDescription(desc);

        Map<String,String> features = getFeatures(brandDocument);
        if(pieces != null)
            brandSpecs.setFeatures(features);

        String pdf = getPdf(brandDocument);
        if(pdf != null)
            brandSpecs.setPdf(pdf);

        List<String> images = getImages(brandDocument);
        if(images != null)
            brandSpecs.setImagesList(images);

        //System.out.println(brandSpecs);
        return brandSpecs;
    }

    public void writeFile(String filePath) {
        LEGO lego = new LEGO();
        String headers = "\"Title\"," +
                "\"BrandName\"," +
                "\"Item No\"," +
                "\"Pieces\"," +
                "\"Age\"," +
                "\"Vip\"," +
                "\"Description\"," +
                "\"PDF\","+
                "\"RTB1\","+"\"RTB2\","+"\"RTB3\","+
                "\"RTB4\","+"\"RTB5\","+"\"RTB6\","+
                "\"RTB7\","+"\"RTB8\","+"\"RTB9\","+
                "\"RTB10\","+"\"RTB11\","+"\"RTB12\","+
                "\"RTB13\","+"\"RTB14\","+"\"RTB15\","+
                "\"RTB16\","+"\"RTB17\","+"\"RTB18\","+
                "\"RTB19\","+"\"RTB20\","+"\"RTB21\","+
                "\"RTB22\","+"\"RTB23\","+"\"RTB24\","+
                "\"RTB25\","+
                "\"Image1\","+"\"Image2\","+"\"Image3\","+
                "\"Image4\","+"\"Image5\","+"\"Image6\","+
                "\"Image7\","+"\"Image8\","+"\"Image9\","+
                "\"Image10\","+"\"Image11\","+"\"Image12\","+
                "\"Image13\","+"\"Image14\","+"\"Image15\","+"\"Image16\","+"\"Image17\","+"\"Image18\","+"\"Image19\","+"\"Image20\","+
                "\"Image21\","+"\"Image22\","+"\"Image23\","+"\"Image24\","+"\"Image25\","+"\"Image26\","+"\"Image27\","+"\"Image28\","+"\"Image29\","+
                "\"Image30\","+"\"Image31\","+"\"Image32\","+"\"Image34\","+"\"Image35\","+"\"Image36\",";



        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            bw.write(headers);

            List<String> products = lego.getCategory();
            for (int index = products.size()-1 ; index > 0; index--) {
                String url = products.get(index);
                //String url = "https://www.casadellibro.com/libro-un-ano-entre-los-persas/mkt0003066624/4306635";
                BrandSpecs brandSpecs = lego.brandSpecs(url);

                bw.newLine();
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getName()) + "\",");
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getBrand_name()) + "\",");
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getCode()) + "\",");
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getPieces()) + "\",");
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getAge()) + "\",");
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getVip()) + "\",");
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getDescription()) + "\",");
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getPdf()) + "\",");


                int i = 0;
                if(brandSpecs.getFeatures() != null) {
                    for (Map.Entry<String, String> entry : brandSpecs.getFeatures().entrySet()) {
                        bw.write("\"" + Utils.formatForCSV(entry.getValue()) + "\"," );
                        i++;
                        if (i == 25) break;
                    }
                }

                while (i < 25) {
                    bw.write(",");
                    i++;
                }

                i = 0;
                //pictures 5
                List<String> imagesList = brandSpecs.getImagesList();
                if (imagesList != null)
                    for (String image : imagesList) {
                        bw.write("\"" + Utils.formatForCSV(image) + "\",");
                        i++;
                        if (i == 35) break;
                    }
                while (i < 35) {
                    bw.write(",");
                    i++;
                }
            }

                bw.flush();
                bw.close();
        }catch(Exception e){
                e.printStackTrace();
            }
        }



    /*private List<String> getCategory(){
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
       for(int i = 0; i< getCategory().size(); i++ ){
            String url = get_html(getCategory().get(0));
            Document document = parse_html(url);
            Elements elements = document.getElementsByClass(Constants.CAT_CLASS);
            for(Element element : elements){
                String href = element.getElementsByTag("a").get(0).attr("href");
                cat.add(Constants.BASE_URL+href);
            }
       }
       // System.out.println(cat);
        return cat;
    }
    private List<String> productList(){
        List<String> product = new LinkedList<>();
        for(int i = 0 ; i < catList().size() ; i++) {
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
       }
       //System.out.println(product);
        return product;
    }*/

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
        String filePath = "C:\\Users\\Sowjanya\\Documents\\LEGO";
        // books.brandSpecs(url);
        lego.writeFile(filePath + File.separator + "missed"+".csv");
    }
}
