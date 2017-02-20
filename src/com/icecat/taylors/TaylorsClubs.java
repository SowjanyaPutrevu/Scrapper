package com.icecat.taylors;

import com.icecat.BrandSpecs;
import com.icecat.Scrapper;
import com.icecat.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Created by Sowjanya on 2/13/2017.
 */
public class TaylorsClubs extends Scrapper{

  private List<String> getBrandUrls(){
      List<String> urls = new ArrayList<>();
      String url = Constants.CATEGORY_URL;
      String html = get_html(url);
      Document document = parse_html(html);
      Elements category = document.getElementsByClass("selected-refinement");

          Elements links = category.get(0).getElementsByClass("refinement-link");
          for(Element link : links){
              String pUrl = link.attr("href");
              urls.add(pUrl);
          }

      //System.out.println(urls);
      //System.out.println(urls.size());
      return urls;
  }

  private List<String> getProductUrls(){
      List<String> pUrls = new ArrayList<>();
      for(int index = 0; index < getBrandUrls().size();index++) {
          String purl = getBrandUrls().get(index);
          String html = get_html(purl);
          Document document = parse_html(html);
          Elements category = document.getElementsByClass("thumb-link");

          for(Element element : category){
            String url = element.attr("href");
            pUrls.add(url);
          }
      }
      //System.out.println(pUrls);
     // System.out.println(pUrls.size());
      return pUrls;
  }
  /*private List<String> getBrandImages() {
        List<String> bImages = new ArrayList<>();
       // for(int index = 0; index < getProductUrls().size();index++ ) {
            String bImageUrl = getProductUrls().get(0);
           // System.out.println(bImageUrl);
            String html = get_html(bImageUrl);
           // System.out.println(html);
            Document document = parse_html(html);
           // System.out.println(document);
            Elements images = document.getElementsByClass("productthumbnail lazyOwl");

            for(Element image : images){
                String urls = image.attr("src");
                System.out.println(urls);
                bImages.add(urls);
            //}
        }
       // System.out.println(bImages);
        return bImages;
  }*/
  private Set<String> getBrandImages(Document document) {
      Set<String> productImages = new HashSet<>();
      Elements images = document.getElementsByTag("img");
      for(int i=0; i<images.size(); i++){
           Elements iClass = images.get(i).getElementsByClass("primary-image");
              for (Element element : iClass) {
                  String pImages = element.attr("src");
                  productImages.add(pImages);
              }
          }

      //System.out.println(productImages);
      return productImages;
  }


  private String getDescription(Document document){
      Elements category = document.getElementsByClass("product-description-short");
      String description = category.get(0).text();
     // System.out.println(description);
      return description;
  }
  private Map<String,Set<String>> getlongDescription(String url){

      Map<String,Set<String>> longDescription = new HashMap<>();
      Set<String> longdesc = null;
      Document document = parse_html(get_html(url));

      Element element = document.getElementById("pdpTab-product-description-full");

          Elements tags = element.getElementsByTag("strong");
          for(Element tag : tags){
              String keyPar = tag.text();
             //System.out.println(keyPar);
              Elements par = element.getElementsByTag("p");
              for(Element p : par){
                  if(p.text().contentEquals(keyPar)){
                      String[] split = p.text().split(keyPar);
                      System.out.println(split.length);
                  }

              }
              longDescription.put(keyPar,longdesc);
          }
     // System.out.println(longdesc);
      return longDescription;
  }

  private List<String> getspecUrl(Document document){
      List<String> specUrl = new ArrayList<>();
          Element element = document.getElementById("pdpTab2");
          Elements images = element.getElementsByTag("img");
          for (Element image : images) {
              specUrl.add(image.attr("src"));
          }
      //System.out.println(specUrl);
      return specUrl;
  }

  private BrandSpecs getBrandSpecs(String brandUrl){
      BrandSpecs brandSpecs = new BrandSpecs();
      Document brandDocument = parse_html(get_html(brandUrl));


      Set<String> images = getBrandImages(brandDocument);
      brandSpecs.setProductImages(images);

      Elements category = brandDocument.getElementsByClass("product-name");
      String brandNames = category.text();
      brandSpecs.setBrand_name(brandNames);

      String description = getDescription(brandDocument);
      brandSpecs.setDescription(description);

      List<String> specUrl = getspecUrl(brandDocument);
      brandSpecs.setSpecUrl(specUrl);
      //System.out.println(brandSpecs);
      return brandSpecs;
  }


    public static void main(String[] args) {
        TaylorsClubs clubs = new TaylorsClubs();
        //clubs.getProductUrls();
       /* for(int index = 0; index < clubs.getProductUrls().size();index++ ) {
            String pUrl = clubs.getProductUrls().get(0);
        }
        */
        String pUrl = "http://taylormadegolf.eu/M1-Driver/DW-WZ577.html";
        clubs.getBrandSpecs(pUrl);
        clubs.getlongDescription(pUrl);
    }
}
