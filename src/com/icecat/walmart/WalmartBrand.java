package com.icecat.walmart;

import com.icecat.Scrapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class WalmartBrand extends Scrapper {
    private List<String> list(){
        List<String> list = new LinkedList<>();
        String url =  Constants.TOYS_URL;
        String html = get_html(url);
        Document document = parse_html(html);
        Element element = document.getElementById("ShopbyCategory-Expander");
        Element tag = element.getElementsByTag("ul").get(0);
        Elements tags = tag.getElementsByTag("li");
        for(Element t : tags){
            String href = t.getElementsByTag("a").get(0).attr("href");
            list.add(Constants.BASE_URL+href);
        }
        //System.out.println(list);
        return list;
    }
    private Map<String,Integer> brands() {
        Map<String,Integer> brand = new HashMap<>();
        List<String> list = list();
        for(int urls = 0 ; urls < 1; urls++) {
            String url = list.get(urls);
            String html = get_html(url);
            Document document = parse_html(html);
            String element = document.toString();
            String script = "window.__WML_REDUX_INITIAL_STATE__ =";
            int json_start = element.indexOf(script);
            try {
                if (json_start > -1) {
                    int json_end = element.indexOf("</script>", json_start);
                    String json = element.substring(json_start + script.length(), json_end - 1);
                    //System.out.println(json);
                    JSONObject object = new JSONObject(json);
                    JSONObject jsonObject = object.getJSONObject("preso");
                    JSONArray selected = jsonObject.getJSONArray("facets");
                    JSONObject three = selected.getJSONObject(3);
                    JSONArray array = three.getJSONArray("values");
                    for (int i = 0; i < array.length(); i++) {
                        String name = array.getJSONObject(i).getString("name");
                        int itemno = array.getJSONObject(i).getInt("itemCount");
                        if (name.contains(" ")) {
                            name = name.replace(" ", "+");
                        }
                        brand.put(name, itemno);
                    }

                } else {
                    System.out.println("could not find the json!");

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(brand);
        return brand;
    }



    public static void main(String[] args) {
        WalmartBrand brand = new WalmartBrand();
        brand.brands();
    }
}
