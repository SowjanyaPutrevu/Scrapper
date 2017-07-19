package com.icecat.walmart;

import com.icecat.BrandSpecs;
import com.icecat.Scrapper;

import com.icecat.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;


public class Walmart extends Scrapper {
    private List<String> products() {
        List<String> productList = new LinkedList<>();
        for(int i = 1 ; i <= 1 ; i++) {
            String url = get_html(Constants.POOL_URL.replace("%data%",i+""));
            Document html = parse_html(url);
            String s = html.toString();
            String script = "window.__WML_REDUX_INITIAL_STATE__ =";
            int json_start = s.indexOf(script);
            if (json_start > -1) {
                int json_end = s.indexOf("</script>", json_start);
                String json = s.substring(json_start + script.length(), json_end - 1);
                JSONObject object = new JSONObject(json);
                JSONObject jsonObject = object.getJSONObject("preso");
                JSONArray items = jsonObject.getJSONArray("items");
                for (int index = 0; index < items.length(); index++) {
                    JSONObject item = items.getJSONObject(index);
                    String product = item.getString("productPageUrl");
                    productList.add(Constants.BASE_URL + product);
                }
            } else {
                System.out.println("could not find the json!");
            }
        }
       // System.out.println(productList);
            return productList;

    }
    private String title(Document document){
        String text = document.getElementsByClass("prod-ProductTitle").text();
        //System.out.println(text);
        return text;
    }
    private String brandName(Document document){
        String text = document.getElementsByClass("prod-BrandName").text();
        //System.out.println(text);
        return text;
    }
    private String walmartNo(Document document){
        String number = document.getElementsByClass("wm-item-number").text();
        number = number.replace("Walmart #: ","");
        //System.out.println(number);
        return number;
    }
    private String isbn(Document document){
        String isbn = document.getElementsByTag("span").attr("content");
        //System.out.println(isbn);
        return isbn;
    }
    private String manufaturerNO(Document document){
        String product = null;
        String s = document.toString();
        String script = "window.__WML_REDUX_INITIAL_STATE__ =";
        int json_start = s.indexOf(script);
        try {
            if (json_start > -1) {
                int json_end = s.indexOf("</script>", json_start);
                String json = s.substring(json_start + script.length(), json_end - 3);
                // System.out.println( json);
                JSONObject object = new JSONObject(json);
                JSONObject jsonObject = object.getJSONObject("product");
                JSONObject selected = jsonObject.getJSONObject("selected");
                String select = selected.getString("product");
                JSONObject items = jsonObject.getJSONObject("products");
                JSONObject item = items.getJSONObject(select);
                JSONObject attr = item.getJSONObject("productAttributes");

                product = attr.getString("manufacturerProductId");
                //productList.add(Constants.BASE_URL+product);
                //System.out.println(product);
            } else {
                System.out.println("could not find the json!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return product;
    }

    private BrandSpecs brandSpecs(String url){
        BrandSpecs brandSpecs = new BrandSpecs();
        Document brandDocument = parse_html(get_html(url));
        String title = title(brandDocument);
        brandSpecs.setName(title);

        String brandName = brandName(brandDocument);
        brandSpecs.setBrand_name(brandName);

        String walmartNo = walmartNo(brandDocument);
        brandSpecs.setCode(walmartNo);

        String isbn = isbn(brandDocument);
        brandSpecs.setIsbn(isbn);

        String manufacturerNo = manufaturerNO(brandDocument);
        brandSpecs.setCategory(manufacturerNo);

        System.out.println(brandSpecs);
        return brandSpecs;
    }

    private void writeFile(String filePath){
        Walmart walmart = new Walmart();
        String headers =    "\"Title\","+
                            "\"BrandName\","+
                            "\"Walmart No:\","+
                            "\"ISBN\","+
                            "\"Manufacturer NO:\",";

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            bw.write(headers);
            bw.newLine();

            List<String> list = walmart.products();
           for (int index = 26 ; index <= 39 ; index++) {
                String url = list.get(index);
                BrandSpecs brandSpecs = walmart.brandSpecs(url);
                String name = brandSpecs.getName() != null ? brandSpecs.getName() : "null";
                bw.write("\"" + Utils.formatForCSV(name) + "\",");
                String brandName = brandSpecs.getBrand_name() != null ? brandSpecs.getBrand_name() : "null";
                bw.write("\"" + Utils.formatForCSV(brandName) + "\",");
                String code = brandSpecs.getCode() != null ? brandSpecs.getCode() : "null";
                bw.write("\"" + Utils.formatForCSV(code) + "\",");
                String isbn = brandSpecs.getIsbn() != null ? brandSpecs.getIsbn() : "null";
                bw.write("\"" + Utils.formatForCSV(isbn) + "\",");
                String manufacturer = brandSpecs.getCategory() != null ? brandSpecs.getCategory() : "null";
                bw.write("\"" + Utils.formatForCSV(manufacturer) + "\",");

                bw.newLine();

            }
            bw.flush();
            bw.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Walmart walmart = new Walmart();
        String filePath = "C:\\Users\\Sowjanya\\Documents\\Walmart";
        walmart.writeFile(filePath + File.separator + "action2"+".csv");
        walmart.products();
    }
}
