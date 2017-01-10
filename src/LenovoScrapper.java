import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sowji on 10/01/2017.
 */
public class LenovoScrapper extends Scrapper {

    public static final String SPECS_URL = "http://shop.lenovo.com//nl//nl";
    public static final String SUBMENU_CLASS = "products_submenu";
    public static final String LISTBOX_CLASS = "list_box";

    public static void main(String[] args) throws Exception {
        Scrapper scrapper = new LenovoScrapper() ;
        String html = scrapper.get_html(SPECS_URL);
        Document document = scrapper.parse_html(html);
        Elements products = document.getElementsByClass(SUBMENU_CLASS);
        String urls[] = new String[products.size()];
        int x=0;
        for(Element element : products){
            String url =element.attr("href");
            urls[x]=url;
            x++;
        }

        /*System.out.println("Number of urls found: "+x);
        List<String> productUrls = new ArrayList<>();
        for(String url : urls){
            String fullUrl = "http:"+url;
            html = scrapper.get_html(fullUrl);
            document = scrapper.parse_html(html);
            System.out.println(html);
            if(true) break;
            Elements productsListboxDiv = document.getElementsByClass(LISTBOX_CLASS);
            System.out.println(productsListboxDiv.size());
            for(Element div : productsListboxDiv){
               Elements aList = div.getElementsByTag("a");
               for(Element a : aList ){
                   String href = a.attr("href");
                   productUrls.add(href);
               }
            }
        }
        System.out.println(productUrls.size());*/
    }



}
