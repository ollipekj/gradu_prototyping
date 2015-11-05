package transferModule.transferServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import transferModule.MetadataTransferModule;
import utility.URLFetcher;
import utility.parsers.JSONparser;
import utility.parsers.PublistParser;
import utility.parsers.UtilityParser;
import utility.parsers.XMLparser;
import utility.parsers.ZoteroURLBuilder;
import utility.parsers.ParserUtilities.ParsedDataHolder;

public class allItemsServlet extends HttpServlet {
	
	private int incompleteLinks;
	private UtilityParser pubListParser;
	private JSONparser jsonParser;
	private ZoteroURLBuilder zapi;
	private ArrayList<ParsedDataHolder> items;
	private MetadataTransferModule transferer;
	private URLFetcher fetcher; 
	
	private static final Logger log  = Logger.getLogger(allItemsServlet.class.getName());
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
			
		String data = req.getParameter("items");
		
		pubListParser = new PublistParser();
		jsonParser = new JSONparser();
		zapi = new ZoteroURLBuilder();
		transferer = new MetadataTransferModule();
		fetcher = new URLFetcher();
		
		log.log(Level.INFO, "data = " + data);
		
		if(data != null){
			int length = jsonParser.getArrayLength(data);
			String tempObject = null;
			String authorKey = null;
			String authorName = null;
			String xmlFileUrl = null;

			incompleteLinks = 0;
			items = new ArrayList<ParsedDataHolder>();
			
			for(int i=0; i<length; i++){
				tempObject = jsonParser.getValueFromArray(data, i);
				authorKey = jsonParser.getStringValue(tempObject, "authorKey");
				authorName = jsonParser.getStringValue(tempObject, "authorName");
				xmlFileUrl = zapi.getXmlFileUrl(authorKey);
				xmlFileUrl = getValue(xmlFileUrl, "data", "name");
				//log.log(Level.INFO, xmlFileUrl);
				
				fetcher.sendStatusUpdate("Starting data transfer for " + authorName);
				if(xmlFileUrl != null){					
					transferer.setAuthorKey(authorKey);
					getItems(xmlFileUrl);
					
					if(items != null){
						fetcher.sendStatusUpdate("List of publication page URLs populated. total links:" + items.size() + ", Incomplete URLs enountered: " + incompleteLinks);
						transferItems(authorName);
	
						fetcher.sendStatusUpdate("Transfer complete");
						log.log(Level.INFO, "Transfer complete");
					}else
						fetcher.sendStatusUpdate("Error fetching publication list");
				}else
					fetcher.sendStatusUpdate("No publication list link found");
			}
		}
	}
	
//	private int resolveCurrentNumberOfItems(String authorKey){
//		
//		String url = zapi.getCollectionInfo(authorKey);
//		return Integer.parseInt(getValue(url, "meta", "numItems"));
//	}
	
	private String getValue(String url, String parent, String child){
		
		fetcher.fetch(url);
		String data = fetcher.getResult();
		log.log(Level.INFO, data);
		if(data != null && data.length() > 4){
			data = jsonParser.getValueFromArray(data, 0);
			data = jsonParser.getObjectByKey(data, parent);
			data = jsonParser.getStringValue(data, child);	
			return data;
		}else
			return null;				
	}
	
	//Doesn't add incomplete URLs 
	private void getItems(String url) throws IOException{

		pubListParser.parse(url);
		
		if(pubListParser.getResults() != null){
			items = pubListParser.getResults();
			log.log(Level.INFO, items.toString());
			for(ParsedDataHolder item : items){
				if(item.getFirst().equals("https://solecris.oulu.fi/crisyp")){
					item.setFirst(null);
					incompleteLinks++;
				}				
			}
		}else
			items = null;
	}
	
	private void transferItems(String authorName){
		
		int returnCode = 0;
		int count = 1;
		
		for(ParsedDataHolder item : items){
			try {
				String link = item.getFirst();
				String citation = item.getSecond();
				
				if(link != null){
					returnCode = transferer.transferData(link, citation, authorName);
					fetcher.sendStatusUpdate(count + ". Status from " + link + ": " + returnCode);
				}else{
					fetcher.sendStatusUpdate(count + ". Missing link for an article requires manual input: " + item.getSecond());
				}					
								
			} catch (IOException e) {
				e.printStackTrace();
			}		
			count++;
		}
	}
}
