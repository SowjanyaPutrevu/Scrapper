import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Created by Sowji on 17/01/2017.
 */
public class CallawayGolfClubScrapper extends Scrapper {
    public static final String BASE_URL = "http://www.callawaygolf.com";
    public static final String CATEGORY_URL = "http://www.callawaygolf.com/golf-clubs/?sz=12&start=%data%&format=page-element";

    public static void main(String[] args) {
        Scrapper scrapper = new CallawayGolfClubScrapper();

        List<String> actualUrl = new LinkedList<>();

        for (int start = 0; start <= 72; start += 12) {
            String url = CallawayGolfClubScrapper.CATEGORY_URL.replace("%data%", start + "");
            //System.out.println(url);
            String html = scrapper.get_html(url);
            Document document = scrapper.parse_html(html);
            Elements spans = document.getElementsByTag("a");
            for (Element element : spans) {
                String names = element.attr("href");
                actualUrl.add(BASE_URL + names);
                //System.out.println(BASE_URL + names);
            }
        }
        Map<String, Set<String>> map = new HashMap<>();
        for (String brandUrl : actualUrl) {
            String brandHtml = scrapper.get_html(brandUrl);
            Document document = scrapper.parse_html(brandHtml);
            Elements metaTags = document.getElementsByTag("meta");

            String brandName = null;
            for (Element tags : metaTags) {
                if (tags.attr("itemprop").equals("name")) {
                    brandName = tags.attr("content");
                    break;
                }
            }
            //System.out.println(brandName);
            Set<String> skuList = new HashSet<>();
            for (Element element : metaTags) {
                if (element.attr("itemprop").equals("sku")) {
                    skuList.add(element.attr("content"));
                }
            }
            map.put(brandName, skuList);
            System.out.println(map.size());
        }

        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            System.out.println("Key: " + entry.getKey() + " Value : " + entry.getValue());
        }
        System.out.println(map.size());

    }
}
