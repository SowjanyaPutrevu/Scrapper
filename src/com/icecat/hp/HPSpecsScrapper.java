package com.icecat.hp;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.icecat.Scrapper;

/**
 * Created by Sowji on 05/01/2017.
 *
 */
public class HPSpecsScrapper  extends   Scrapper{

    //10862166
    //
    public static final String SPECS_URL = "http://www8.hp.com/us/en/products/laptops/product-detail.html?oid={data}";
    public static final String SPECS_ITEM_CONTENT_CLASS = "prog-disc-item-content";

    public static void main(String[] args) throws Exception {

        Scrapper scrapper = new HPSpecsScrapper();


        for (long id = 10505334; id <= 10721962; id++) {

            //    for (String id : ids) {


            String url = HPSpecsScrapper.SPECS_URL.replace("{data}", id+"");
            System.out.println(url);
            //System.out.println(id);
            String html = scrapper.get_html(url);

            Document document =  scrapper.parse_html(html);
            Map<String, String> map = ((HPSpecsScrapper)scrapper).get_data(document);
            if(map!=null && map.isEmpty()){
              continue ;
            }
            PrintWriter pw = new PrintWriter(new File("C:\\Users\\Sowji\\Downloads\\HP\\"+"test[" + id + "].csv"));
            for (Map.Entry<String, String> entry : map.entrySet()) {
                //System.out.println("Key: " + entry.getKey() + " Value : "  + entry.getValue() );
                //Write to csv
                pw.append("\"" + entry.getKey() + "\"");
                pw.append(',');
                pw.append("\"" + entry.getValue() + "\"");
                pw.append("");

            }
            pw.append("");
            pw.close();
        }
   // }
        //Store to db;
    }

    public Map<String, String> get_data(Document document) {

        if (document == null) return null;

        Elements contents = document.getElementsByClass(SPECS_ITEM_CONTENT_CLASS);

        Map<String, String > map = new HashMap<>();
        for(Element content: contents) {

            Elements dl = content.getElementsByTag("dl");
            for(Element dt : dl ) {
                Elements key = dt.getElementsByTag("dt");
                Elements value = dt.getElementsByTag("dd");
                if(key.size() > 0 && value.size() > 0)
                    map.put(key.first().text(), value.first().text());
            }
        }
        return map;
    }






}
