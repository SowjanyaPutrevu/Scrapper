package com.icecat;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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

    public static String cookies;

    public String get_html( String site){

        String content = null;

        try {
            URL obj = new URL(site);

            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setReadTimeout(5000);
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            if (cookies!=null && !cookies.isEmpty()) conn.setRequestProperty("Cookie", cookies);

            System.out.println("Request URL ... " + site);


            boolean redirect = false;

            // normally, 3xx is redirect
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }
            if (cookies!=null && !cookies.isEmpty()) cookies += ";" + conn.getHeaderField("Set-Cookie");
            else cookies = conn.getHeaderField("Set-Cookie");

            System.out.println("Response Code ... " + status);

            while (redirect) {

                // get redirect url from "location" header field
                String newUrl = conn.getHeaderField("Location");

                // get the cookie if need, for login
                cookies = conn.getHeaderField("Set-Cookie");

                // open the new connnection again
                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                conn.setRequestProperty("Cookie", cookies);
                conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
                //conn.addRequestProperty("Referer", "google.com");

                System.out.println("Redirect to URL : " + newUrl);

                status = conn.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK) {
                    if (status == HttpURLConnection.HTTP_MOVED_TEMP
                            || status == HttpURLConnection.HTTP_MOVED_PERM
                            || status == HttpURLConnection.HTTP_SEE_OTHER)
                        redirect = true;
                }

            }


            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
