package com.icecat.callaway;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    private String getBrandName(String brandUrl) {
        String[] tokens = brandUrl.split("/");
        return tokens[tokens.length - 1].replace(".html","");
    }

    private String getSpecsUrl(String brandUrl) {
        return Constants.PRODUCT_SPECS_URL.replace("%data%", getBrandName(brandUrl) );
    }

    public BrandSpecs getBrandSpecs(String brandUrl){
        String brandName = getBrandName(brandUrl);
        return getBrandSpecs(brandUrl, brandName);
    }

    private BrandSpecs getBrandSpecs(String brandUrl, String brandName){
        BrandSpecs brandSpecs = new BrandSpecs();
        brandSpecs.setName(brandName);

        Document brandDocument = parse_html(get_html(brandUrl));
        List<String> images = getBrandImages(brandDocument);

        if(images!=null && !images.isEmpty()){
            brandSpecs.setImagesList(images);
        }

        Element description = brandDocument.getElementById(Constants.BRAND_DESCRIPTION_ID);
        if(description != null)
            brandSpecs.setDescription(description.text());

        Map<String,String> fdescription = new HashMap<>();
        Map<String, String> fImages     = new HashMap<>();

        Elements tags = brandDocument.getElementsByClass("product-technology-feature-text");
        Elements tagImages = brandDocument.getElementsByClass("product-technology-feature-img");

        int j = 0;
        for(Element element : tags){
           Elements feature =  element.getElementsByTag("h2");
           String ftext = feature.text();
           feature.remove();
           String dtext = element.text();
           fdescription.put(ftext,dtext);
           Elements feImgs = tagImages.get(j).getElementsByTag("img");
           fImages.put(ftext, feImgs.get(0).attr("src"));
           j++;
        }
        brandSpecs.setFeatures(fdescription);
        brandSpecs.setFeatureImages(fImages);

        List<String> videos = new ArrayList<>();
        Elements videoClass = brandDocument.getElementsByClass(Constants.VIDEO_CLASS);
        for(Element video : videoClass){
           String videoUrl =  video.attr("data-url");
            videos.add(videoUrl);
        }
        brandSpecs.setVideos(videos);


        String specsUrl = getSpecsUrl(brandUrl);
        String specsHtml = get_html(specsUrl);
        Document specsDocument = parse_html(specsHtml);

        Elements titles = specsDocument.getElementsByClass(Constants.BRAND_SPECS_TITLE_CLASS);
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

    public Set<ProductSpecs> getProducts(String brandUrl, boolean writeToFile, String filePath){
        String html = get_html(brandUrl);
        Document document = parse_html(html);
        String brandName = getBrandName(brandUrl);
        return getProducts(document, writeToFile, filePath, brandName);
    }

    private void fetchProduct(String brandName, int step, Map<String, List<Specification>> attributes,
                                int attributeValueIndex, Set<ProductSpecs> products,
                              String baseUrl, List<Specification> specList, boolean writeToFile, String filePath){
        //Here step indicates current attribute, we are changing with the attributeValueIndex.
        //So, we will increment the steps until we reach 7, where we add products. -- These are standard products
        //Also ignoring options at this point
        //Next we change the options
        baseUrl += "&" + attributes.get(step+"").get(attributeValueIndex).getSourceAttributeId() + "="
                + attributes.get(step+"").get(attributeValueIndex).getSourceAttributeValue();
        String html = get_html(baseUrl);
        JSONObject object = new JSONObject(html);
        attributes = parseJson( object.getJSONArray("attributes"), 1 );
        if( step == attributes.size() - 1 ) {
            ProductSpecs product = new ProductSpecs();
            product.setBrand(brandName);
            product.setSpecifications(specList);
            Specification spec = specList.get(step);
            product.setPrice(spec.getAttributes().get("variantPrice"));
            product.setSourceId(spec.getProductId());

            JSONObject shafts = object.getJSONObject("shafts");
            if ( shafts.has("origin") ) {
                JSONArray origin = shafts.getJSONArray("origin");
                for(int i = 0; i < origin.length(); i++){
                   JSONObject obj = origin.getJSONObject(i);
                   if(obj.getBoolean("selected")){
                       Specification sp =new Specification();
                       sp.setName("Shaft Origin");
                       sp.setValues(obj.getString("displayValue"));
                       specList.add(sp);
                       break;
                   }
                }
            }
            if ( shafts.has("material") ) {
                JSONArray origin = shafts.getJSONArray("material");
                for(int i = 0; i < origin.length(); i++){
                    JSONObject obj = origin.getJSONObject(i);
                    if(obj.getBoolean("selected")){
                        Specification sp =new Specification();
                        sp.setName("Shaft Material");
                        sp.setValues(obj.getString("displayValue"));
                        specList.add(sp);
                        break;
                    }
                }
            }

            if ( shafts.has("manufacturer") ) {
                JSONArray origin = shafts.getJSONArray("manufacturer");
                for(int i = 0; i < origin.length(); i++){
                    JSONObject obj = origin.getJSONObject(i);
                    if(obj.getBoolean("selected")){
                        Specification sp =new Specification();
                        sp.setName("Shaft Manufacturer");
                        sp.setValues(obj.getString("displayValue"));
                        specList.add(sp);
                        break;
                    }
                }
            }

            if ( shafts.has("type") ) {
                JSONArray origin = shafts.getJSONArray("type");
                for(int i = 0; i < origin.length(); i++){
                    JSONObject obj = origin.getJSONObject(i);
                    if(obj.getBoolean("selected")){
                        Specification sp =new Specification();
                        sp.setName("Shaft Type");
                        sp.setValues(obj.getString("displayValue"));
                        specList.add(sp);
                        break;
                    }
                }
            }

            if ( shafts.has("flex") ) {
                JSONArray origin = shafts.getJSONArray("flex");
                for(int i = 0; i < origin.length(); i++){
                    JSONObject obj = origin.getJSONObject(i);
                    if(obj.getBoolean("selected")){
                        Specification sp =new Specification();
                        sp.setName("Shaft Flex");
                        sp.setValues(obj.getString("displayValue"));
                        specList.add(sp);
                        break;
                    }
                }
            }



            JSONArray jsonArrayOptions = object.getJSONArray("options");
            for(Object optionObject : jsonArrayOptions) {
                JSONObject option = (JSONObject) optionObject;
                Specification specifcation  = new Specification();
                specifcation.setName( ((JSONObject) optionObject).getString("displayName") );
                JSONArray selectedValues =  option.has("selectedValues") ?  option.getJSONArray("selectedValues") : null;
                if( selectedValues != null && selectedValues.length() > 0 ){
                    specifcation.setValues( selectedValues.getJSONObject(0).getString("displayValue") );
                }
                specList.add(specifcation);
            }

            products.add(product);
            if(writeToFile){
               Utils.writeFile(product, filePath);
            }
        } else {
            for( int i=0; i< attributes.get(""+ (step+1)).size(); i++ ) {
                List<Specification> newList = new ArrayList<>(specList);
                newList.add(attributes.get((step+1)+"").get(i));
                fetchProduct(brandName, step+1, attributes, i, products, baseUrl, newList, writeToFile, filePath );
            }
        }
    }

    private Set<ProductSpecs> getProducts(Document brandDocument, boolean writeToFile, String filePath,  String brandName){
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

        JSONArray jsonArray = jsonObject.getJSONArray("attributes");
        attributes = parseJson(jsonArray, 1);

        //From here, what I need is a minor change in the attributes, which lead to new json, one step at a time
        //There are 7 attrributes, which can be measured as steps! Options come in the end.

        int step = 0;
        String baseUrl = action + "?" + initAttributes.get("pid") + initAttributes.get("vid") + initAttributes.get("qty")
                + initAttributes.get("cgid") + initAttributes.get("format") + initAttributes.get("condition") ;
        for(int i=0; i<attributes.get(step+"").size(); i++) {
            List<Specification> specList = new ArrayList<>();
            specList.add(attributes.get((step)+"").get(i));
            fetchProduct(brandName, step, attributes, i, products, baseUrl, specList, writeToFile, filePath);
        }

        return products;
    }


    private Map<String, List<Specification>> parseJson(JSONArray jsonArray, int setIndexAsKey) {
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

    public static class WorkerThread implements  Runnable{

        CallawayGolfClubScrapper scrapper;
        String brandUrl;

        public WorkerThread(CallawayGolfClubScrapper scrapper, String brandUrl){
            this.scrapper = scrapper;
            this.brandUrl = brandUrl;
        }

        @Override
        public void run() {
            try {
                String brandName = scrapper.getBrandName(brandUrl);
                //Step 2: get Brand Specs
                BrandSpecs brandSpecs = scrapper.getBrandSpecs(brandUrl);
                String filePath = "C:\\Users\\Sowjanya\\Documents\\Callaway Clubs" + File.separator + brandName;
                File f =  new File(filePath );
                f.mkdir();
                Utils.writeFile(brandSpecs, filePath);
                //Step 3: get Product Specs
                filePath = filePath + File.separator + brandName + "-productList.csv";
                BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
                bw.write("\"images\", " + "\"name\"," + "\"model\"," + "\"brand\"," + "\"description\"," + "\"sku\"," + "\"price\"," + "\"gender\"," + "\"hand\"," + "\"loft\"," + "\"shaft origin\"," + "\"shaft type\"," + "\"shaft manufacturer\"," + "\"shaft material\"," + "\"shaft flex\"," + "\"grip\"," + "\"wraps\"," + "\"length\"," + "\"lie angle\",");
                bw.newLine();
                bw.close();

                Set<ProductSpecs> productSpecs = scrapper.getProducts(brandUrl, true, filePath);
                Set<String> skus = scrapper.getSKUs(brandUrl);

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        //String brandUrl = "http://www.callawaygolf.com/on/demandware.store/Sites-CG-Site/en_US/ProductSpecs-Get?productCode=drivers-great-big-bertha-epic-2017";
        //BrandSpecs brandSpecs =
        //        scrapper.getBrandSpecs("http://www.callawaygolf.com/on/demandware.store/Sites-CG-Site/en_US/ProductSpecs-Get?productCode=drivers-great-big-bertha-epic-2017");
        //System.out.println(Scrapper.cookies);
//      String brandUrl = "http://www.callawaygolf.com/golf-clubs/fwoods-2016-xr-pro.html";

        CallawayGolfClubScrapper scrapper = new CallawayGolfClubScrapper();
        //Step1 - get Brand Urls
        // List<String> brandUrls =  scrapper.getBrandUrls();
        //,"http://www.callawaygolf.com/golf-clubs/mens/drivers/drivers-great-big-bertha-epic-2017.html","http://www.callawaygolf.com/golf-clubs/fwoods-2016-xr-pro.html"
        String[] brandUrls = {"http://www.callawaygolf.com/golf-clubs/drivers-2016-xr.html"};
        ExecutorService executor = Executors.newFixedThreadPool(15);
        for( String brandUrl : brandUrls) {
            executor.execute(new WorkerThread(scrapper, brandUrl));
        }
        executor.shutdown();
        while (!executor.isTerminated()) { System.out.println("Waiting for the threads to finish tasks!"); Thread.sleep(60000);  };
    }
}
