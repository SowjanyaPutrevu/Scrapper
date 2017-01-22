package com.icecat.callaway;
import java.util.*;

import com.icecat.*;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Sowji on 17/01/2017.
 * Main function of this class is to fetch all brands and SKUs
 */
public class CallawayGolfClubScrapper extends Scrapper {

    private List<String> getBrandUrls(){
        List<String> brandUrls = new ArrayList<>();
        for (int start = 0; ; start += 12) {
            String url = Constants.CATEGORY_URL.replace("%data%", start + "");
            String html = get_html(url);
            Document document = parse_html(html);
            Elements spans = document.getElementsByTag("a");

            if(spans.size() == 0) break;

            for (Element element : spans) {
                String names = element.attr("href");
                brandUrls.add(Constants.BASE_URL + names);
            }
        }
        return brandUrls;
    }

    private List<String> getBrandImages(Document document) {
        Elements images = document.getElementsByTag("img");
        return Utils.getImageUrls(images);
    }

    public BrandSpecs getBrandSpecs(String brandUrl){
        String brandHtml = get_html(brandUrl);
        Document document = parse_html(brandHtml);
        return getBrandSpecs(document);
    }

    private BrandSpecs getBrandSpecs(Document brandDocument){
        BrandSpecs brandSpecs = new BrandSpecs();
        List<String> images = getBrandImages(brandDocument);

        if(images!=null && !images.isEmpty()){
            brandSpecs.setImagesList(images);
        }

        Elements titles = brandDocument.getElementsByClass(Constants.BRAND_SPECS_TITLE_CLASS);
        boolean secondGenderPresent = titles.size() > 1;
        Element firstGender = titles.first();

        if(firstGender == null) return brandSpecs;

        Element secondGender = null;
        if( secondGenderPresent ) {
            secondGender = titles.get(1);
        }

        Elements specs = firstGender.siblingElements();
        Map<String, List<Specification>> generalSpecs = new HashMap<>();
        Map<String, List<Specification>> modelSpecs = new HashMap<>();

        String key = firstGender.text();
        String model = "";
        boolean isModel = false;

        for(Element element : specs) {
                if(secondGenderPresent && secondGender == element){
                    key = secondGender.text();
                    isModel = false;
                } else if( element.hasClass( Constants.BRAND_SHAFT_SPECS_CLASS) ) {
                    model =  element.text();
                    isModel = true;
                } else if( element.hasClass("table-responsive") ) {
                    List<List<Specification>> tableData = parseTable(element);
                    for(List<Specification> row : tableData){
                        if( !isModel ) {
                            String k = key+ "_" + row.get(0).getValues();
                            generalSpecs.put(k, row);
                        } else {
                            String k = key + "_" + model + "_" + row.get(1).getValues();
                            modelSpecs.put(k, row);
                        }
                    }
                }
        }

        brandSpecs.setGeneralSpecs(generalSpecs);
        brandSpecs.setModelSpecs(modelSpecs);

        return brandSpecs;
    }

    private List<List<Specification>> parseTable( Element element ) {

        Elements tables = element.getElementsByTag("table");
        Element toi =  tables.last();
        return Utils.getTableData(toi);

    }

    public Set<String> getSKUs(String brandUrl) {
        String brandHtml = get_html(brandUrl);
        Document document = parse_html(brandHtml);
        return getSKUs(document);
    }

    private Set<String> getSKUs(Document brandDocument) {
        Elements metaTags = brandDocument.getElementsByTag("meta");

        Set<String> skuList = new HashSet<>();
        for (Element element : metaTags) {
            if (element.attr("itemprop").equals("sku")) {
                skuList.add(element.attr("content"));
            }
        }

        return skuList;
    }

    public List<ProductSpecs> getProducts(String brandUrl){
        String html = get_html(brandUrl);
        System.out.println(html);
        Document document = parse_html(html);
        return getProducts(document);
    }

    public List<ProductSpecs> getProducts(Document brandDocument){
        List<ProductSpecs> products = new ArrayList<>();
        Element form = brandDocument.getElementById(Constants.BRAND_PRODUCT_CONFIG_FORM_ID);
        String url = form.attr("action");
        Elements inputs = form.getElementsByTag("input");
        for(Element input : inputs) {
            String name = input.attr("name");
            String value = input.attr("value");
            url += "&"+name+"="+value;
        }
        String json = get_html(url);
        JSONObject jsonObject = new JSONObject(json);
        Map<String, List<Specification>> attributes = new HashMap<>();
        Map<String, ProductSpecs> productsMap = new HashMap<>();

        JSONArray jsonArray = jsonObject.getJSONArray("attributes");
        for( int i=0; i < jsonArray.length(); i++ ){
            JSONObject object =(JSONObject)jsonArray.get(i);
            String key = (String)object.get("name");
            JSONArray values = (JSONArray)object.get("values");
            List<Specification> specificationList = new ArrayList<>();
            for(Object obj : values) {
                JSONObject value = (JSONObject) obj;
                Specification specification = new Specification();
                specification.setName(key);
                specification.setValues((String)value.get("displayValue"));
                specification.setSourceAttributeId((String)object.get("id"));
                specification.setSourceAttributeValue((String)value.get("id"));
                specificationList.add(specification);
            }
            attributes.put(key, specificationList);
        }

        jsonArray = jsonObject.getJSONArray("options");
        for( int i=0; i < jsonArray.length(); i++ ){
            JSONObject object =(JSONObject)jsonArray.get(i);
            String key = (String)object.get("name");
            JSONArray values = (JSONArray)object.get("values");
            List<Specification> specificationList = new ArrayList<>();
            for(Object obj : values) {
                JSONObject value = (JSONObject) obj;
                Specification specification = new Specification();
                specification.setName(key);
                specification.setValues((String)value.get("displayValue"));
                specification.setSourceAttributeId((String)object.get("id"));
                specification.setSourceAttributeValue((String)value.get("id"));
                specification.setBrand((String)value.get("brand"));
                specification.setModel((String)value.get("model"));
                specificationList.add(specification);
            }
            attributes.put(key, specificationList);
        }

        jsonArray = jsonObject.getJSONArray("shaft");
        for( int i=0; i < jsonArray.length(); i++ ){
            JSONObject object =(JSONObject)jsonArray.get(i);
            String key = (String)object.get("name");
            JSONArray values = (JSONArray)object.get("values");
            List<Specification> specificationList = new ArrayList<>();
            for(Object obj : values) {
                JSONObject value = (JSONObject) obj;
                Specification specification = new Specification();
                specification.setName(key);
                specification.setValues((String)value.get("displayValue"));
                specification.setSourceAttributeId((String)object.get("id"));
                specification.setSourceAttributeValue((String)value.get("id"));
                specification.setBrand((String)value.get("brand"));
                specification.setModel((String)value.get("model"));
                specificationList.add(specification);
            }
            attributes.put(key, specificationList);
        }


        return products;
    }

    /*
    Apparently, the easiest way to get item specs is by adding it to the cart!
     */
    private ProductSpecs getProductSpecsFromCart(Document productDocument){
        ProductSpecs productSpecs = new ProductSpecs();


        Element cartItem = productDocument.getElementsByClass(Constants.CART_0_ITEM_CLASS).first();
        if (cartItem == null) return productSpecs;

        Elements images = cartItem.getElementsByClass(Constants.CART_ITEM_IMAGE_CLASS);
        List<String> imageUrls = Utils.getImageUrls(images.first().getElementsByTag("img"));
        productSpecs.setImagesList(imageUrls);

        Element specs = cartItem.getElementsByClass(Constants.CART_ITEM_CLASS).first();
        Elements uls = specs.getElementsByTag("ul");

        List<Specification> specsList = new ArrayList<>();

        for(Element ul : uls) {
            for(Element li : ul.children()) {
                Elements spans = li.children();
                Specification spec = new Specification();
                spec.setName(spans.get(0).text());
                spec.setValues(spans.get(1).text());
                specsList.add(spec);
            }
        }

        Element price = cartItem.getElementsByClass(Constants.CART_PRICE_SUBTOTAL).first();
        productSpecs.setPrice(price.text());

        productSpecs.setSpecifications(specsList);

        return productSpecs;
    }

    public static void main(String[] args) {
        CallawayGolfClubScrapper scrapper = new CallawayGolfClubScrapper();
        //http://www.callawaygolf.com/golf-clubs/mens/drivers/drivers-great-big-bertha-epic-2017.html
        //http://www.callawaygolf.com/on/demandware.store/Sites-CG-Site/en_US/ProductConfigurator-FilteredAttributes?format=json&pid=drivers-great-big-bertha-epic-2017&vid=drivers-great-big-bertha-epic-2017&cgid=drivers&qty=1&condition=BNW&a1509=6340&a44=69&a71=5711&option_2-CEX-166bp9472=14985&option_1661-CEX-166bp9472=7496&option_3-CEX-166bp9472=6098&option_54-CEX-166bp9472=14978%7C98%7C100720%7C5692%7C5693
        String brandUrl = "http://www.callawaygolf.com/on/demandware.store/Sites-CG-Site/en_US/ProductSpecs-Get?productCode=drivers-great-big-bertha-epic-2017";
        BrandSpecs brandSpecs =
                scrapper.getBrandSpecs("http://www.callawaygolf.com/on/demandware.store/Sites-CG-Site/en_US/ProductSpecs-Get?productCode=drivers-great-big-bertha-epic-2017");
        System.out.println(Scrapper.cookies);
        List<ProductSpecs> productSpecs = scrapper.getProducts(brandUrl);
        System.out.println(Scrapper.cookies);
        System.out.println(productSpecs);
    }
}
