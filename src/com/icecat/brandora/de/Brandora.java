package com.icecat.brandora.de;

import com.icecat.BrandSpecs;
import com.icecat.Scrapper;
import com.icecat.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Brandora extends Scrapper {

    private List<String> getProductList(){
        List<String> products = new LinkedList<>();
        for(int i = 1632 ; i <= 2016 ; i+=32) {
            String url = Constants.PRODUCT_URL.replace("%data%",i+"");
            String html = get_html(url);
            Document document = parse_html(html);
            Elements elements = document.getElementsByClass("productGalleryInnerTable");
            for(Element element : elements){
                Element element1 = element.getElementsByClass("galleryTD").get(0);
                String productsUrl = element1.getElementsByTag("a").attr("href");
                products.add(productsUrl);
            }
        }
       //System.out.println(products);
        return products;
    }
    private Map<String,String> details(Document document){
        Map<String,String> details =  new HashMap<>();
        //for(int i = 0; i < getProductList().size();i++) {

            Elements elements = document.getElementsByClass("factTR");
            for(Element element : elements){
                String key = element.getElementsByClass("factTDLeft").text();
                String value = element.getElementsByClass("factTDRight").text();
                details.put(key,value);
            }
        //}
        //System.out.println(details);
        return details;
    }
    private List<String> imageList(String html){
        Pattern pattern = Pattern.compile("imgData.AddImg\\('(.*?)'");
        Matcher matcher = pattern.matcher(html);
        Set<String> set = new HashSet<>();
        while(matcher.find()){
            String current = Constants.BASE_URL+matcher.group(1);
            set.add(current);
        }
        //System.out.println(set);
        return new ArrayList<>(set);
    }
    private String description(Document document){
        String text = null;
        //for(int i = 0; i < getProductList().size(); i++){
        Elements elements = document.getElementsByClass("descriptionDiv");
        for(Element element : elements) {
            text = element.getElementsByTag("p").text();
        }
        return text;
        //}
    }
    private BrandSpecs brandSpecs(String url){
        BrandSpecs brandSpecs = new BrandSpecs();

        String html = get_html(url);
        Document document = parse_html(html);

        String description = description(document);
        brandSpecs.setDescription(description);

        Map<String,String> details = details(document);
        brandSpecs.setDetails(details);

        List<String> images = imageList(html);
        brandSpecs.setImagesList(images);

        System.out.println(brandSpecs);
        return brandSpecs;

    }
   private void writeFile(String filePath){
        Brandora brandora = new Brandora();
        String headers ="\"Description\"," +
                "\"Image1\"," +
                "\"Image2\"," +
                "\"Image3\"," +
                "\"Image4\"," +
                "\"Image5\"," +
                "\"Image6\"," +
                "\"Image7\"," +
                "\"Image8\"," +
                "\"Image9\"," +
                "\"Image10\"," +
                "\"Image11\"," +
                "\"Image12\"," +
                "\"Image13\"," +
                "\"Image14\"," +
                "\"Image15\"," +
                "\"Image16\"," +
                "\"Image17\"," +
                "\"Image18\"," +
                "\"Image19\"," +
                "\"Image20\"," ;

            List<String> extraHeaders = new ArrayList<>();
            Set<String> extraHeaderSet = new HashSet<>();
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
                List<String> list = brandora.getProductList();
                for(int index = 0 ; index < list.size() ;index++) {
                    String url = list.get(index);

                    BrandSpecs brandSpecs = brandora.brandSpecs(url);
                    String description = brandSpecs.getDescription() != null ? brandSpecs.getDescription() : "null";
                    bw.write("\"" + Utils.formatForCSV(description) + "\",");
                    int i = 0;
                    //pictures 3
                    List<String> images = brandSpecs.getImagesList();
                    if (images != null)
                        for (String image : images) {
                            bw.write("\"" + Utils.formatForCSV(image) + "\",");
                            i++;
                            if (i == 20) break;
                        }
                    while (i < 20) {
                        bw.write(",");
                        i++;
                    }

                i = 0;
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
            }
            bw.flush();
            bw.close();
            }catch (Exception e){
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
        Brandora brandora = new Brandora();
        String filePath = "C:\\Users\\Sowjanya\\Documents\\Brandora";
        brandora.writeFile(filePath + File.separator + "Toys6"+".csv");

        // brandora.getProductList();
       /* List<String>list=  brandora.getProductList();
        for(int i = 0 ; i < list.size(); i++){
            brandora.brandSpecs(list.get(i));
        }*/
       //brandora.brandSpecs("http://www.brandora.de/ProductPage.aspx?IzmLang=9&Pro=394239&PRT=toys&OPS=2&LUR=http%3a%2f%2fwww.brandora.de%2fProductListPage.aspx%3fIzmLang%3d9%26PRT%3dtoys%26LPS%3d0%26&");
        //brandora.details();

    }
}
