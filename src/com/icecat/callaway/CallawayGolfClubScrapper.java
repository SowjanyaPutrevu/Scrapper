package com.icecat.callaway;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        Elements productImages = new Elements();
        for(int i=0; i<images.size(); i++){
            if( images.get(i).hasClass("rsTmb")) {
                productImages.add(images.get(i));
            }
        }
        return Utils.getImageUrls(productImages);
    }
    private List<String> get3Dview(Document document){
        Elements urls3d = document.getElementsByClass("product-3D-iframe");
        Elements view3dUrls = new Elements();
        for(int i = 0; i<urls3d.size();i++ ){
            view3dUrls.add(urls3d.get(i));
        }
        return Utils.getImageUrls(view3dUrls);
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
        //System.out.println(brandDocument);
        List<String> images = getBrandImages(brandDocument);

        if(images!=null && !images.isEmpty()){
            brandSpecs.setImagesList(images);
        }


        Elements title = brandDocument.getElementsByTag("h1");
        for(Element text : title){
            if(text != null)
                brandSpecs.setBrand_name(text.text());
        }

        List<String> threeD = get3Dview(brandDocument);

        if(threeD!=null && !threeD.isEmpty()){
            brandSpecs.setThreeD(threeD);
        }

        Element description = brandDocument.getElementById(Constants.BRAND_DESCRIPTION_ID);
        if(description != null)
            brandSpecs.setDescription(description.text());

        Map<String,String> fdescription = new HashMap<>();
        Map<String,String> fImages     = new HashMap<>();

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

        Set<String> videos = new HashSet<>();
        Elements videoClass = brandDocument.getElementsByClass(Constants.VIDEO_CLASS);
        for(Element video : videoClass){
            String videoUrl =  video.attr("data-url");
            videos.add(videoUrl);
        }

        List<String> youtube = new ArrayList<>();
        for(String video:videos) {
            String html = get_html(video);
            Document document = parse_html(html);
            Elements classes = document.getElementsByClass("mk-video-container");
            for(Element url : classes) {
                Element iframe = url.getElementsByTag("iframe").first();
                String videoUrls = iframe.attr("src");
                youtube.add(videoUrls);
            }
        }
        brandSpecs.setVideos(youtube);

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
                int loftIndex = 0;
                if(!isModel && tableData!=null && !tableData.isEmpty()){
                    while(!tableData.get(0).get(loftIndex).getName().equalsIgnoreCase("loft")){
                        loftIndex++;
                    }
                }
                for(List<Specification> row : tableData){
                    if( !isModel ) {
                        String k = key+ "_" + row.get(loftIndex).getValues();
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

    /*
        Manufacturer, i
        Flex, i
        Shaft Weight,
        Torque,
        Kickpoint
     */

    private void fetchProduct(BrandSpecs brandSpecs, int step, Map<String, List<Specification>> attributes,
                              int attributeValueIndex, Set<ProductSpecs> products,
                              String baseUrl, List<Specification> specList, boolean writeToFile, String filePath, Map<String, String> brandConversions){
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
            product.setBrand(brandSpecs.getBrand_name());
            product.setBrandSpecs(brandSpecs);
            product.setSpecifications(specList);
            Specification spec = specList.get(step);
            product.setPrice(spec.getAttributes().get("variantPrice"));
            product.setSourceId(spec.getProductId());

            String brandUrl = Constants.SEARCH_URL;
            String brandHtml = get_html(brandUrl.replace("%data%",product.getSourceId()));
            Document document = parse_html(brandHtml);
            Elements tags = document.getElementsByClass("product-title-container");
            if (tags!= null && !tags.isEmpty()){
                Elements title = tags.get(0).getElementsByTag("h1");
                for(Element text : title){
                    if(text != null)
                        product.setName(text.text());
                }
            }


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

            int size = products.size();
            products.add(product);

            if(writeToFile && products.size() > size){
                System.out.println("Fetched a product! " + product.getSourceId());
                writeFile(product, filePath, brandConversions);
            }
        } else {
            for( int i=0; i< attributes.get(""+ (step+1)).size(); i++ ) {
                List<Specification> newList = new ArrayList<>(specList);
                newList.add(attributes.get((step+1)+"").get(i));
                fetchProduct(brandSpecs, step+1, attributes, i, products, baseUrl, newList, writeToFile, filePath, brandConversions );
            }
        }
    }


    public Set<ProductSpecs> getProducts(String brandUrl, boolean writeToFile, String filePath, BrandSpecs brandSpecs, Map<String, String> brandConversions){
        String html = get_html(brandUrl);
        Document document = parse_html(html);
        String brandName = getBrandName(brandUrl);
        return getProducts(document, writeToFile, filePath, brandSpecs, brandConversions);
    }

    private Set<ProductSpecs> getProducts(Document brandDocument, boolean writeToFile, String filePath, BrandSpecs brandSpecs, Map<String, String> brandConversions){
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
        /*ProductSpecs productSpecs = new ProductSpecs();
        Elements title = brandDocument.getElementsByTag("h1");
        for(Element text : title){
            if(text != null)
                productSpecs.setName(text.text());
        }
       // System.out.println(productSpecs.getName());*/

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
        for(int i=0; i < attributes.get(step+"").size() ; i++) {
            List<Specification> specList = new ArrayList<>();
            specList.add(attributes.get((step)+"").get(i));
            fetchProduct(brandSpecs, step, attributes, i, products, baseUrl, specList, writeToFile, filePath, brandConversions);
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

    public static void writeFile(ProductSpecs product, String filePath, Map<String, String> brandConversions) {
        if (product == null) {
            System.out.println("Null product passed");
            return;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath , true));
            String imagesList = "";
            if (product.getImagesList() != null) {
                for (String imageUrl : product.getImagesList()) {
                    imagesList += "\"" + imageUrl + "\",";
                }
            }


            if (product.getSpecifications() != null) {
                Map<String, String> map = new HashMap<>();
                for (Specification spec : product.getSpecifications()) {
                    map.put(spec.getName(), spec.getValues());
                }
                String clubs = map.get("Clubs");
                String loft = ( map.get("Loft") == null
                        ? ( clubs != null  && clubs.indexOf("(") > -1 ? clubs.substring( clubs.indexOf("(")+1, clubs.indexOf(")") ) : " ") :
                        map.get("Loft") );
                String key = map.get("Gender") + "_" + map.get("Shaft Manufacturer") + "_" + map.get("Shaft Flex")+"_";
                String key_brand = map.get("Gender") + "_" + loft + "_";

                bw.write("\""+Utils.formatForCSV(product.getSourceId()) + "\","
                        + "\"Callaway\","
                        + "\""+Utils.formatForCSV(product.getBrandSpecs().getBrand_name()) + "\","
                        + "\""+Utils.formatForCSV(product.getName())+ "\","
                );

                int i = 0;
                //pictures 12
                List<String> images = product.getBrandSpecs().getImagesList();
                if(images!=null)
                    for(String image : images){
                        bw.write("\"" + Utils.formatForCSV(image) + "\",");
                        i++;
                        if( i == 12 ) break;
                    }
                while(i<12){
                    bw.write(",");
                    i++;
                }
                //videos 12
                i=0;
                List<String> videos = product.getBrandSpecs().getVideos();
                if(videos != null)
                    for(String video : videos){
                        bw.write("\"" + Utils.formatForCSV(video) + "\",");
                        i++;
                        if( i == 12 ) break;
                    }
                while(i<12){
                    bw.write(",");
                    i++;
                }
                //3d videos
                List<String> threeDvideos = product.getBrandSpecs().getThreeD();
                if( threeDvideos != null && !threeDvideos.isEmpty()) {
                    bw.write("\"" + threeDvideos.get(0) + "\",");
                }else{
                    bw.write("\""+"null"+"\",");
                }
                //description
                bw.write("\"" + Utils.formatForCSV(product.getBrandSpecs().getDescription()) + "\",");
                i=0;
                for (Map.Entry<String, String> entry : product.getBrandSpecs().getFeatures().entrySet()) {
                    bw.write("\"" + Utils.formatForCSV(entry.getKey()) + "\",\"" + Utils.formatForCSV(entry.getValue()) + "\"," +
                            "\"" + Utils.formatForCSV(product.getBrandSpecs().getFeatureImages().get(entry.getKey())) + "\",");
                    i++;
                    if(i == 6) break;
                }

                while( i < 6){
                    bw.write("\"\",\"\",\"\",");
                    i++;
                }

                bw.write("\""  +
                        Utils.formatForCSV(product.getModel()) + "\",\""  +
                        Utils.formatForCSV(product.getDescription()) + "\",\"" +

                        Utils.formatForCSV(product.getPrice()) + "\",");


                //sku,

                bw.write("\"" + Utils.formatForCSV(map.get("Gender")) + "\",\"" +
                        Utils.formatForCSV(map.get("Hand")) + "\",\"" +
                        Utils.formatForCSV(loft) + "\",\"" +
                        Utils.formatForCSV(map.get("Shaft Origin")) + "\",\"" +
                        Utils.formatForCSV(map.get("Shaft Type")) + "\",\"" +
                        Utils.formatForCSV(map.get("Shaft Manufacturer")) + "\",\"" +
                        Utils.formatForCSV(map.get("Shaft Material")) + "\",\"" +
                        Utils.formatForCSV(map.get("Shaft Flex")) + "\",\"" +
                        brandConversions.get(key+"sw") +  "\",\"" +
                        brandConversions.get(key+"tq") +  "\",\"" +
                        brandConversions.get(key+"kp") +  "\",\"" +
                        Utils.formatForCSV(map.get("Grip")) + "\",\"" +
                        Utils.formatForCSV(map.get("Wraps")) + "\",\"" +
                        Utils.formatForCSV(map.get("Length")) + "\",\"" +
                        Utils.formatForCSV(brandConversions.get(key_brand + "sl")) + "\",\"" +
                        Utils.formatForCSV(map.get("Lie Angle") ) + "\",\"" +
                        brandConversions.get(key_brand + "lie") + "\",\"" +
                        brandConversions.get(key_brand + "sw") + "\",\"" +
                        brandConversions.get(key_brand + "cc") + "\""
                ) ;
            }

            bw.newLine();
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(BrandSpecs brand, String filePath ) {
        if (brand == null) {
            System.out.println("Null brand passed");
            return;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath + File.separator + brand.getName() + ".csv"));
            bw.write("\"key\",\"value\"");
            bw.newLine();
            String imagesList = "";
            if (brand.getImagesList() != null) {
                for (String imageUrl : brand.getImagesList()) {
                    imagesList += "\"" + Utils.formatForCSV(imageUrl) + "\",";
                }
            }

            String threeDList = "";
            if (brand.getThreeD() != null) {
                for (String imageUrl : brand.getThreeD()) {
                    threeDList += "\"" + Utils.formatForCSV(imageUrl) + "\",";
                }
            }

            String videosList = "";
            if(brand.getVideos() != null){
                for(String video : brand.getVideos()) {
                    videosList += "\"" + Utils.formatForCSV(video) + "\"," ;
                }
            }
            bw.newLine();
            bw.write("\"images\", " + imagesList);
            bw.newLine();
            bw.write("\"3DView\", " + threeDList);
            bw.newLine();
            bw.write("\"name\",\"" + Utils.formatForCSV(brand.getBrand_name()) + "\"");
            bw.newLine();
            bw.write("\"description\",\"" + Utils.formatForCSV(brand.getDescription()) + "\"");
            bw.newLine();
            bw.write("\"video\"," +  videosList );
            bw.newLine();
            int i = 1;
            for (Map.Entry<String, String> entry : brand.getFeatures().entrySet()) {
                bw.write("rtb"+ i + ",\"" + Utils.formatForCSV(entry.getKey()) + "\",\"" + Utils.formatForCSV(entry.getValue()) + "\"," +
                        "\"" + Utils.formatForCSV(brand.getFeatureImages().get(entry.getKey())) + "\"");
                bw.newLine();
                i++;
            }
            bw.close();

            Map<String, List<Specification>> generalSpecs = brand.getGeneralSpecs();
            if (generalSpecs != null) {
                bw = new BufferedWriter(new FileWriter(filePath + File.separator + brand.getName() + "_standard.csv"));
                for (Map.Entry<String, List<Specification>> entry : generalSpecs.entrySet()) {
                    if (entry.getValue() != null) {
                        Map<String, String> map = new HashMap<>();
                        for (Specification spec : entry.getValue()) {
                            map.put(spec.getName(), spec.getValues());
                        }
                        bw.write(
                                "\"" + Utils.formatForCSV( entry.getKey().split(" ")[0] ) + "\"," +
                                        "\"" + Utils.formatForCSV(map.get("Model") ) + "\"," +
                                        "\"" + Utils.formatForCSV(map.get("Loft") ) + "\"," +
                                        "\"" + Utils.formatForCSV(map.get("Availability") ) + "\"," +
                                        "\"" + Utils.formatForCSV(map.get("Standard Length") ) + "\"," +
                                        "\"" + Utils.formatForCSV(map.get("Lie" ) ) + "\"," +
                                        "\"" + Utils.formatForCSV(map.get("CC") ) + "\"," +
                                        "\"" + Utils.formatForCSV(map.get("Swing Weight") ) + "\""
                        );
                        bw.newLine();
                    }
                    bw.flush();
                }
                bw.close();
            }

            generalSpecs = brand.getModelSpecs();
            if (generalSpecs != null) {
                bw = new BufferedWriter(new FileWriter(filePath + File.separator + brand.getName() + "_manufacturer.csv"));
                for (Map.Entry<String, List<Specification>> entry : generalSpecs.entrySet()) {
                    if (entry.getValue() != null) {
                        Map<String, String> map = new HashMap<>();
                        for (Specification spec : entry.getValue()) {
                            map.put(spec.getName(), spec.getValues());
                        }
                        bw.write(
                                "\"" + Utils.formatForCSV( entry.getKey().split(" ")[0] ) + "\"," +
                                        "\"" + Utils.formatForCSV(map.get("Manufacturer") ) + "\"," +
                                        "\"" + Utils.formatForCSV(map.get("Flex") ) + "\"," +
                                        "\"" + Utils.formatForCSV(map.get("Shaft Weight") ) + "\"," +
                                        "\"" + Utils.formatForCSV(map.get("Torque") ) + "\"," +
                                        "\"" + Utils.formatForCSV(map.get("Kickpoint" ) ) + "\""
                        );
                        bw.newLine();
                    }
                    bw.flush();
                }
                bw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                writeFile(brandSpecs, filePath);
                //Step 3: get Product Specs
                filePath = filePath + File.separator + brandName + "-productListnew.csv";
                BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
                bw.write("\"sku\","
                        + "\"brand\","
                        + "\"familyName\","
                        + "\"productName\","
                        + "\"picture 1\","
                        + "\"picture 2\","
                        + "\"picture 3\","
                        + "\"picture 4\","
                        + "\"picture 5\","
                        + "\"picture 6\","
                        + "\"picture 7\","
                        + "\"picture 8\","
                        + "\"picture 9\","
                        + "\"picture 10\","
                        + "\"picture 11\","
                        + "\"picture 12\","
                        + "\"video 1\","
                        + "\"video 2\","
                        + "\"video 3\","
                        + "\"video 4\","
                        + "\"video 5\","
                        + "\"video 6\","
                        + "\"video 7\","
                        + "\"video 8\","
                        + "\"video 9\","
                        + "\"video 10\","
                        + "\"video 11\","
                        + "\"video 12\","
                        + "\"3d video 1 \","
                        + "\"brand description\","

                        + "\"rtb title 1\","
                        + "\"rtb description 1\","
                        + "\"rtb image 1\","

                        + "\"rtb title 2\","
                        + "\"rtb description 2\","
                        + "\"rtb image 2\","

                        + "\"rtb title 3\","
                        + "\"rtb description 3\","
                        + "\"rtb image 3\","

                        + "\"rtb title 4\","
                        + "\"rtb description 4\","
                        + "\"rtb image 4\","

                        + "\"rtb title 5\","
                        + "\"rtb description 5\","
                        + "\"rtb image 5\", "

                        + "\"rtb title 6\","
                        + "\"rtb description 6\","
                        + "\"rtb image 6\","

                        + "\"model\"," + "\"description\"," +
                        "\"price\","
                        + "\"gender\"," + "\"hand\"," + "\"loft\"," + "\"shaft origin\"," + "\"shaft type\","
                        + "\"shaft manufacturer\"," + "\"shaft material\"," + "\"shaft flex\","+ "\"shaft weight\","+ "\"torque\","+ "\"kickpoint\","
                        + "\"grip\"," + "\"wraps\"," + "\"length\"," + "\"standard length\"," + "\"lie angle\"," + "\"lie\"," + "\"swing weight\"," + "\"cc\"");
                bw.newLine();
                bw.close();

                Map<String, String> brandConversions = new HashMap<>();
                //gender_Manufacturer_flex_shaftw, value
                //gender_Ma_fx_torque, value
                //gender_Ma,fx_kicpo, value
                for(Map.Entry<String, List<Specification>> entry : brandSpecs.getModelSpecs().entrySet()) {
                    String gender = entry.getKey().toLowerCase().startsWith("wom") ? "Ladies" : "Mens";
                    List<Specification> specifications = entry.getValue();
                    Map<String, String> specsMap = new HashMap<>();
                    for(Specification specification : specifications){
                        specsMap.put(specification.getName(), specification.getValues());
                    }
                    String flex = specsMap.get("Flex").equals("X-Stiff") ? "XStiff" : specsMap.get("Flex");
                    brandConversions.put(gender+ "_" + specsMap.get("Manufacturer")+"_"+ flex +"_sw", specsMap.get("Shaft Weight") );
                    brandConversions.put(gender+ "_" + specsMap.get("Manufacturer")+"_"+ flex +"_tq", specsMap.get("Torque") );
                    brandConversions.put(gender+ "_" + specsMap.get("Manufacturer")+"_"+ flex +"_kp", specsMap.get("Kickpoint") );
                }
/*"\"" + Utils.formatForCSV(map.get("Standard Length") ) + "\", " +
                                        "\"" + Utils.formatForCSV(map.get("Lie" ) ) + "\", " +
                                        "\"" + Utils.formatForCSV(map.get("CC") ) + "\", " +
                                        "\"" + Utils.formatForCSV(map.get("Swing Weight") ) + "\" "
 */
                for(Map.Entry<String, List<Specification>> entry : brandSpecs.getGeneralSpecs().entrySet()) {
                    String gender = entry.getKey().toLowerCase().startsWith("wom") ? "Ladies" : "Mens";
                    List<Specification> specifications = entry.getValue();
                    Map<String, String> specsMap = new HashMap<>();
                    for(Specification specification : specifications){
                        specsMap.put(specification.getName(), specification.getValues());
                    }
                    brandConversions.put(gender+ "_" + specsMap.get("Loft")+"_sl", specsMap.get("Standard Length") );
                    brandConversions.put(gender+ "_" + specsMap.get("Loft")+"_cc", specsMap.get("CC") );
                    brandConversions.put(gender+ "_" + specsMap.get("Loft")+"_sw", specsMap.get("Swing Weight") );
                    brandConversions.put(gender+ "_" + specsMap.get("Loft")+"_lie", specsMap.get("Lie") );
                }
                Set<ProductSpecs> productSpecs = scrapper.getProducts(brandUrl, true, filePath, brandSpecs, brandConversions);
                //Set<String> skus = scrapper.getSKUs(brandUrl);

                System.out.println("Finished processing brand: " + brandName + " found " + productSpecs.size() + " products");
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
        //List<String> brandUrls =  scrapper.getBrandUrls();
        //"http://www.callawaygolf.com/golf-clubs/drivers-2016-xr.html"
        //,"http://www.callawaygolf.com/golf-clubs/mens/drivers/drivers-great-big-bertha-epic-2017.html","http://www.callawaygolf.com/golf-clubs/fwoods-2016-xr-pro.html"
        String[] brandUrls = {""};
        ExecutorService executor = Executors.newFixedThreadPool(15);
        for( String brandUrl : brandUrls) {
            executor.execute(new WorkerThread(scrapper, brandUrl));
        }
        executor.shutdown();
        while (!executor.isTerminated()) { System.out.println("Waiting for the threads to finish tasks!"); Thread.sleep(60000);  };
    }
}