package transferModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.urlfetch.HTTPMethod;

import utility.Constants;
import utility.TransferStatus;
import utility.URLFetcher;
import utility.ZoteroURLBuilder;
import utility.DataMappings.ItemField;
import utility.parsers.AuthorParser;
import utility.parsers.JSONparser;
import utility.parsers.ParserUtilities.ParsedDataHolder;
import utility.parsers.HTMLParsers.SolecrisAPI;

public class MetadataTransferModule{
	
	TransferStatus returnStatus;
	int authorCount = 0;
	String parsingStatus;
	String title;
	String publicationData = "";
	String url = null;
	String authorKey = null;
	String address;
	SolecrisAPI sapi;
	JSONparser jsonParser;
	AuthorParser authorParser;
	ZoteroURLBuilder zapi;
	URLFetcher fetcher;
	DuplicateTester tester; 
	private static final Logger log  = Logger.getLogger(MetadataTransferModule.class.getName());
	
	public MetadataTransferModule(){
		sapi = new SolecrisAPI();
		zapi = new ZoteroURLBuilder();
		jsonParser = new JSONparser();
		authorParser = new AuthorParser();
		fetcher = new URLFetcher();
		tester = new DuplicateTester();
	}
	
	public void setAuthorKey(String key){
		authorKey = key;
	}
	
	public TransferStatus transferData(String linkURL, String citation, String name) throws IOException{
		
		title = "";
		parsingStatus = "";
		url = linkURL;
        log.log(Level.INFO, "URL = " + url);
        log.log(Level.INFO, "Author code = " + authorKey);
        
        if(url != null || authorKey != null){
        	//Get HTML data from solecris and parse it
    		fetcher.fetch(url); 
    		String htmlString = fetcher.getResult();
    		if(htmlString == "error")
    			return new TransferStatus(601, "", "");
    		else
    			sapi.parseHTML(htmlString); 
    		
    		if(sapi.getParsedData() == null)
    			return new TransferStatus(602, "", "");
        	
        	String type = sapi.getType();
        	//log.log(Level.INFO, "Type = " + type);
        	if(type != null){
        		address = null;
        		
        		//Testing if the publication is a duplicate
        		title = sapi.getValueByName(Constants.TITLE);
        		String searchAddress = zapi.getSearchUrl(title);
        		//log.log(Level.INFO, "Searching from = " + searchAddress);
        		tester.compareData(searchAddress, authorKey);        		
        		HTTPMethod method = HTTPMethod.POST;

        		if(tester.isNewItem()){            
        			
        			//Resolve the type of the publication and get corresponding JSON template from Zotero
        			fetcher.fetch(zapi.getTemplateURL(type));
        			String template = fetcher.getResult();
        			
        			//Enter parsed values to the template
        			String authors = sapi.getValueByName(Constants.AUTHORS);
        			try{
        				authorCount = Integer.parseInt(sapi.getValueByName(Constants.AUTHOR_COUNT));
        			}catch(NumberFormatException e){
        				authorCount = 0;
        			}
        			
        			ArrayList<ParsedDataHolder> parsedAuthors = null;
        		
        			if(citation == null){
        				parsedAuthors = authorParser.parseAuthorString(authors, name, authorCount, false);        			
        			}
        			else{
        				parsedAuthors = authorParser.parseAuthorString(citation, name, authorCount, true);  
        				log.log(Level.INFO, "Parsing authors from citation");
        			}
        			
        			resolveAuthorParsingStatus();
      				publicationData = enterValues(template, authorKey, sapi.getParsedData(), parsedAuthors);
        			address = zapi.getSendUrl();
        		}else{
        			address = zapi.getUpdateUrl(tester.getItemKey());
        			publicationData = tester.getUpdateString();
        			method = HTTPMethod.PUT;
        		}

        		if(!publicationData.equals("error")){
        			returnStatus = fetcher.sendMetadata(address, publicationData, method);
        			if(returnStatus.getResponseCode() == 1)
        				returnStatus = fetcher.sendMetadata(address, publicationData, method); //Try again once
        			}
        		else
        			returnStatus.setResponseCode(601); // error in data transmission
        	}else
        		returnStatus.setResponseCode(700);	// found publication type is null/unknown
        }else 
        	returnStatus.setResponseCode(603);	// url or authorkey is null
        
        returnStatus.setAuthorParsingStatus(parsingStatus);
        returnStatus.setTitle(title);
        
        return returnStatus;   
	}
	
	public String enterValues(String template, String authorKey, Map<String, ItemField> values, ArrayList<ParsedDataHolder> authors){

		template = jsonParser.addDataToArray(template, "", "collections", authorKey);
		//jsonTemplate = new JSONObject(template);
		//log.log(Level.INFO, "Template: " + template);
		for(Entry<String, ItemField> entry : values.entrySet())
		{
			String key = entry.getValue().getZoteroType();
			String value =  entry.getValue().getValue();
			//log.log(Level.INFO, "key: " + key + ", value: " + value);
			switch(key){
			case "creators":
				template = jsonParser.addArrayToObject(template, key, jsonParser.authorsToJson(authors));
				//jsonTemplate.put(key , jsonParser.authorsToJson(authors));
				break;
			case "date": //Save the 'date' value also to the 'tag' field to give more searching options
				template = jsonParser.addDataToArray(template, "", "tags", value);
				template = jsonParser.enterValueToTemplate(template, key, value);
				break;
			case "DOI": // Data might have doi but not url defined. Url is generated from doi just in case
				if(value.length() > 3){ //Doi field might include characters such as '-' to indicate no DOI. Ignore them.
					template = jsonParser.enterValueToTemplate(template, "url", jsonParser.createDOIUrl(value));
					template = jsonParser.enterValueToTemplate(template, key, value);
				}
				break;
			default:
				template = jsonParser.enterValueToTemplate(template, key, value);
			}
		}		
		return "[" + template.toString() + "]";
	}
	
	private void resolveAuthorParsingStatus(){
		if(!authorParser.isRightAuthorCount()){
//			if(authorCount == 0)
//				parsingStatus += "; The number of authors was not stated in the original data";
//			else
			if(authorCount != 0)
				parsingStatus += "; Number of saved authors did not match the original data";
		}
		if(!authorParser.isNameIdentified()){
			parsingStatus += "; There may be a misspelling in the author names";
		}
	}
	

}
