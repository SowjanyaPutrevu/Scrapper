package com.icecat.ottosimon;

import com.icecat.BrandSpecs;
import com.icecat.Scrapper;
import com.icecat.Utils;
import com.icecat.amazon.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class OttoSimon extends Scrapper {
    private List<String> getProductsList(){
        List<String> getProducts = new LinkedList<>();
        String[] eans = Constants.EANS;
        String url = Constants.SEARCH_URL;
        for(int i = 101 ; i < eans.length ; i++ ){
            String surl = url.replace("%data%",eans[i]+"");
            String html = get_html(surl);
            Document document = parse_html(html);
            if(document.getElementsByClass("product-info") != null){
                Elements elements = document.getElementsByClass(Constants.SEARCH_CLASS);
                for(Element element1 : elements) {
                    String href = element1.getElementsByTag("a").get(0).attr("href");
                    getProducts.add(Constants.BASE_URL + href);
                    break;
                }
            }
        }
       // System.out.println(getProducts);
        return getProducts;
    }
    private String getItemNo(Document document){

        Element item = document.getElementsByClass("product-id").get(0);

        String value = item.getElementsByClass("value").get(0).text();

        System.out.println(value);
        return value;
    }
    private String getTitle(Document document){
        String title = document.getElementsByTag("h1").get(0).text();
        System.out.println(title);
        return title;
    }
    private List<String> getImagesList(Document document){
        List<String> imageList = new LinkedList<>();
        /*if(document.getElementsByClass("thumbnail") != null) {
            Elements elements = document.getElementsByClass("thumbnail");
            for (Element element : elements) {
                Elements elements1 = element.getElementsByTag("img");
                for (Element element1 : elements1) {
                    String url = element1.attr("src");
                    if (url.contains("small")) {
                        url = url.replace("small", "large");
                        imageList.add(Constants.BASE_URL + url);
                    }
                }
            }
        }else{*/
            Elements elements = document.getElementsByClass("slider-list-item");
            for(Element element : elements){
                Elements elements1 = element.getElementsByTag("img");
                for(Element element1 : elements1){
                    String src = element1.attr("src");
                    imageList.add(Constants.BASE_URL+src);
                }
            }
        //}

        System.out.println(imageList);
        return imageList;
    }
    private String getShortDescription(Document document){
        if(document.getElementsByClass("description") != null) {
            String desc = document.getElementsByClass("description").get(0).text();
            System.out.println(desc);
            return desc;
        }else{
            return null;
        }
    }
    private String getDescription(Document document){
        String desc = null;
        if(document.getElementById("description") != null) {
        Element element = document.getElementById("description");
            Elements elements = element.getElementsByClass("description");
            for (Element element1 : elements) {
                desc = element1.text();
            }
            System.out.println(desc);
            return desc;
        }else{
            return null;
        }
    }
    private Map<String,String> getDetails(Document document){
        Map<String,String> map = new HashMap<>();
        Elements elements = document.getElementsByTag("tr");
        for(Element element : elements){
          String name =  element.getElementsByClass("name").get(0).text();
          String value = element.getElementsByClass("value").get(0).text();
          map.put(name,value);
        }
        System.out.println(map);
        return map;
    }

    private BrandSpecs brandSpecs(String url){
        BrandSpecs brandSpecs = new BrandSpecs();
        Document document = parse_html(get_html(url));

        String title = getTitle(document);
        brandSpecs.setBrand_name(title);

        List<String> images = getImagesList(document);
        brandSpecs.setImagesList(images);

        String desc = getShortDescription(document);
        brandSpecs.setShortDescription(desc);

        String ldesc = getDescription(document);
        brandSpecs.setDescription(ldesc);

        Map<String,String> details = getDetails(document);
        brandSpecs.setDetails(details);

        String itemno = getItemNo(document);
        brandSpecs.setName(itemno);

        //System.out.println(brandSpecs);
        return brandSpecs;
    }
    public void writeFile(String filePath) {
        OttoSimon ottoSimon = new OttoSimon();
        String headers = "\"Title\"," +
                "\"Item No\","+
                "\"Short Description\","+
                "\"Long Description\"," +
                "\"Image1\"," +
                "\"Image2\"," +
                "\"Image3\","+
                "\"Image4\","+
                "\"Image5\",";
        List<String> extraHeaders = new ArrayList<>();
        Set<String> extraHeaderSet = new HashSet<>();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));

            List<String> products = ottoSimon.getProductsList() ;
            for (int index = 0; index < products.size() ; index++) {
                String url = products.get(index);
                //String url = "https://www.casadellibro.com/libro-un-ano-entre-los-persas/mkt0003066624/4306635";
                BrandSpecs brandSpecs = ottoSimon.brandSpecs(url);
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getBrand_name()) + "\",");
                bw.write("\""+Utils.formatForCSV(brandSpecs.getName())+"\",");
               String shortDescription = brandSpecs.getDescription() != null ? brandSpecs.getShortDescription() : "null";
                bw.write("\"" + Utils.formatForCSV(shortDescription) + "\",");

                String description = brandSpecs.getDescription() != null ? brandSpecs.getDescription() : "null";
                bw.write("\"" + Utils.formatForCSV(description) + "\",");
                int i = 0;
                //pictures 5
                List<String> images = brandSpecs.getImagesList();
                if (images != null)
                    for (String image : images) {
                        bw.write("\"" + Utils.formatForCSV(image) + "\",");
                        i++;
                        if (i == 5) break;
                    }
                while (i < 5) {
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
        OttoSimon ottoSimon = new OttoSimon();
        String filePath = "C:\\Users\\Sowjanya\\Documents\\OttoSimon";
        // books.brandSpecs(url);
        ottoSimon.writeFile(filePath + File.separator + "Toys21"+".csv");

    }
}
