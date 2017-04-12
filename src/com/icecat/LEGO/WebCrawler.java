package webscrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
	
	private static final int MAX_PAGES_TO_CRAWL=10; 

	private Document HTMLproductList;
	public WebCrawler(String url){
    int ImgMax=0;
    int VidMax=0;
    int rtbMax=0;
	List<String> linksOnPage;	
		
		File file = new File("out.csv");
	      
	
		try
		{	file.createNewFile();
		 
	      
	        FileWriter CSVwriter = new FileWriter(file);
	     
			Connection connection=Jsoup.connect(url);
			HTMLproductList=connection.get(); 
			if (connection.response().statusCode() !=200){

		         }
			if (!connection.response().contentType().contains("text/html"))
				
			{
				
				System.out.println("FAILURE, Link is not of type html");
				
				
				
			}
			
			
			
			
			
			String page= (HTMLproductList.select("div.pagination").first().text());
			page=page.substring(page.indexOf("OF")+3,page.indexOf("NEXT"));
			System.out.println("Pages in category:"+page);
			int pages=Integer.parseInt(page);
			
			   pages=2;
		    linksOnPage=new LinkedList<String>();
		    
			for (int p=pages; p>=1;p--)
			{
			Document jSONProductCatalogues= Jsoup.connect("https://sp1004f1fe.guided.ss-omtrdc.net/?do=json-db&callback=json&count=18&S1=&cc=us&i=1&jsonp=jsonCallback&lang=en&page="+p+"&pt=shop&q=*&q1=extrasVideoGamesFacetCategory&rank=rankUS&showRetired=false&sort=sort_flags&sp_q_exact_9=us&x1=productType_id&jsonp=jsonCallback").ignoreContentType(true).get();

			String jSONProductCatalogue=jSONProductCatalogues.html();
			
			String jSONProductPre="{"+jSONProductCatalogue.substring(jSONProductCatalogue.indexOf(("\"results\":")), jSONProductCatalogue.indexOf(("\"promotions\":")))+"}";
			
			
			/*for (int i=0; i<jSONproductPre.length(); i++) {        //get rid of special characters in catalogue page json string      
				int c = (int) jSONproductPre.charAt(i);
				if (c >= 128 && c!=34) {
				nextChar = Integer.toHexString(0x10000 | c).substring(1).toUpperCase();}
				else
				{
					
					 nextChar=String.valueOf(jSONproductPre.charAt(i));
				}
				 jSONproduct+=nextChar;
				
				}
			
			
			

			JSONObject proList = new JSONObject(jSONproduct);
		    JSONArray proArray = proList.getJSONArray("results");
		    String[] productIds=new String[proArray.length()];
		    for (int u=0; u<productIds.length;u++)
		    {
		    	   JSONObject PROid = proArray.getJSONObject(u);
		    	   productIds[u]=(PROid.getString("seo_path"));}
		
		    
			*/
		    List<String> productIds=new LinkedList<String>();
//extract links out of json
			int lastPath=jSONProductPre.lastIndexOf("seo_path");
			for (int d= jSONProductPre.indexOf("seo_path");d>= 0; d = jSONProductPre.indexOf("seo_path", d + 1))
			
			{				//System.out.println((jSONProductPre.substring(d+12,jSONProductPre.indexOf("\",", d + 1))));

				productIds.add((jSONProductPre.substring(d+12,jSONProductPre.indexOf("\",", d + 1))));
			}
			
			
			
			
		    int o=0;
		    for(String j: productIds)
		    {
		    
		    	
		    	linksOnPage.add("https://shop.lego.com/en-US/"+j);
		    	o++;
		    }

			}
			
			
			
			
		
			int w=0;
			
			String CSVBasicValues_begin[]= {"Title","Product ID","Ages","Category","Pieces","VIP Points"};
			LinkedList<String> CSVBasicValuesHeaders_begin=new LinkedList<String>();
			
			for(int s =0; s<CSVBasicValues_begin.length;s++)
			{CSVBasicValuesHeaders_begin.add("'"+CSVBasicValues_begin[s]+"',");}
			
			String CSVBasicValues_end[]= {"Building instructions PDF","File Size","Description","Print Versions"};
			LinkedList<String> CSVBasicValuesHeaders_end=new LinkedList<String>();
			
			for(int q =0; q<CSVBasicValues_end.length;q++)
			{CSVBasicValuesHeaders_end.add("'"+CSVBasicValues_end[q]+"',");
			}	
			
			
			//write every header before first rtb
			//CSVBasicValuesHeaders_begin.add("\""+CSVBasicValues_begin[CSVBasicValues_begin.length-1]+"\" ");
			 for(String u : CSVBasicValuesHeaders_begin)
				{ 		      

		    	  CSVwriter.write(u);
		    	  
				}  
			 
			 
			 
		  
		     
		      System.out.println("researching needed headers..");
		     /* 
		      for(String link: linksOnPage)
				{
		    	  
					preScrapp preScrap= new preScrapp(link);
					int[] IMGRTB= preScrap.getIMGRtb();
					if(IMGRTB[0]>ImgMax)
					{
						ImgMax=IMGRTB[0];
						
					}
					if(IMGRTB[1]>rtbMax)
					{
						rtbMax=IMGRTB[1];
					}
				}
		      *
		     */
				ImgMax=36;
				VidMax=4;
                rtbMax=23;
		      System.out.println("max Images: "+ImgMax);
		      System.out.println("max rtb: "+rtbMax);

		      

		      for (int l=0; l<rtbMax;l++)
		      {

			      CSVwriter.write("'rtb "+l+"',");

		      
		      }
		      
		      
		      for (int l=0; l<ImgMax;l++)
		      {
			      CSVwriter.write("'Image "+l+"',");

		      
		      }
		      
		      for (int l=0; l<VidMax;l++)
		      {
			      CSVwriter.write("'Video "+l+"',");

		      
		      }
		      
		      
		      
		  	//write every header after last image header
				//CSVBasicValuesHeaders_end.add("\""+CSVBasicValues_end[CSVBasicValues_end.length-1]+"\" ");
				
				
				for(String u : CSVBasicValuesHeaders_end)
					{ 
			    	  CSVwriter.write(u);
					}  
				 
		      
		      CSVwriter.write(" \n");
			
		      
		      
		      
		      
		      
			for(String link: linksOnPage)
			{
				//if (w<MAX_PAGES_TO_CRAWL)
				{
				Webscrapper scrap= new Webscrapper(link,CSVwriter,rtbMax,ImgMax,VidMax,2);
				w++;
				}
				}
			
			/*
			boolean allDone=false;

			while(!allDone)
			{
			try
				{
				
				if (nextPage!=null)
		    	{
					
		    	Connection con=Jsoup.connect(nextPage);
			    Document HTMLproductList=con.get();
			    Response response = Jsoup.connect(nextPage).followRedirects(true).execute();
				
					if (con.response().statusCode() !=200){

				         }
					if (!con.response().contentType().contains("text/html"))
						
					{
						
						System.out.println("FAILURE, Link is not of type html");
						
						
						
					}
		    
			linksOnPage=HTMLproductList.select("a[data-test=product-leaf-link-image]");
			for(Element link: linksOnPage)
			{
				//if (w<MAX_PAGES_TO_CRAWL)
				{
				Webscrapper scrap= new Webscrapper(link.absUrl("href"),CSVwriter);
				w++;
				}
				}
			try{
			    PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
			    writer.println(HTMLproductList.html());

			    writer.close();
			} catch (IOException e) {
			   // do something
			}
			System.out.println(response.url());
			}
			else
			{
				allDone=true;
			}	
			
			}
			catch (IOException ioe)
			{
				System.out.println("HTTP request error: "+ioe);
				
			}
		
	}*/
			
		CSVwriter.flush();
		      CSVwriter.close();
		
		}
			
		catch (IOException ioe)
		{
			System.out.println("HTTP request error: "+ioe);
			
		}  

}
}
