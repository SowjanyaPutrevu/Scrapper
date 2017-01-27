package com.icecat.callaway;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

import com.icecat.*;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CallawayGolfBalls extends Scrapper {

	private List<String> getBrandUrls() {
		List<String> brandUrls = new ArrayList<>();

		for (int start = 0; ; start += 12) {
			String url = Constants.BALL_CATEGORY_URL.replace("%data%", start + "");
			String html = get_html(url);
			Document document = parse_html(html);

			Elements spans = document.getElementsByTag("a"); //git test
			if (spans.size() == 0)
				break;

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
		for (int i = 0; i < images.size(); i++) {
			if (images.get(i).hasClass("rsTmb"))
				productImages.add(images.get(i));
		}
		return Utils.getImageUrls(productImages);
	}

	private String getBrandName(String brandUrl) {
		String[] tokens = brandUrl.split("/");
		return tokens[tokens.length - 1].replace(".html", "");
	}

	private String getSpecsUrl(String brandUrl) {
		return Constants.PRODUCT_SPECS_URL.replace("%data%", getBrandName(brandUrl));
	}

	public BrandSpecs getBrandSpecs(String brandUrl) {
		String brandName = getBrandName(brandUrl);
		return getBrandSpecs(brandUrl, brandName);
	}

	private BrandSpecs getBrandSpecs(String brandUrl, String brandName) {
		BrandSpecs brandSpecs = new BrandSpecs();
		brandSpecs.setName(brandName);

		Document brandDocument = parse_html(get_html(brandUrl));
		List<String> images = getBrandImages(brandDocument);

		if (images != null && !images.isEmpty()) {
			brandSpecs.setImagesList(images);
		}

		Element description = brandDocument.getElementById(Constants.BRAND_DESCRIPTION_ID);
		if (description != null)
			brandSpecs.setDescription(description.text());

		Map<String, String> fdescription = new HashMap<>();
		Map<String, String> fImages = new HashMap<>();

		Elements tags = brandDocument.getElementsByClass("product-technology-feature-text");
		Elements tagImages = brandDocument.getElementsByClass("product-technology-feature-img");

		int j = 0;
		for (Element element : tags) {
			Elements feature = element.getElementsByTag("h2");
			String ftext = feature.text();
			feature.remove();
			String dtext = element.text();
			fdescription.put(ftext, dtext);
			Elements feImgs = tagImages.get(j).getElementsByTag("img");
			fImages.put(ftext, feImgs.get(0).attr("src"));
			j++;
		}
		brandSpecs.setFeatures(fdescription);
		brandSpecs.setFeatureImages(fImages);

		Set<String> videos = new HashSet<>();
		Elements videoClass = brandDocument.getElementsByClass(Constants.VIDEO_CLASS);
		for (Element video : videoClass) {
			String videoUrl = video.attr("data-url");
			videos.add(videoUrl);
		}

		List<String> youtube = new ArrayList<>();
		for (String video : videos) {
			String html = get_html(video);
			Document document = parse_html(html);
			Elements classes = document.getElementsByClass("mk-video-container");
			for (Element url : classes) {
				Element iframe = url.getElementsByTag("iframe").first();
				String videoUrls = iframe.attr("src");
				youtube.add(videoUrls);
			}
		}
		brandSpecs.setVideos(youtube);

		String specsUrl = getSpecsUrl(brandUrl);
		String specsHtml = get_html(specsUrl);
		Document specsDocument = parse_html(specsHtml);
		Elements element = specsDocument.getElementsByClass("table-responsive");

		Map<String,List<Specification>> map = new HashMap<>();

			for(Element element1 : element) {
				List<List<Specification>> tableData = parseTable(element1);
				for (List<Specification> row : tableData) {
					 String k = row.get(0).getValues();
					 map.put(k,row);
				}
			}
		brandSpecs.setGeneralSpecs(map);
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
	public void writeToFile(BrandSpecs brandSpecs,String filePath) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			bw.write("\"BrandName\","+"\""+Utils.formatForCSV(brandSpecs.getName())+"\"");
			bw.newLine();
			String imagesList = "";
			if (brandSpecs.getImagesList() != null) {
				for (String imageUrl : brandSpecs.getImagesList()) {

					imagesList += "\"" + Utils.formatForCSV(imageUrl) + "\",";
				}
			}
			bw.write("\"images\", " + imagesList);
			bw.newLine();
			bw.write("\"Description\","+"\""+Utils.formatForCSV(brandSpecs.getDescription())+"\"");
			bw.newLine();
			String videosList = "";
			if(brandSpecs.getVideos() != null){
				for(String video : brandSpecs.getVideos()) {
					videosList += "\"" + Utils.formatForCSV(video) + "\"," ;
				}
			}
			bw.write("\"video\"," +  videosList );
			bw.newLine();
			int i =1;
			for(Map.Entry<String,String> entry : brandSpecs.getFeatures().entrySet()) {
				bw.write("\"RTB"+i+"\"," + "\"" + Utils.formatForCSV(entry.getKey()) + "\","+"\""+Utils.formatForCSV(entry.getValue())+"\","+"\""+Utils.formatForCSV(brandSpecs.getFeatureImages().get(entry.getKey()))+"\"");
				bw.newLine();
				i++;
			}
			for(Map.Entry<String,List<Specification>> entry : brandSpecs.getGeneralSpecs().entrySet()){
				List<Specification> specifications = entry.getValue();
				for( i = 0; i < specifications.size();i++){
					// "\""+a+"\",b"
					bw.write("\""+specifications.get(i).getName()+"\","+"\""+specifications.get(i).getValues() + "\"");
					bw.newLine();
				}
			}

			bw.close();


		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		CallawayGolfBalls scrapper = new CallawayGolfBalls();

		String filePath = "C:\\Users\\Sowjanya\\Documents\\Callawayballs";
		List<String> brandUrls =  scrapper.getBrandUrls();
		for(int i=0;i<brandUrls.size();i++) {
			BrandSpecs brandSpecs = scrapper.getBrandSpecs(brandUrls.get(i));
			scrapper.writeToFile(brandSpecs,filePath+ File.separator+brandSpecs.getName()+".csv");
		}
	}
}
