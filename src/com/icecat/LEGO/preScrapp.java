package webscrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class preScrapp {

	

	List<String> LinksOnPage =new LinkedList<String>();
	Set<String>  LinksVisited=new HashSet<String>();
	private Document currentPage;
	private Connection connection;
	private static final String USER_AGENT= "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1"; //user_agent setzen, damit der server denkt man sein ein normaler webbrowser
    private int imageNumb;
    private int rtbNumb;
	
    public preScrapp(String productPage) {	
		scrapPage(productPage);
		

	}



	public int[] getIMGRtb()
	{
		
		int[] imageRtb=new int[2];
	imageRtb[0]=imageNumb;
	imageRtb[1]=rtbNumb;
	return imageRtb;
	}
	
	private void scrapPage(String url)
	{
		
		
		
		try {connection = Jsoup.connect(url).userAgent(USER_AGENT);
			currentPage= connection.get();
		
		if (connection.response().statusCode() !=200){
			System.out.println("FAILURE, HTML error:"+connection.response().statusCode()); }
		
		if (!connection.response().contentType().contains("text/html"))
			
		{
			
			System.out.println("FAILURE, Link is not of type html");
		
			
			
		}
		
	


	    rtbNumb= currentPage.select("span.product-features__description li").size();

		String productId=currentPage.select("dd[data-test=product-details__product-code]").first().text();
		
		String productIMGNumbSource= Jsoup.connect("https://sh-s7-live-s.legocdn.com/is/image//LEGO/"+productId+"_mms?req=set,json,UTF-8&handler=requestS7MixedMediaSetCallback").userAgent(USER_AGENT).get().html();

		
		
		productIMGNumbSource="{"+productIMGNumbSource.substring(productIMGNumbSource.indexOf(("_mms\","))+6, productIMGNumbSource.length()-15);
		
		
		JSONObject imgList = new JSONObject(productIMGNumbSource);
	
        Object imgArrayPre = imgList.get("item");
        String[] productIMGIds;

		    
		    
		if (imgArrayPre instanceof JSONArray) {
			
			    JSONArray imgArray=(JSONArray)imgArrayPre;
			    
			    productIMGIds=new String[imgArray.length()];
                for (int h=0; h<imgArray.length();h++)
			    {
			    JSONObject IMGid = imgArray.getJSONObject(h);
			    productIMGIds[h]=(IMGid.getString("iv"));}
		}
		
		else if (imgArrayPre instanceof JSONObject) {
		    JSONObject imgArray=(JSONObject) imgArrayPre;

			productIMGIds=new String[1];
			productIMGIds[0]=(imgArray.getString("iv"));
		}
		else {
			productIMGIds=new String[0];
		}
		
		
		
	 
	 
	    
	    for (String j: productIMGIds)
	    {
	   // System.out.println(j);
	    }
		

		
		
		
		imageNumb=productIMGIds.length;

		
		
	
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Couldn't prescrap : "+url+" because connection couldn't be established: "+e.getMessage());
		}
		catch (NullPointerException e) {
			// TODO Auto-generated catch block
			System.out.println("Couldn't prescrap : "+url+" because of NullPointer on some element (site has different layout)");
		}
	}
	
	
	

	
	}
	
	

