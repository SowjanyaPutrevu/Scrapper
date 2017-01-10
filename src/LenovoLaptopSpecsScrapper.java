import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sowji on 10/01/2017.
 */
public class LenovoLaptopSpecsScrapper extends Scrapper {

    public String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    Map<String, List<String>> get_data(Document document) {
        Map<String,List<String>> data = new HashMap<>();
        Elements techSpecs = document.getElementsByClass("techSpecs-table");
        for(Element specs : techSpecs) {
            Elements rows = specs.getElementsByTag("tr");
            for(Element row : rows){
                Elements td = row.getElementsByTag("td");
                if ( td.size() > 0) {
                    Element td1 = td.get(0);
                    Element td2 = td.get(1);
                    //System.out.println(td2.text());
                    String key = td1.text();
                    List<String> values = new LinkedList<>();
                    Elements list = td2.getElementsByTag("li");
                    for(Element l : list){
                        String txt = l.text();
                        values.add(txt);
                    }
                    data.put(key,values);
                }
            }
        }

        return data;
    }
}
