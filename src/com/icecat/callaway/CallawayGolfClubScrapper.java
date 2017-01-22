package com.icecat.callaway;
import java.util.*;

import com.icecat.*;
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
                            String k = key+ "_" + row.get(0).getValues().get(0);
                            generalSpecs.put(k, row);
                        } else {
                            String k = key + "_" + model + "_" + row.get(1).getValues().get(0);
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

    public ProductSpecs getProductSpecs(String sku){
        String html = get_html(Constants.CART_URL.replace("%data%", sku));
        System.out.println(html);
        Document document = parse_html(html);
        return getProductSpecs(document);
    }

    /*
    Apparently, the easiest way to get item specs is by adding it to the cart!
     */
    private ProductSpecs getProductSpecs(Document productDocument){
        ProductSpecs productSpecs = new ProductSpecs();

        Element cartItem = productDocument.getElementsByClass(Constants.CART_0_ITEM_CLASS).first();
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
                List<String> values = new ArrayList<>();
                spec.setValues(values);
                values.add(spans.get(1).text());
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
        //BrandSpecs brandSpecs = scrapper.getBrandSpecs("http://www.callawaygolf.com/on/demandware.store/Sites-CG-Site/en_US/ProductSpecs-Get?productCode=drivers-great-big-bertha-epic-2017");
        ProductSpecs productSpecs = scrapper.getProductSpecs("spr4625662");
        System.out.println(productSpecs);
    }
}
