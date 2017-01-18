import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sowji on 17/01/2017.
 */
public class CallawayClubSpecsScrapper extends Scrapper {
    public static final String PRODUCT_URL = "http://www.callawaygolf.com/on/demandware.store/Sites-CG-Site/en_US/ProductSpecs-Get?productCode=hybrids-2015-apex&amp;locale=en-US";
    public static final String TABLE_CLASS_NAME = "table responsive";
    public static final String CLASS_NAME = "table table-striped";

    public static void main(String[] args) throws Exception {
        Scrapper scrapper = new CallawayGolfClubScrapper();
        String html = scrapper.get_html(PRODUCT_URL);
        Document document = scrapper.parse_html(html);
        //Elements tables = document.getElementsByClass(TABLE_CLASS_NAME);
        //for(Element table : tables ) {
            Elements techSpecs = document.getElementsByClass(CLASS_NAME);
            for (Element specs : techSpecs) {
                Elements rows = specs.getElementsByTag("tr");
                for (Element row : rows) {
                    Elements th = row.getElementsByTag("th");
                    if (th.size() > 0) {
                        for (Element header : th) {
                            String theader = header.text();
                            System.out.print(theader + "\t");
                        }
                    }
                    System.out.println();
                    Elements td = row.getElementsByTag("td");
                    for (Element value : td) {
                        String tvalue = value.text();
                        System.out.print(tvalue + "\t\t");
                        //System.out.println();
                    }
                }
            //}
        }
    }
}





