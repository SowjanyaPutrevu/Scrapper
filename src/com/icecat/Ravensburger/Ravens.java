package com.icecat.Ravensburger;

import com.icecat.BrandSpecs;
import com.icecat.Scrapper;
import com.icecat.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;


public class Ravens extends Scrapper {

    private List<String> productList(){
        List<String> list = new LinkedList<>();
        for(int i = 1 ; i <= 5 ; i++){
            String url = Constants.THEMES__URL.replace("%index%",i+"");
            String html = get_html(url);
            Document document = parse_html(html);
            Element element = document.getElementById("right");
            Elements elements =  element.getElementsByClass("productTeaser_main_111");
            for(Element pClass : elements){
                Elements tags = pClass.getElementsByTag("h4");
                for(Element tag : tags){
                    Elements ahref = tag.getElementsByTag("a");
                    for(Element a : ahref) {
                        String href = a.attr("href");
                        list.add(Constants.BASE_URL+href);
                    }
                }
            }
        }
        //System.out.println(list.size());
        return list;
    }

    private String getTitle(Document document){
        String text = document.getElementsByTag("h1").get(0).text();
        //System.out.println(text);
        return text;
    }

    private String getDescription(Document document){
        String text = null;
        Elements elements = document.getElementsByClass("productDetail_main");
        for(Element element : elements){
            text = element.getElementsByTag("div").get(0).text();
        }

       // System.out.println(barcode[1]);
        //System.out.println(text);
        return text;
    }

    private Map<String,String> features(Document document) {
        Map<String, String> details = new HashMap<>();
        Elements elements = document.getElementsByClass("small_left");
        int i = 0;
        for (Element element : elements) {
            Elements tags = element.getElementsByTag("a");
            String attr = tags.get(0).attr("title");
            if (attr.contains(":")) {
                String[] feature = attr.split(":");
                details.put(feature[0], feature[1]);
            } else {
                details.put("unknown" + i, attr);
            }

        }
        Elements elementright = document.getElementsByClass("small_right");
        for (Element element : elementright) {
            Elements tags = element.getElementsByTag("a");
            for (Element tag : tags) {
                String attr = tag.attr("title");
                details.remove("");
                if (attr.contains(":")) {
                    String[] feature = attr.split(":");
                    details.put(feature[0], feature[1]);
                } else {
                    details.put("unknown" + i, attr);
                }
            }
        }

            //System.out.println(details);
            return details;
        }

    private List<String> images(Document document){
        List<String> imageList = new LinkedList<>();
        Element element = document.getElementById(Constants.IMAGE_ID);
        try {
            Elements tags = element.getElementsByTag("a");
            for (Element tag : tags) {
                String href = tag.attr("href");
                imageList.add(href);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
            //System.out.println(imageList);
            return imageList;

    }
    private String points(Document document){
        Elements elements = document.getElementsByClass("blue");
        String text = elements.get(1).text();
        //System.out.println(text);
        return text;
    }
    private BrandSpecs brandSpecs(String url){
        BrandSpecs brandSpecs =  new BrandSpecs();
        Document brandDocument = parse_html(get_html(url));

        String title = getTitle(brandDocument);
        brandSpecs.setBrand_name(title);

        String description = getDescription(brandDocument);
        brandSpecs.setDescription(description);

        List<String> images = images(brandDocument);
        brandSpecs.setImagesList(images);

        String points = points(brandDocument);
        brandSpecs.setName(points);

        Map<String,String> details = features(brandDocument);
        brandSpecs.setDetails(details);

        System.out.println(brandSpecs);
        return brandSpecs;
    }
    public void writeFile(String filePath){
        Ravens ravens = new Ravens();
        String headers = "\"Title\"," +
                "\"Description\"," +
                "\"Ravensburger Points\"," +
                "\"Image1\","+
                "\"Image2\","+
                "\"Image3\","+
                "\"Image4\","+
                "\"Image5\","+
                "\"Image6\","+
                "\"Image7\","+
                "\"Image8\","+
                "\"Image9\","+
                "\"Image10\","+
                "\"Image11\","+
                "\"Image12\","+
                "\"Image13\","+
                "\"Image14\","+
                "\"Image15\","+
                "\"Image16\","+
                "\"Image17\",";
        List<String> extraHeaders = new ArrayList<>();
        Set<String> extraHeaderSet = new HashSet<>();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            List<String> list = ravens.productList();
            for (int index = 0; index < list.size() ; index++) {
                String url = list.get(index);
                BrandSpecs brandSpecs = ravens.brandSpecs(url);
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getBrand_name()) + "\",");
                String description = brandSpecs.getDescription() != null ? brandSpecs.getDescription() : "null";
                bw.write("\"" + Utils.formatForCSV(description) + "\",");
                String points = brandSpecs.getName() != null ? brandSpecs.getName() : "null";
                bw.write("\"" + Utils.formatForCSV(points) + "\",");

                int i = 0;
                //pictures 3
                List<String> images = brandSpecs.getImagesList();
                if (images != null)
                    for (String image : images) {
                        bw.write("\"" + Utils.formatForCSV(image) + "\",");
                        i++;
                        if (i == 17) break;
                    }
                while (i < 17) {
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
        Ravens ravens = new Ravens();
        String filePath = "C:\\Users\\Sowjanya\\Documents\\Ravens";
        ravens.writeFile(filePath + File.separator + "test"+".csv");


        //}
    }
}
