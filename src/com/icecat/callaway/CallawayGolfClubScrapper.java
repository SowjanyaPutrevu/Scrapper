package com.icecat.callaway;
import java.util.*;

import com.icecat.*;
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

    public Set<ProductSpecs> getProducts(String brandUrl){
        String html = get_html(brandUrl);
        System.out.println(html);
        Document document = parse_html(html);
        return getProducts(document);
    }

    private void fetchProduct(int step, Map<String, List<Specification>> attributes, Map<String, List<Specification>> options,
                              Map<String, List<Specification>> shafts, int attributeValueIndex, Set<ProductSpecs> products,
                              String baseUrl, List<Specification> specList){
        //Here step indicates current attribute, we are changing with the attributeValueIndex.
        //So, we will increment the steps until we reach 7, where we add products. -- These are standard products
        //Also ignoring options at this point
        //Next we change the options
        baseUrl += "&" + attributes.get(step+"").get(attributeValueIndex).getSourceAttributeId() + "="
                + attributes.get(step+"").get(attributeValueIndex).getSourceAttributeValue();
        String html = get_html(baseUrl);
        JSONObject object = new JSONObject(html);
        attributes = parseJson( object.getJSONArray("attributes"), 1 );
        options  = parseJson( object.getJSONArray("options"), 0 );
        List<Specification> newList = new ArrayList<>(specList);
        if(step == 7 ) {
            List<Specification> values = attributes.get(step+"");
            for(Specification spec : values){
                ProductSpecs product = new ProductSpecs();
                newList.add(spec);
                product.setSpecifications(newList);
                product.setPrice(spec.getAttributes().get("variantPrice"));
                product.setSourceId(spec.getProductId());
                products.add(product);
            }
        } else {
            for( int i=0; i< attributes.get(""+ (step+1)).size(); i++ ) {
                newList.add(attributes.get((step+1)+"").get(i));
                fetchProduct(step+1, attributes, options, shafts, i, products, baseUrl, newList );
            }
        }
    }

    public Set<ProductSpecs> getProducts(Document brandDocument){
        Set<ProductSpecs> products = new HashSet<>();

        //http://www.callawaygolf.com/on/demandware.store/Sites-CG-Site/en_US/ProductConfigurator-FilteredAttributes?format=json&pid=drivers-great-big-bertha-epic-2017&vid=drivers-great-big-bertha-epic-2017&cgid=drivers&qty=1&condition=BNW
        //http://www.callawaygolf.com/on/demandware.store/Sites-CG-Site/en_US/ProductConfigurator-Process?&format=json&pid=drivers-great-big-bertha-epic-2017&vid=drivers-great-big-bertha-epic-2017&cgid=drivers&qty=1&condition=BNW
        Element form = brandDocument.getElementById(Constants.BRAND_PRODUCT_CONFIG_FORM_ID);
        String action = Constants.FILTER_ATTRIBUTES_URL;
        String params = "?";
        Elements inputs = form.getElementsByTag("input");
        Map<String, String> initAttributes = new HashMap<>();

        for(Element input : inputs) {
            String name = input.attr("name");
            String value = input.attr("value");
            if(value!=null && !value.isEmpty()) {
                params += "&" + name + "=" + value;
                initAttributes.put(name, "&" + name + "=" + value);
            }
        }

        String json = get_html(action + params);

        JSONObject jsonObject = new JSONObject(json);

        Map<String, List<Specification>> attributes;
        Map<String, List<Specification>> options;
        Map<String, List<Specification>> shafts;

        JSONArray jsonArray = jsonObject.getJSONArray("attributes");
        attributes = parseJson(jsonArray, 1);

        jsonArray = jsonObject.getJSONArray("options");
        options = parseJson(jsonArray, 0);

        //jsonArray = jsonObject.get("shafts");
        //shafts = parseJson(jsonArray, 0);
        shafts = new HashMap<>();

        //From here, what I need is a minor change in the attributes, which lead to new json, one step at a time
        //There are 7 attrributes, which can be measured as steps! Options come in the end.

        int step = 0;
        String baseUrl = action + "?" + initAttributes.get("pid") + initAttributes.get("vid") + initAttributes.get("qty")
                + initAttributes.get("cgid") + initAttributes.get("format") + initAttributes.get("condition") ;
        for(int i=0; i<attributes.get(step+"").size(); i++) {
            List<Specification> specList = new ArrayList<>();
            specList.add(attributes.get((step)+"").get(i));
            fetchProduct(step, attributes, options, shafts, i, products, baseUrl, specList);
        }

        return products;
    }


    public Map<String, List<Specification>> parseJson(JSONArray jsonArray, int setIndexAsKey) {
        Map<String, List<Specification>> map = new HashMap<>();

        for( int i=0; i < jsonArray.length(); i++ ){
            JSONObject object =(JSONObject)jsonArray.get(i);
            String key = object.has("name") ? object.getString("name") : object.has("displayName") ? object.getString("displayName") : "" ;
            JSONArray values = (JSONArray)object.get("values");
            List<Specification> specificationList = new ArrayList<>();
            for(Object obj : values) {
                JSONObject value = (JSONObject) obj;
                Specification specification = new Specification();
                specification.setName(key);
                specification.setValues(value.getString("displayValue"));
                specification.setSourceAttributeId(object.getString("id"));
                specification.setSourceAttributeValue(value.getString("id"));
                if(value.has("sku")) specification.setProductId(value.getString("sku"));
                if(value.has("brand")) specification.setBrand(value.getString("brand"));
                if(value.has("model")) specification.setModel(value.getString("model"));

                Map<String, String> additional = new HashMap<>();
                if(value.has("price")) additional.put("price", String.valueOf(value.get("price")));
                if(value.has("variantPrice")) additional.put("variantPrice", String.valueOf( value.get("variantPrice") ));
                specification.setAttributes(additional);

                specificationList.add(specification);
            }
            key = setIndexAsKey == 1 ? i+"" : key;
            map.put(key, specificationList);
        }

        return map;
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
        //String brandUrl = "http://www.callawaygolf.com/on/demandware.store/Sites-CG-Site/en_US/ProductSpecs-Get?productCode=drivers-great-big-bertha-epic-2017";
        //String brandUrl = "http://www.callawaygolf.com/golf-clubs/mens/drivers/drivers-great-big-bertha-epic-2017.html";
        String brandUrl = "http://www.callawaygolf.com/golf-clubs/fwoods-2016-xr-pro.html";
        //BrandSpecs brandSpecs =
        //        scrapper.getBrandSpecs("http://www.callawaygolf.com/on/demandware.store/Sites-CG-Site/en_US/ProductSpecs-Get?productCode=drivers-great-big-bertha-epic-2017");
        //System.out.println(Scrapper.cookies);
        Set<ProductSpecs> productSpecs = scrapper.getProducts(brandUrl);
//        /System.out.println(Scrapper.cookies);
        System.out.println(productSpecs);
    }
}
