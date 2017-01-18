import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

/**
 * Created by Sowji on 05/01/2017.
 *
 *  This interface provides mandatory methods for scraping
 */

/*
    Database:
        Category:
        Subcategory:
        Company -> specs_website, ...,company,
        Product -> src product id, company, icecat_product_id
 */
public abstract class Scrapper {

    public String get_html( String site){

        String content = null;

        try {
            URL url = new URL(site);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            StringBuilder sb = new StringBuilder();
            while ((inputLine = in.readLine()) != null){
                sb.append(inputLine);
            }
            in.close();
            content = sb.toString();
        }  catch ( Exception e) {
            e.printStackTrace();
        }

        return content;
    }

    public Document parse_html(String html){
        if ( html == null ) return null;
        return Jsoup.parse(html);
    }


}
