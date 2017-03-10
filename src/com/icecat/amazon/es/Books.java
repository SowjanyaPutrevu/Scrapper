package com.icecat.amazon.es;

import com.icecat.BrandSpecs;
import com.icecat.Scrapper;
import com.icecat.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

public class Books extends Scrapper {
    private List<String> getProductUrls() {
        String[] ean = {"9782330072827",
                "9788420306025",
                "9788420301495",
                "9788491420507",
                "9780749016029",
                "9788416394401",
                "9781449474256",
                "9788494347672",
                "9781786491978",
                "9781408851104",
                "9781408881385",
                "9781408857335",
                "9781472937681",
                "9781408876947",
                "9781408870020",
                "9788494606434",
                "9781782118749",
                "9788498465020",
                "9788416387908",
                "9781907970887",
                "9788494437861",
                "9788493784591",
                "9788417000011",
                "9788494613852",
                "9788494614453",
                "9788494614446",
                "9788494174957",
                "9788416034833",
                "9788494554100",
                "9788497849944",
                "9788416572892",
                "9788497843126",
                "9780008220853",
                "9781848454996",
                "9780008220075",
                "9780008113773",
                "9780008218652",
                "9780007459650",
                "9780008105983",
                "9780007531639",
                "9780008115418",
                "9780062562067",
                "9780062656322",
                "9781786691361",
                "9788425414886",
                "9788425437670",
                "9788425437922",
                "9788425438103",
                "9788425438493",
                "9781473638426",
                "9780340923146",
                "9781785781612",
                "9788416434664",
                "9782290138892",
                "9788494576973",
                "9788494576911",
                "9788494520433",
                "9788487403187",
                "9788416145348",
                "9780751567366",
                "9788494537677",
                "9781250092175",
                "9781250118714",
                "9781250085559",
                "9781250097293",
                "9788416737109",
                "9788416858057",
                "9788416505876",
                "9781474601764",
                "9781409168751",
                "9788499758190",
                "9788499758206",
                "9788499758312",
                "9788497798402",
                "9788416970230",
                "9781509822843",
                "9781509827558",
                "9781447289432",
                "9781509823314",
                "9781509812134",
                "9788415830993",
                "9780241978153",
                "9780141983202",
                "9780718179502",
                "9780718184087",
                "9781844883516",
                "9780241980255",
                "9780141357058",
                "9780141028743",
                "9780399583063",
                "9781101987995",
                "9780735215665",
                "9780735216655",
                "9780143129998",
                "9781524705312",
                "9780515155594",
                "9780735215818",
                "9788492964673",
                "9788492964567",
                "9781785036064",
                "9781786531377",
                "9781911215240",
                "9781780890883",
                "9781784702304",
                "9781784741648",
                "9780091959418",
                "9781785941115",
                "9781780896878",
                "9781784755119",
                "9780345541437",
                "9780385351997",
                "9780553447705",
                "9781101966044",
                "9781616958008",
                "9780812983203",
                "9781101911198",
                "9780812995343",
                "9780804173315",
                "9781101971222",
                "9780451495112",
                "9781524759971",
                "9780399591235",
                "9781524711139",
                "9781101967539",
                "9780451496218",
                "9780812987935",
                "9781612196596",
                "9780345802538",
                "9781612196015",
                "9781524711115",
                "9788416981274",
                "9781471156311",
                "9781471133220",
                "9781471159466",
                "9781454704003",
                "9788467713039",
                "9788467713022",
                "9788467713015",
                "9788467713008",
                "9788467711691",
                "9788467711707",
                "9788467711684",
                "9788467711677",
                "9788467710601",
                "9788467710625",
                "9788467710649",
                "9788467719666",
                "9788467752571",
                "9788467750232",
                "9788467734829",
                "9788467737486",
                "9788467752632",
                "9788467752625",
                "9788467752618",
                "9788467752601",
                "9788467752373",
                "9788467752113",
                "9788467752120",
                "9780552168977",
                "9781784162061",
                "9780552170291",
                "9788494399251",
                "9782845638822",
                "9788416428946",
                "0889853948321",
                "0075597939613",
                "0889853751716",
                "8429652008134",
                "0095115517925",
                "0095115518021",
                "0889854062927",
                "0889854063214",
                "0889854055110",
                "0889854101091",
                "0889854101190",
                "0190296982552",
                "0190296982538",
                "0093624915478",
                "8436564933492",
                "3760014199660",
                "3760014197147",
                "0095115193228",
                "8436564933867",
                "0889854068325",
                "8421597096214",
                "8421597096177",
                "8421597096290",
                "8428353706127",
                "8436004065707",
                "8436004065745",
                "8428353786211",
                "0605633009422",
                "0605633009323",
                "0605633009620",
                "0605633009729",
                "0605633010022",
                "0605633009828",
                "3760195734254",
                "4010072774132",
                "7619990103757",
                "5400439003743",
                "5400439003767",
                "0605633134827",
                "0605633135022",
                "8424562235113",
                "0605633134728",
                "0889854129521",
                "8437016124468",
                "0889854048327",
                "0889854126223",
                "8436559461597",
                "0605633134643",
                "0605633135329"};
        List<String> title = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String url = Constants.SEARCH_URL.replace("%data%", ean[1]);
            String html = get_html(url);
            Document document = parse_html(html);
            Element id = document.getElementById("result_0");
            Element a = id.getElementsByTag("a").get(0);

            title.add(a.attr("href"));

        }
        //System.out.println(title);
        return title;
    }

    private String getTitle(Document document) {
        /*String url = getProductUrls().get(0);
        String html = get_html(url);
        Document document = parse_html(html);*/
        String text = document.getElementById("productTitle").text();
        System.out.println(text);
        return text;
    }

    /*private List<String> getImageList(Document document) {
        List<String> images = new ArrayList<>();
            /*String url = getProductUrls().get(0);
            String html = get_html(url);
            Document document = parse_html(html);
        //System.out.println(document);
        Element element = document.getElementById("ebooks-img-canvas");
        Elements imgs = element.getElementsByTag("img");
        //System.out.println(elements.size());
        for (Element img : imgs) {
            String src = img.attr("src");
            images.add(src);
        }
        System.out.println(images);
        return images;
    }*/

    private String getDesc(Document document) {
            /*String url = getProductUrls().get(0);
            String html = get_html(url);
            Document document = parse_html(html);*/
        /*if (document.hasClass("productDescriptionWrapper")) {
            String text1 = document.getElementsByClass("productDescriptionWrapper").get(0).text();
            System.out.println(text1);
            return text1;
       /* } else {*/
            String text = document.getElementById("productDescription").text();
            System.out.println(text);
            return text;
        }


    private List<String> getDetails(Document document) {
        List<String> details = new LinkedList<>();
            /*String url = getProductUrls().get(0);
            String html = get_html(url);
            Document document = parse_html(html);*/
        Element element = document.getElementById("detail_bullets_id");
        Elements tags = element.getElementsByTag("li");
        for (Element tag : tags) {
            String body = tag.text();
            details.add(body);
            /*String[] text = body.split(":");
            details.put(text[0], text[1]);*/
        }
        System.out.println(details);
        return details;
    }

    private BrandSpecs brandSpecs(String url) {
        BrandSpecs brandSpecs = new BrandSpecs();
        Document brandDocument = parse_html(get_html(url));

        String title = getTitle(brandDocument);
        brandSpecs.setBrand_name(title);

        //List<String> images = getImageList(brandDocument);

        String desc = getDesc(brandDocument);
        if (desc != null) {
            brandSpecs.setDescription(desc);
        }
        //Map<String, String> features = getDetails(brandDocument);
        /*Map<String,String> features = getDetails(brandDocument);
        brandSpecs.setDetails(features);*/

        return brandSpecs;
    }
        private void writeToFile(BrandSpecs brandSpecs,String filePath){
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
                bw.write("\"Title\","+
                        "\"Description\","+
                        "\"Image 1\","+
                        "\"Image 2\","+
                        "\"Image 3\","+
                        "\"Image 4\","+
                        "\"RTB Title1\","+
                        "\"RTB Value1\","+
                        "\"RTB Title2\","+
                        "\"RTB Value2\","+
                        "\"RTB Title3\","+
                        "\"RTB Value3\","+
                        "\"RTB Title4\","+
                        "\"RTB Value4\","+
                        "\"RTB Title5\","+
                        "\"RTB Value5\","+
                        "\"RTB Title6\","+
                        "\"RTB Value6\","+
                        "\"RTB Title7\","+
                        "\"RTB Value8\",");
                bw.newLine();
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getBrand_name()) +
                        "\"" + Utils.formatForCSV(brandSpecs.getDescription()) + "\",");
                int i = 0;
                //pictures 12
                List<String> images = brandSpecs.getImagesList();
                if (images != null)
                    for (String image : images) {
                        bw.write("\"" + Utils.formatForCSV(image) + "\",");
                        i++;
                        if (i == 4) break;
                    }
                while (i < 4) {
                    bw.write(",");
                    i++;
                }
                i=0;
                for (Map.Entry<String, String> entry : brandSpecs.getFeatures().entrySet()) {
                    bw.write("\"" + Utils.formatForCSV(entry.getKey()) + "\",\"" + Utils.formatForCSV(entry.getValue()) + "\"," );
                    i++;
                    if(i == 7) break;
                }

                while( i < 7){
                    bw.write("\"\",\"\",\"\",");
                    i++;
                }

            }catch (Exception e) {
                e.printStackTrace();
            }
        }


    public static void main(String[] args) {
        Books books = new Books();
        String filePath = "C:\\Users\\Sowjanya\\Documents\\Books";
       // for(int i = books.getProductUrls().size()-1 ; i > 0 ; i--) {
            BrandSpecs brandSpecs = books.brandSpecs("https://www.amazon.es/ballena-invierno-%C3%80lbums-Locomotora/dp/8416394407/ref=sr_1_1?ie=UTF8&qid=1488449246&sr=8-1&keywords=9788416394401");
            //clubs.writeToFile(brandSpecs,filePath+ File.separator+brandSpecs.getBrand_name()+".csv");
       // }

    }
}
