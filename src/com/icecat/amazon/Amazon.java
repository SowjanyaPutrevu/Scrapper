package com.icecat.amazon;

import com.icecat.BrandSpecs;
import com.icecat.Scrapper;
import com.icecat.Utils;
import com.icecat.myPrep.BalancedParanthesis;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

/**
 * Created by Sowjanya on 3/17/2017.
 */
public class Amazon extends Scrapper {
    private List<String> url(){
        List<String> pUrl = new LinkedList<>();
        String[] eans = Constants.EANS;
        String url = Constants.SEARCH_URL;
        for(int i = 0 ; i <eans.length; i++ ){
            String surl = url.replace("%data%",eans[i]+"");
            String html = get_html(surl);
            Document document = parse_html(html);
            if(document.getElementById("result_0") != null){
                Element element = document.getElementById("result_0");
                String href = element.getElementsByTag("a").get(0).attr("href");
                pUrl.add(href);
            }
        }
        System.out.println(pUrl);
        return pUrl;
    }
    private String title(Document document){
            Element title = document.getElementById("productTitle");
            String text = title.text();
            System.out.println(text);
        return text;
    }
    private String author(Document document){
        String author = document.getElementById("byline").text();
        return author;
    }
    private List<String> images(Document document){
        List<String> imgs = new LinkedList<>();
        Elements elements = document.getElementsByClass("a-button-text");
        for(Element element : elements){
            Elements tags = element.getElementsByTag("img");
            for(Element tag : tags){
                String src = tag.attr("src");
                src = src.replace("_SS40_.","_SS450_.");
                imgs.add(src);
            }

        }
        return imgs;
    }
    private String description(Document document){
        if(document.getElementById("productDescription") != null) {
            String text = document.getElementById("productDescription").text();
            return text;
        }else{
            return null;
        }
    }
    private Map<String,String> details(Document document){
        Map<String,String> details= new HashMap<>();
        Elements elements = document.getElementsByClass("content");
        for(Element element : elements){
            Elements tags = element.getElementsByTag("li");
            for(Element tag : tags) {
                String text = tag.text();
                int i = 0;
                if (text.contains(":")) {
                    String[] features = text.split(":");
                    details.put(features[0], features[1]);
                } else {
                    details.put("unknown"+i,text);
                    i++;
                }
            }
        }
        return details;
    }
    private BrandSpecs brandSpecs(String url){
        BrandSpecs brandSpecs = new BrandSpecs();
        Document document = parse_html(get_html(url));

        String title = title(document);
        brandSpecs.setBrand_name(title);

        String author = author(document);
        brandSpecs.setName(author);

        String description = description(document);
        brandSpecs.setDescription(description);

        List<String> images = images(document);
        brandSpecs.setImagesList(images);

        Map<String,String> details = details(document);
        brandSpecs.setDetails(details);

        System.out.println(brandSpecs);
        return brandSpecs;
    }
    // Title
    //Author
    //Description
    //Images
    //details
    public void writeFile(String filePath) {
        Amazon amazon = new Amazon();
        String headers = "\"Title\"," +
                "\"Author\"," +
                "\"Author Description\"," +
                "\"Publisher\"," +
                "\"Description\"," +
                "\"Image1\"," +
                "\"Image2\"," +
                "\"Image3\",";
        List<String> extraHeaders = new ArrayList<>();
        Set<String> extraHeaderSet = new HashSet<>();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));

            List<String> bookList = amazon.url();
            for (int index = 0; index < bookList.size() ; index++) {
                String url = bookList.get(index);
                //String url = "https://www.casadellibro.com/libro-un-ano-entre-los-persas/mkt0003066624/4306635";
                BrandSpecs brandSpecs = amazon.brandSpecs(url);
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getBrand_name()) + "\",");
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getName()) + "\",");
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getAuthorDesc()) + "\",");
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getPublisher()) + "\",");
                String description = brandSpecs.getDescription() != null ? brandSpecs.getDescription() : "null";
                bw.write("\"" + Utils.formatForCSV(description) + "\",");
                int i = 0;
                //pictures 3
                List<String> images = brandSpecs.getImagesList();
                if (images != null)
                    for (String image : images) {
                        bw.write("\"" + Utils.formatForCSV(image) + "\",");
                        i++;
                        if (i == 3) break;
                    }
                while (i < 3) {
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
        Amazon amazon = new Amazon();
        String filePath = "C:\\Users\\Sowjanya\\Documents\\Books";
        // books.brandSpecs(url);
        amazon.writeFile(filePath + File.separator + "cini-1"+".csv");
        //ean.getEnglishBooks();
        }
    }

