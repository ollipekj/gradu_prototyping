package transferModule;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.urlfetch.HTTPMethod;

import utility.Constants;
import utility.DataMappings;
import utility.DuplicateTester;
import utility.URLFetcher;
import utility.parsers.AuthorParser;
import utility.parsers.JSONparser;
import utility.parsers.ZoteroURLBuilder;
import utility.parsers.HTMLParsers.SolecrisAPI;

public class MetadataTransferModule{
	
	int returnCode = 0;
	String publicationData = "";
	String url = null;
	String authorKey = null;
	SolecrisAPI sapi;
	JSONparser jsonHandler;
	AuthorParser authorHandler;
	ZoteroURLBuilder zapi;
	URLFetcher fetcher;
	DuplicateTester tester; 
	private static final Logger log  = Logger.getLogger(MetadataTransferModule.class.getName());
	
	public MetadataTransferModule(){
		sapi = new SolecrisAPI();
		zapi = new ZoteroURLBuilder();
		jsonHandler = new JSONparser();
		authorHandler = new AuthorParser();
		fetcher = new URLFetcher();
		tester = new DuplicateTester();
	}
	
	public void setAuthorKey(String key){
		authorKey = key;
	}
	
	public int transferData(String linkURL, String citation, String name) throws IOException{
		
		url = linkURL;
        log.log(Level.INFO, "URL = " + url);
        log.log(Level.INFO, "Author code = " + authorKey);
        
        if(url != null || authorKey != null){
        	//Get HTML data from solecris and parse it
    		fetcher.fetch(url); 
    		String htmlString = fetcher.getResult();
    		if(htmlString == "error")
    			return 601;
    		else
    			sapi.parseHTML(htmlString); 
    		
    		if(sapi.getParsedData() == null)
    			return 602; //
        	
        	String type = DataMappings.solecrisTypes.get(sapi.getType());
        	log.log(Level.INFO, "Type = " + type);
        	if(type != null){
        		//Testing if the publication is a duplicate
        		String title = sapi.getValueByName(Constants.TITLE);
        		String searchAddress = zapi.getSearchUrl(title);
        		//log.log(Level.INFO, "Searching from = " + searchAddress);

        		tester.compareData(searchAddress, authorKey);
        		String address = null;
        		HTTPMethod method = HTTPMethod.POST;

        		if(tester.isNewItem()){            
        			//Resolve the type of the publication and get corresponding JSON template from Zotero
        			fetcher.fetch(zapi.getTemplateURL(type));
        			String template = fetcher.getResult();

        			//Enter parsed values to the template
        			String authors = sapi.getValueByName(Constants.AUTHORS);
        			//log.log(Level.INFO, "authorKey = " + authorKey + ", template = " + template);
        			if(citation == null)
        				publicationData = jsonHandler.enterValues(template, authorKey, sapi.getParsedData(), authorHandler.parseAuthorString(authors, name, false));
        			else{
        				publicationData = jsonHandler.enterValues(template, authorKey, sapi.getParsedData(), authorHandler.parseAuthorString(citation, name, true));
        				log.log(Level.INFO, "Parsing authors from citation");
        			}
        			address = zapi.getSendUrl();
        		}else{
        			address = zapi.getUpdateUrl(tester.getItemKey());
        			publicationData = tester.getUpdateString();
        			method = HTTPMethod.PUT;
        		}

        		if(!publicationData.equals("error")){
        			returnCode = fetcher.sendMetadata(address, publicationData, method);
        			if(returnCode == 1)
        				returnCode = fetcher.sendMetadata(address, publicationData, method); //Try again once
        			}
        		else
        			returnCode = 601; // error in data transmission
        	}else
        		returnCode = 602;	// found publication type is null/unknown
        }else 
        	returnCode = 603;	// url or authorkey is null
        
         return returnCode;      
	}
}
