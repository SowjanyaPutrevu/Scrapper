package com.icecat.student100;

import com.icecat.BrandSpecs;
import com.icecat.Scrapper;
import com.icecat.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;


public class Toys extends Scrapper{

    private List<String> products(){
        List<String> productList = new LinkedList<>();
       for(int i = 10; i <= 20; i++){
            String url = Constants.CATEGORY_URL.replace("%data%",i+"");
            //String url = "https://webshop.studio100.com/en/categories/page6.html";
            String html = get_html(url);
            Document document = parse_html(html);
            Element element = document.getElementById(Constants.PRODUCT_CON_ID);
            Elements elements = element.getElementsByClass(Constants.PRODUCT_URL_CLASS);
            for(Element element1 : elements){
                String href  = element1.getElementsByTag("a").get(0).attr("href");
                productList.add(href);
            }
        }
        System.out.println(productList.size());
        return productList;
    }

    private String title(Document document){
        Element element = document.getElementById(Constants.TITLE_ID);
        String title = element.getElementsByTag("h1").get(0).text();
        return title;
    }
    private String description(Document document){
        String text = null;
        Element element = document.getElementsByClass(Constants.DESC_CLASS).get(3);
       //for(Element tag : element) {
            //if(tag.hasClass(Constants.DESC_CLASS)){
                text = element.getElementsByTag("p").text();
                //System.out.println(text);
            //}
        //}
        return text;
    }
    private List<String> img(Document document){
        List<String> images = new LinkedList<>();
        Element element = document.getElementById(Constants.IMAGE_ID);
        Elements elements = element.getElementsByClass(Constants.IMAGE_CLASS);
        for(Element pImg : elements){
            String href = pImg.getElementsByTag("a").get(0).attr("href");
            images.add(href);
        }
        return images;
    }

    private Map<String,String> getDetails(Document document){
        Map<String,String> details = new HashMap<>();
        Element element = document.getElementById(Constants.TABLE_ID);
        Elements elements = element.getElementsByClass("table");
        for(Element table : elements){
            Elements tags = table.getElementsByTag("tr");
            for(Element tag : tags){
                String key = tag.getElementsByTag("th").text();
                String value = tag.getElementsByTag("td").text();
                details.put(key,value);
            }
        }
        //System.out.println(details);
        return details;
    }
    private BrandSpecs brandSpecs(String url ){
        BrandSpecs brandSpecs = new BrandSpecs();
        Document brandDocument = parse_html(get_html(url));

        String title = title(brandDocument);
        brandSpecs.setName(title);
        String description = description(brandDocument);
        brandSpecs.setDescription(description);
        List<String> image = img(brandDocument);
        brandSpecs.setImagesList(image);
        Map<String,String> details = getDetails(brandDocument);
        brandSpecs.setDetails(details);

        System.out.println(brandSpecs);
        return brandSpecs;
    }


    public static void writeFile(String filePath) {
        Toys toys = new Toys();
        String headers = "\"Title\"," +
                "\"Description\"," +
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
                "\"Image11\",";
        List<String> extraHeaders = new ArrayList<>();
        Set<String> extraHeaderSet = new HashSet<>();

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            List<String> list = toys.products();
            for (int index = 0; index < list.size() ; index++) {
                String url = list.get(index);
                BrandSpecs brandSpecs = toys.brandSpecs(url);
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getName()) + "\",");
                String description = brandSpecs.getDescription() != null ? brandSpecs.getDescription() : "null";
                bw.write("\"" + Utils.formatForCSV(description) + "\",");

                int i = 0;
                //pictures 3
                List<String> images = brandSpecs.getImagesList();
                if (images != null)
                    for (String image : images) {
                        bw.write("\"" + Utils.formatForCSV(image) + "\",");
                        i++;
                        if (i == 11) break;
                    }
                while (i < 11) {
                    bw.write(",");
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
        Toys toys = new Toys();
        String filePath = "C:\\Users\\Sowjanya\\Documents\\Student100";
        // books.brandSpecs(url);
        toys.writeFile(filePath + File.separator + "Toys00"+".csv");
    }
}
