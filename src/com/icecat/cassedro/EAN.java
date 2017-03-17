package com.icecat.cassedro;

import com.icecat.BrandSpecs;
import com.icecat.Scrapper;
import com.icecat.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

/**
 * Created by Sowjanya on 3/14/2017.
 */
public class EAN extends Scrapper {

    private List<String> getEnglishBooks(){
        List<String> list = new ArrayList<>();
        for(int i = 0; i < Constants.eans.length; i++) {
            String url = Constants.SEARCH_URL.replace("%data%",Constants.eans[i]+"");
            String html = get_html(url);
            Document document = parse_html(html);
            Element h2 = document.getElementsByClass("title-link").get(0);
            Elements tags = h2.getElementsByTag("a");
            for(Element tag : tags) {
                String href = tag.attr("href");
                //System.out.println(href);
                list.add(Constants.BASE_URL+href);
            }
            if( i%10 == 0 || i == Constants.eans.length - 1  )
                System.out.println("Fetched url " + i);
        }

        //System.out.println(list);
        System.out.println(list.size());
        return list;
    }

    private String getTitle(Document document){
        String[] titleText = null;
        Elements elements = document.getElementsByTag("h1");
        for(Element element : elements) {
            String title = (element.text());
            titleText = title.split("(En papel)");
            //System.out.println(title);
        }      // return titleText[0].replace("(", " ");
        return  titleText[0].replace("(", " ");
    }

    private String getDescription(Document document) {
        /*for(int i = 0; i < getEnglishBooks().size(); i++ ){
            String url = getEnglishBooks().get(i);
            String html = get_html(url);
            Document document = parse_html(html);*/

        Elements elements = document.getElementsByClass("col02");
        if(elements.size() > 0) {
            String text = elements.get(0).text();
            if(text.contains("Resumen del libro")) {
                String[] desc = text.split("Resumen del libro");
                desc[0] = "Resumen del libro ";
                String description = desc[1];
                return description;
            }else{

                return text;
            }

        }else{
            return "null";
        }
    }

    //System.out.println(description);
    //}


    private Map<String,String> details(Document document){
        Map<String,String> details = new HashMap<>();
        Element element = document.getElementsByClass("list07").get(1);
        Elements tags = element.getElementsByTag("li");
        int unknown = 0;
        for(Element tag : tags) {
       /* Elements elements = element.getElementsByClass("first-child");
        for(Element element1 : elements) {
           String text =  element1.text();*/
            //String[] map = text.split(":");
            String text = tag.text();
            String[] map = text.split(":");
            if( map.length > 1) {
                details.put(map[0], map[1]);
            } else {
                details.put("unknown"+unknown, map[0]);
                unknown++;
            }
        }
        //}
        //System.out.println(details);
        return details;
    }
    private String getAuthor(Document document){
        String author = document.getElementsByClass("book-header-2-subtitle-author").get(0).text();
        //System.out.println(author);
        return author;
    }
    private String getAuthorDetails(Document document){
        String author = document.getElementsByClass("book-header-2-subtitle").get(0).getElementsByTag("a").attr("href");
        if(author != null && !author.equals("")) {
            //System.out.println(Constants.BASE_URL+author);
            return Constants.BASE_URL + author;
        }else
            return null;
    }
    private String getAuthorDesc(Document document){
        String url = getAuthorDetails(document);
        if(url != null) {
            String html = get_html(url);
            Document document1 = parse_html(html);
            Elements ps = document1.getElementsByTag("p");
            String text = null;
            for(Element p : ps) {
                if(p.hasClass("mt5")){
                    text = p.text();
                    break;
                }
            }
            //System.out.println(text);
            return text;
        }else
            return "null";
    }
    private String getPublisher(Document document){
        Elements elements = document.getElementsByClass("book-header-2-subtitle-publisher");
        if(elements.size() > 0) {
            String publisher = elements.get(0).text();
            return publisher;
        }else{
            return "null";
        }
        //System.out.println(publisher);

    }
    private List<String> getImageList(Document document){
        List<String> images = new ArrayList<>();
        String img = document.getElementsByClass("img-shadow").get(0).attr("src");
        if(!img.contains("defect")) {
            images.add(img);
            //System.out.println(images);
            return images;
        }else{
            return null;
        }
    }

    public static void writeFile( String filePath){
        EAN books = new EAN();

        String headers = "\"Title\"," +
                "\"Author\"," +
                "\"Author Description\"," +
                "\"Publisher\"," +
                "\"Description\"," +
                "\"Image1\"," +
                "\"Image2\"," +
                "\"Image3\"," ;

        List<String> extraHeaders = new ArrayList<>();
        Set<String> extraHeaderSet = new HashSet<>();

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));

            List<String> bookList = books.getEnglishBooks();
            for(int index = 0 ; index <  bookList.size() ;index++) {
                String url = bookList.get(index);
                //String url = "https://www.casadellibro.com/libro-un-ano-entre-los-persas/mkt0003066624/4306635";
                BrandSpecs brandSpecs = books.brandSpecs(url);
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getName()) + "\",");
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getBrand_name()) + "\",");
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
    private BrandSpecs brandSpecs(String url){
        BrandSpecs brandSpecs = new BrandSpecs();
        Document brandDocument =parse_html(get_html(url));

        String title = getTitle(brandDocument);
        if(title != null )
            brandSpecs.setName(title);

        List<String> images = getImageList(brandDocument);
        if(images != null)
            brandSpecs.setImagesList(images);

        String desc = getDescription(brandDocument);
        if(desc != null)
            brandSpecs.setDescription(desc);

        Map<String, String> details = details(brandDocument);
        if(details!=null)
            brandSpecs.setDetails(details);

        String author = getAuthor(brandDocument);
        if(author != null)
            brandSpecs.setBrand_name(author);

        String authorDesc = getAuthorDesc(brandDocument);
        if(authorDesc != null)
            brandSpecs.setAuthorDesc(authorDesc);

        String publisher = getPublisher(brandDocument);
        if(publisher != null)
            brandSpecs.setPublisher(publisher);

        System.out.println(brandSpecs);
        return brandSpecs;
    }

    public static void main(String[] args) {
        EAN ean = new EAN();
       String filePath = "C:\\Users\\Sowjanya\\Documents\\casadellibro";
        // books.brandSpecs(url);
        ean.writeFile(filePath + File.separator + "taskbooks"+".csv");
        //ean.getEnglishBooks();
    }
}
