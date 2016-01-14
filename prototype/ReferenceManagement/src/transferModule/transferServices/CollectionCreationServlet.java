package transferModule.transferServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.urlfetch.HTTPMethod;

import utility.URLFetcher;
import utility.ZoteroURLBuilder;
import utility.parsers.JSONparser;

public class CollectionCreationServlet extends HttpServlet{
	
	ZoteroURLBuilder urlBuilder = new ZoteroURLBuilder(); 
	URLFetcher fetcher = new URLFetcher();
	JSONparser jsonParser = new JSONparser();
	private static final Logger log  = Logger.getLogger(SingleItemServlet.class.getName());
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{

		String items = req.getParameter("items");

		for(int i=0; i<jsonParser.getArrayLength(items); i++){
			
			String data = jsonParser.getValueFromArray(items, i);
			String authorKey = jsonParser.getStringValue(data, "authorKey");
			String authorName = jsonParser.getStringValue(data, "authorName");

			if(authorKey != null){

				fetcher.sendStatusUpdate("Starting collection initialization " + authorName);
				//createCollection("Publications" , authorKey);	

				String createdCollection = "Metainfo";

				if(!collectionExists(authorKey, createdCollection)){
					createCollection(createdCollection , authorKey);	
				}else
					fetcher.sendStatusUpdate("Collection " + createdCollection + " already exists");

				fetcher.sendStatusUpdate("Collection initialization complete for " + authorName);
			}			
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{		
		
		String items = req.getParameter("items");
		log.log(Level.INFO, items);
		String data = jsonParser.getValueFromArray(items, 0);
		String authorKey = jsonParser.getStringValue(data, "authorKey");
		String authorName = jsonParser.getStringValue(data, "authorName");
		
		if(authorKey != null){
			
			fetcher.sendStatusUpdate("Starting collection deletion procedure for " + authorName);
			//createCollection("Publications" , authorKey);	
			
			fetcher.fetch(urlBuilder.getSubCollectionsURL(authorKey));
			String collectionInfo = fetcher.getResult();
			String keys = "";
			int length = jsonParser.getArrayLength(collectionInfo);
			
			for(int i=0; i<length; i++){
				
				String entry = jsonParser.getValueFromArray(collectionInfo, i);
				String collectionKey = jsonParser.getStringFromDataField(entry, "key");
				keys += collectionKey + ",";			
			}	
			keys = keys.substring(0, keys.length()-1);
			log.log(Level.INFO, keys);
			int statusCode = fetcher.sendMetadata(urlBuilder.getCollectionDeleteURL(keys), "", HTTPMethod.DELETE).getResponseCode();
			fetcher.sendStatusUpdate("Remove status " + statusCode);
			fetcher.sendStatusUpdate("Collection initialization complete for " + authorName);
		}			
	}
	
	private boolean collectionExists(String authorKey, String name){
		
		fetcher.fetch(urlBuilder.getSubCollectionsURL(authorKey));
		String collectionInfo = fetcher.getResult();
		
		for(int i=0; i<jsonParser.getArrayLength(collectionInfo); i++){
			
			String entry = jsonParser.getValueFromArray(collectionInfo, i);
			String collectionName = jsonParser.getStringFromDataField(entry, "name");
			
			//log.log(Level.INFO, name);
			if(collectionName.equals(name)){
				return true;
			}
		}	
		return false;
	}
	
	private void createCollection(String collectionName, String parentCollection){
		
		String url = urlBuilder.getCollectionCreationURL();
		String data = jsonParser.getCollectionCreationString(collectionName, parentCollection);
		int statusCode = fetcher.sendMetadata(url, data, HTTPMethod.POST).getResponseCode();		
		fetcher.sendStatusUpdate(collectionName + ": " + Integer.toString(statusCode));
	}
}
