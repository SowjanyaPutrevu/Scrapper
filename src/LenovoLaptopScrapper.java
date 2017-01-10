import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static javax.management.Query.attr;

/**
 * Created by Sowji on 10/01/2017.
 */
public class LenovoLaptopScrapper extends Scrapper {

    public static final String BASE_URL = "http://shop.lenovo.com";
    public static final String SPECS_URL = "http://shop.lenovo.com/nl/nl/laptops/?menu-id=laptops_en_ultrabooks";
    public static final String BRANDNAME_CLASS = "mobileBrandName";
    public static final String BRAND_URL = "http://shop.lenovo.com/SEUILibrary/controller/e/nlweb/LenovoPortal/nl_NL/wci.workflow:load?page=/WW/wci3/emea_shared/nl/common/splitter-v5/%data%.html";
//http://shop.lenovo.com/SEUILibrary/controller/e/nlweb/LenovoPortal/nl_NL/wci.workflow:load?page=/WW/wci3/emea_shared/nl/common/splitter-v5/thinkpad.html
    public static void main(String[] args) throws Exception {
        Scrapper scrapper = new LenovoLaptopScrapper();
        String html = scrapper.get_html(SPECS_URL);
        Document document = scrapper.parse_html(html);
        Elements spans = document.getElementsByClass(BRANDNAME_CLASS);
        String[] brandNames = new String[spans.size()];
        int x = 0;
        for (Element element : spans) {
            brandNames[x] = element.text();
            x++;
        }
        List<String> productUrl = new LinkedList<>();
        for (String name : brandNames) {
            String url = BRAND_URL.replace("%data%", name);
            html = scrapper.get_html(url);
            System.out.println(html);
            document = scrapper.parse_html(html);
            Elements anchors = document.getElementsByTag("a");
            for (Element e : anchors) {
                String href = e.attr("href");
                productUrl.add(BASE_URL + href);
            }
        }
        LenovoLaptopSpecsScrapper newscrapper = new LenovoLaptopSpecsScrapper();
       // System.out.println(productUrl.get(3));
        System.out.println("No of Product urls: " + productUrl.size());
        for (String i : productUrl) {
            System.out.println(i);
            html = scrapper.get_html(i);
            document = scrapper.parse_html(html);
            Map<String,List<String>> map =newscrapper.get_data(document);
            PrintWriter pw = new PrintWriter(new File("C:\\Users\\Sowji\\Documents\\LenovoLaptops\\test"+ URLEncoder.encode(i, "UTF-8")+".csv"));

            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                //System.out.println("Key: " + entry.getKey() + " Value : "  + entry.getValue() );
                //Write to csv


                List<String> list = entry.getValue();
                for(String s : list) {
                    pw.append("\"" + entry.getKey() + "\"");
                    pw.append(',');
                    pw.append("\"" + s.replaceAll("\"","") + "\"");
                    pw.append("\n");
                }


            }
            pw.append("\n");
            pw.close();
        }
        }
    }


