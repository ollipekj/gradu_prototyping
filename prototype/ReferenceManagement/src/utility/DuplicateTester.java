package utility;

import java.util.logging.Level;
import java.util.logging.Logger;

import utility.parsers.JSONparser;

public class DuplicateTester {

	boolean isNewItem;
	String itemKey = null;
	String duplicate = null;
	URLFetcher fetcher = new URLFetcher();
	JSONparser parser = new JSONparser();
	private static final Logger log  = Logger.getLogger(DuplicateTester.class.getName());
	
	public void compareData(String searchAddress, String authorKey){
		
		isNewItem = false;
		int counter = 0;
		String similiarItems = getDuplicateCandidates(searchAddress);
		counter = parser.getArrayLength(similiarItems);
		
		if(counter == 0)
			isNewItem = true;
		else{
			duplicate = parser.getValueFromArray(similiarItems, 0);
			//int version = parser.getIntValue(existingItem, "version");
	        log.log(Level.INFO, "existingItem = " + duplicate);
			itemKey = parser.getStringValue(duplicate, "key");
			log.log(Level.INFO, "itemKey = " + itemKey);
			duplicate = parser.addDataToArray(duplicate, "data", "collections", authorKey);
			
			// App engine doesn't support "PATCH" http method
//			collectionUpdate = parser.getCollectionsFromDataField(existingItem, "collections");
//			collectionUpdate = parser.addKeyValuePairToObject(collectionUpdate, "version", version);
//			collectionUpdate = parser.addKeyValuePairToObject(collectionUpdate, "key", itemKey);
//			log.log(Level.INFO, "updateString = " + collectionUpdate);
		}
	}
	
	public String getItemKey(){
		return itemKey;
	}
	
	public boolean isNewItem(){
		return isNewItem;
	}
		
	public String getUpdateString(){
		return duplicate;
	}
	
	private String getDuplicateCandidates(String url){	
		fetcher.fetch(url);
		return fetcher.getResult();
	}
}
