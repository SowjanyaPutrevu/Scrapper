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
import java.util.Arrays;
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
import org.jsoup.select.Elements;

public class Webscrapper {


	List<String> LinksOnPage =new LinkedList<String>();
	Set<String>  LinksVisited=new HashSet<String>();
	private Document currentPage;
	private Connection connection;
	private static final String USER_AGENT= "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1"; //user_agent setzen, damit der server denkt man sein ein normaler webbrowser
    private Hashtable<String,String> outputCSVBasic= new Hashtable<String,String>();
	private List<String> outputCSVSpecs= new LinkedList<String>();
	//private String CSVBasicValues[]= {"Requested_prod_id","Requested_GTIN(EAN/UPC)","Requested_Icecat_id","ErrorMessage","Supplier","Prod_id","Icecat_id","GTIN(EAN/UPC)","Category","CatId","ProductFamily","ProductSeries","Model","Updated","Quality","On_Market","Product_Views","HighPic","HighPic Resolution","LowPic","Pic500x500","ThumbPic","Folder_PDF","Folder_Manual_PDF","ProductTitle","ShortDesc","ShortSummaryDescription","LongSummaryDescription","LongDesc","ProductGallery","ProductGallery Resolution","ProductGallery ExpirationDate","360","EU Energy Label","EU Product Fiche","PDF","Video/mp4","Other Multimedia","ProductMultimediaObject ExpirationDate","ReasonsToBuy"};
	private String CSVBasicValues[];
    private FileWriter CSVwriter;
	int reTrys=0;
    int rtbMax=0;
    int vidMax=0;
    int imgMax=0;

	//private List<String> CSVBasicValuesList = new ArrayList<String>() ;
	
	
	public Webscrapper(String productPage, FileWriter csv, int rtbMAX, int imgMAX, int vidMAX, int reTry) 
	{
		this.reTrys=reTry;
	    this.rtbMax=rtbMAX;
	    this.imgMax=imgMAX;
	    this.vidMax=vidMAX;
		this.CSVwriter=csv;
		scrapPage(productPage);

	}

	private void scrapPage(String url)
	{
		List<String> CSVBasics= new LinkedList<String>();
		String[] CSVBasics_begin={"Title","Product ID","Ages","Category","Pieces","VIP Points"};

		for (String b:CSVBasics_begin){
		CSVBasics.add(b);};
		
		for (int u=0; u<rtbMax; u++)
		{
			
			CSVBasics.add("rtb "+u);};

		
		for (int p=0; p<imgMax; p++)
		{
				
			CSVBasics.add("Image "+p);};
		
			
			
			
			for (int p=0; p<vidMax; p++)
			{
					
				CSVBasics.add("Video "+p);};
				
				
				
				
			
		String[] CSVBasics_end={"Building instructions PDF","File Size","Description","Print Versions"};

		for (String b:CSVBasics_end){
		CSVBasics.add(b);
		
		
		};
		

		CSVBasicValues = CSVBasics.toArray(new String[CSVBasics.size()]);
		String g="";

		
		
		
		
		
		
		try {connection = Jsoup.connect(url).userAgent(USER_AGENT);
		//GetHTML browserParse=new GetHTML(url);
			currentPage= connection.get();
		
		if (connection.response().statusCode() !=200){
			System.out.println("FAILURE, HTML error:"+connection.response().statusCode()); }
		
		if (!connection.response().contentType().contains("text/html"))
			
		{
			
			System.out.println("FAILURE, Link is not of type html");
		
			
			
		}
		
	
	
		

		String productName= currentPage.select("h1[data-test=overview-name]").first().text();
		
	
		
		
		
		
		//productName=productName.substring(0,productName.indexOf(" |"));
		outputCSVBasic.put("Title",productName);
		String productCategory= currentPage.select("a[data-test=breadcrumb-link] > span").eq(1).text();
		outputCSVBasic.put("Category",productCategory);

				
		String productId=currentPage.select("dd[data-test=product-details__product-code]").first().text();
		if (productId.isEmpty())
		{productId="null";}
		System.out.println(productId);
		outputCSVBasic.put("Product ID",productId);   
  
		String productPieces;
        if (currentPage.select("dd.product-details__piece-count").first()!=null)
        {
		productPieces= currentPage.select("dd.product-details__piece-count").first().text();
		outputCSVBasic.put("Pieces",productPieces);
		productPieces="null"; }
        else{
        System.out.println("Couldn't fetch productPieces for "+url);
        productPieces="null";
       ;}
        
        
        
        String productAge;
        if (currentPage.select("dd.product-details__ages").first()!=null)
        {
		productAge= currentPage.select("dd.product-details__ages").first().text();
		outputCSVBasic.put("Ages",productAge);
		   }
        else{
        System.out.println("Couldn't fetch productAge for "+url);
        productAge="null";
       }
  
        
        
        String productVIP;
        if (currentPage.select("span.product-details__vip-points").first()!=null)
        {
		productVIP= currentPage.select("span.product-details__vip-points").first().text();
		outputCSVBasic.put("VIP Points",productVIP);
        }
        else{
        System.out.println("Couldn't fetch productVIP for "+url);
        productVIP="null";
        }
        
        
        Elements productRTBs;
        if (currentPage.select("span.product-features__description li").first()!=null)
        {
		 productRTBs= currentPage.select("span.product-features__description li");   }
        else{
        	
        System.out.println("Couldn't fetch productRTB for "+url);
        productRTBs=null;
       }

		
		int r=0;
		if (productRTBs!=null){
		for (Element z :productRTBs )
		{
		outputCSVBasic.put("rtb "+r,z.text());
		r++;;
		}}
		
		
		
		
		
		
		String productDesc;
		if (currentPage.select("span.product-features__description>p").first()!=null)
		 {productDesc= currentPage.select("span.product-features__description>p").first().text();}
		else if (currentPage.select("span.product-features__description").first()!=null)
		{productDesc= currentPage.select("span.product-features__description").first().text();}
		else
		{productDesc="null";}
	
		outputCSVBasic.put("Description",productDesc);

		
		
		
		
		
		
		//String productIMGNumbSource= Jsoup.connect("https://sh-s7-live-s.legocdn.com/is/image//LEGO/"+productId+"_mms?req=set,json,UTF-8&handler=requestS7MixedMediaSetCallback").userAgent(USER_AGENT).get().html();
		
		String productIMGNumbSource= Jsoup.connect("https://sh-s7-live-s.legocdn.com/is/image//LEGO/"+productId+"_mms?req=set,json,UTF-8&handler=requestS7MixedMediaSetCallback").userAgent(USER_AGENT).get().html();

		
		
		productIMGNumbSource="{"+productIMGNumbSource.substring(productIMGNumbSource.indexOf(("_mms\","))+6, productIMGNumbSource.length()-15);
		
		
		JSONObject imgList = new JSONObject(productIMGNumbSource);
		
		String[] productIMGIds;
        String[] productVIDIds;
        Object imgArrayPre = imgList.get("item");

		    
		    //process all images
		if (imgArrayPre instanceof JSONArray) {
			
			    JSONArray imgArray=(JSONArray)imgArrayPre;
			    
			   
			    productIMGIds=new String[imgArray.length()];
                for (int h=0; h<imgArray.length();h++)
			    {
			    JSONObject IMGid = imgArray.getJSONObject(h);
			    if(IMGid.has("type")){
			    if(IMGid.getString("type")!="video_set")//only add item to list if it's not a video
			    {
			    
			    productIMGIds[h]=(IMGid.getString("iv"));}
			    }}
		}
		
		else if (imgArrayPre instanceof JSONObject) {
		    JSONObject imgArray=(JSONObject) imgArrayPre;

			productIMGIds=new String[1];
			if(imgArray.has("type")){
		    if(imgArray.getString("type")!="video_set")//only add item to list if it's not a video
		    {
		    
		    			productIMGIds[0]=(imgArray.getString("iv"));

		    }}
			
			
			
		}
		else {
			productIMGIds=new String[0];
		}
		
		
		
		List<String> vidLinks= new LinkedList<String>();
		//process all videos
		if (imgArrayPre instanceof JSONArray) {
		 JSONArray vidArray=(JSONArray)imgArrayPre;

			for(Object k: vidArray)
			{
				JSONObject kJ= (JSONObject)k;
				if(kJ.has("type")){
			    if(kJ.getString("type").equals("video_set"))//only add item to list if it's not a video
			    {							

			    	JSONObject vidL= (JSONObject) kJ.get("s");
			    	String vidLink=vidL.getString("mod");


			    	vidLink= vidLink.substring(vidLink.indexOf("LEGO"),vidLink.length()-1);
			    	vidLinks.add(vidLink);
			       	System.out.println(vidLink);
			    }
		   }
		   
	}}
	
	else if (imgArrayPre instanceof JSONObject) {
		 JSONObject kJ=(JSONObject) imgArrayPre;
			
				if(kJ.has("type")){
			    if(kJ.getString("type").equals("video_set"))//only add item to list if it's not a video
			    {					 

			    	JSONObject vidL= (JSONObject) kJ.get("s");
			    	String vidLink=vidL.getString("mod");
					

			    	vidLink= vidLink.substring(vidLink.indexOf("LEGO"),vidLink.length()-1);
			       	System.out.println(vidLink);
			    	vidLinks.add(vidLink);
			    
			    }
		   
		   
	}
		
	}
	else {
	
	}
		
		
		
		
		
		
		
		
		productVIDIds = vidLinks.toArray(new String[vidLinks.size()]);
	 
	    
	    for (String j: productVIDIds)
	    {
	   System.out.println(j);
	    }
		

		
		
		
		int productIMGNumb=productIMGIds.length;
		System.out.println("Images: "+(productIMGNumb+1));
		List<String> IMGLinks=new LinkedList<String>();
		//add standard image
		
		IMGLinks.add("https://sh-s7-live-s.legocdn.com/is/image/LEGO/"+productId+"?id="+ productIMGIds[0]+"=jpg&fit=constrain,1&wid=2000&hei=2000&qlt=80,1&op_sharpen=0&resMode=sharp2&op_usm=1,1,6,0&iccEmbed=0&printRes=300");
		
		if (productIMGIds.length>=1)
		{
		for (int z=0; z<productIMGNumb-1;z++)
		
		{
		  
			IMGLinks.add("https://sh-s7-live-s.legocdn.com/is/image/LEGO/"+productId+"_alt"+(z+1)+"?id="+ productIMGIds[z]+"=jpg&fit=constrain,1&wid=2000&hei=2000&qlt=80,1&op_sharpen=0&resMode=sharp2&op_usm=1,1,6,0&iccEmbed=0&printRes=300"+"\n");
			
			}
		}
		else
		{System.out.println("Only one image on"+url );}
	       r=0;
			for (String z :IMGLinks )
			{
			outputCSVBasic.put("Image "+r,z);
			r++;;
			}
		

			
			
			
			int productVIDNumb=productVIDIds.length;
			System.out.println("Videos: "+(productVIDNumb));
			List<String> VIDLinks=new LinkedList<String>();
			//add standard image
						
			if (productVIDIds.length>=0)
			{
			for (int z=0; z<productVIDNumb;z++)
			
			{
			  
				VIDLinks.add("https://sh-s7-live-s.legocdn.com/is/content/"+productVIDIds[z]+"\n");
				
				}
			}
	
			
			
			
		       r=0;
				for (String z :VIDLinks )
				{
				outputCSVBasic.put("Video "+r,z);
				r++;;
				}
			
			
		
			
			
			
			
			
			
			
			
			
			
			
			
			
		
		
		
		
		//String[] productPDF= getPDF(currentPage.select("a[data-test=building-instructions]").first().attr("href"));
		String[] productPDF= getPDF("https://www.lego.com//service/biservice/search?fromIndex=0&locale=en-US&onlyAlternatives=false&prefixText="+productId);
		if (productPDF[0]!=null)
		{
		outputCSVBasic.put("Building instructions PDF",productPDF[0]);
		}

		if (productPDF[1]!=null)
		{
		outputCSVBasic.put("File Size",productPDF[1]);
		}
		
	    //System.out.println("Headers: "+Arrays.asList(outputCSVBasic));
		
		//Some additional specs

		
		/*String productPieces= currentPage.select("dd.product-details__piece-count").first().text();
		System.out.println(productPieces);
		outputCSVSpecs.add("Pieces:"+productPieces);
		*/

		
		
		List<String>outputCSV= new LinkedList<String>();
		outputCSV=completeBasicsCSV(outputCSVBasic);
		outputCSV.addAll(outputCSVSpecs);
		
		writeCSV(productId,outputCSV);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Couldn't scrap : "+url+" because connection couldn't be established: "+e.getMessage());
			if (reTrys>0){reTrys--;
			Webscrapper scrap= new Webscrapper(url,CSVwriter,rtbMax,imgMax,vidMax,reTrys);}

		
		
		
		}
		/*catch (NullPointerException e) {
			// TODO Auto-generated catch block
			System.out.println("Couldn't scrap : "+url+" because of NullPointer on some element (site has different layout), Exception: "+e.getMessage());
		}*/
	}
	
	
	private String[] getPDF(String url)
	{
		String [] pdfDATA=new String[2];
		
		 InputStream is;
		try {
			is = new URL(url).openStream();

		      BufferedReader readJSON = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		      String jsonText = readJSON.readLine();
		      if((jsonText.indexOf("\"buildingInstructions\":[")>-1)&&jsonText.indexOf("},{")>-1)
		      {
		      jsonText=jsonText.substring(jsonText.indexOf("\"buildingInstructions\":[")+24,jsonText.indexOf("},{")+1);
		      JSONObject json = new JSONObject(jsonText);
		      
		      
		      
             
		      pdfDATA[0]=json.getString("pdfLocation");
		   
		      pdfDATA[1]=json.getString("downloadSize");
		      }else
		      {pdfDATA[0]="null";
		   
		      pdfDATA[1]="null";}
		      
		  	is.close();
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
					}
		return pdfDATA;
	}
	

	private List<String> completeBasicsCSV(Hashtable<String, String> outputCSVBasic)
	{
		String h="";
		String h2="";
		List<String> output = new LinkedList<String>();
		for (String s : CSVBasicValues)
		{h+=s+":";
		
			if (outputCSVBasic.containsKey(s))
			{
				output.add("'"+outputCSVBasic.get(s)+"'");
				 h+=outputCSVBasic.get(s);
			}
			else
			{output.add("'null'");
			
		}
			
		}//System.out.println(h);
		String j="";
		for (String z: output)
		{j+=z;}
		//System.out.println(j);
		return output;
	}
	
	
	
	private void  writeCSV(String id,  List<String> outputCSV)
	{//generate BasicValues headers
		/*LinkedList<String> CSVBasicValuesHeaders=new LinkedList<String>();
		for(String s : CSVBasicValues)
		{CSVBasicValuesHeaders.add("\""+s+"\",");
	

		}*/
		
		//generate additional specs headers
		LinkedList<String> CSVSpecsValuesHeaders=new LinkedList<String>();
		
		int numSpecs=(outputCSV.toArray().length-CSVBasicValues.length);

		for(int i=0; i<numSpecs;i++){
			 CSVSpecsValuesHeaders.add("'"+"Spec "+(i+1)+"'");

		}
	
		
		
		
	
	      
	      try {

	     
	      
	      int i=0;

	     /* for(String s : CSVSpecsValuesHeaders)
			{
	    	  
	    	  if (i+1< CSVSpecsValuesHeaders.size()) //check if we are writing the last header
	    	  {
	    		  CSVwriter.write(s+",");
	    	  }
	    	  else
	    	  {   CSVwriter.write(s+"\n");
	    	  }
	    	
	    	   i++;}*/
	      
	      
	      for(String s : outputCSV)
			{
	    	  
	    	  if (i+1< outputCSV.size()) //check if we are writing the last header
	    	  {
	    		  CSVwriter.write(s+",");
	    	  }
	    	  else
	    	  {   CSVwriter.write(s+"\n");
	    	  }
	    	
	    	   i++;}
	     /* for(String s : outputCSV)
			{CSVwriter.write(s);}*/
	      
	          CSVwriter.write("");
	      }
	
	      catch (IOException e) {
				e.printStackTrace();
			}
	      
			
		
		
}
	}
	
	

