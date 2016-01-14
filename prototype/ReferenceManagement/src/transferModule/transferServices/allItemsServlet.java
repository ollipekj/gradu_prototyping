package transferModule.transferServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import transferModule.MetadataTransferModule;
import utility.ResponseFormatter;
import utility.TransferStatus;
import utility.URLFetcher;
import utility.ZoteroURLBuilder;
import utility.parsers.JSONparser;
import utility.parsers.PublistParser;
import utility.parsers.UtilityParser;
import utility.parsers.ParserUtilities.ParsedDataHolder;

public class allItemsServlet extends HttpServlet {
	
	private int incompleteLinks;
	private UtilityParser pubListParser;
	private ResponseFormatter responseFormatter;
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
		responseFormatter = new ResponseFormatter();
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
				xmlFileUrl = resolvePubListURL(xmlFileUrl);
				log.log(Level.INFO, xmlFileUrl);
				
				fetcher.sendStatusUpdate("<br>Starting data transfer for " + authorName);
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
	
	private String resolvePubListURL(String url){
		
		String result = null;
		fetcher.fetch(url);
		String data = fetcher.getResult();
		int length = jsonParser.getArrayLength(data);
		log.log(Level.INFO, "resolving publist" + data);
		if(data != null && data.length() > 4){
			//data = jsonParser.getValueFromArray(data, 0);
			
			for(int i=0; i<length; i++){
				
				String entry = jsonParser.getValueFromArray(data, i);
				String name = jsonParser.getStringFromDataField(entry, "name");
				String key = jsonParser.getStringFromDataField(entry, "key");
								
				if(name.equals("Metainfo")){
					url = zapi.getXmlFileUrl(key);
					fetcher.fetch(url);
					result = fetcher.getResult();
					if(result != null && result.length() > 4){
						entry = jsonParser.getValueFromArray(result, 0);
						result = jsonParser.getStringFromDataField(entry, "name");	
						log.log(Level.INFO, "Data: " + data);
					}
				}else 
					result = null;
			}			
			return result;
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
		
		int count = 1;
		
		for(ParsedDataHolder item : items){
			try {
				String link = item.getFirst();
				String citation = item.getSecond();
				
				if(link != null){
					TransferStatus status = transferer.transferData(link, citation, authorName);
					status.setCount(count);
					status.setSoleLink(link);  
					fetcher.sendStatusUpdate(responseFormatter.createHTMLFormattedResponse(status));
				}else{
					TransferStatus status = new TransferStatus();
					status.setCount(count);
					status.setCitation(item.getSecond());
					fetcher.sendStatusUpdate(responseFormatter.createIncompleteLinkNotification(status));
				}					
								
			} catch (IOException e) {
				e.printStackTrace();
			}		
			count++;
		}
	}
}
