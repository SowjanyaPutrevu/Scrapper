package com.icecat.amazon;

import com.icecat.Scrapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.LinkedList;
import java.util.List;

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

    public static void main(String[] args) {
        Amazon amazon = new Amazon();
       amazon.url();
    }
}
