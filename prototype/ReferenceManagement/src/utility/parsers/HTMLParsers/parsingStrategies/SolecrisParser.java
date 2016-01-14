package utility.parsers.HTMLParsers.parsingStrategies;

import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utility.Constants;
import utility.DataMappings;
import utility.DataMappings.ItemField;


public class SolecrisParser {
	
	protected DataMappings mappings = new DataMappings();
	protected Document doc;
	private static final Logger log  = Logger.getLogger(SolecrisParser.class.getName());
			
	public SolecrisParser(){
		
	}
	
	private String createNameString(String name){
		return "[name=" + name + "]";
	}	
	
	protected void saveData(String key, String value){
		
		if(notInExcludeList(value))
			mappings.itemFields.get(key).setValue(value);
	}
	
	protected void includeOptionalItem(String key){
		
		ItemField newItem = DataMappings.optionalFields.get(key);
		mappings.itemFields.put(key, newItem);
	}	
		
	protected void resolveConstantFields(String type){
		resolveDefaultItemFields();
		resolveType(type);
		resolveURL();
		resolveLanguage();
		//resolveUnwantedDefaultValues();
	}
	
	protected void resolveDefaultItemFields(){
		
		for (Entry<String, ItemField> entry : mappings.itemFields.entrySet())
		{			
			String key = entry.getKey();
			String value = entry.getValue().getSoleType();

			resolveAttribute(key, value);
		}
	}
	
	private boolean notInExcludeList(String testable){
		
		if(testable.equals(Constants.EX_JUFO) || testable.equals(Constants.EX_SERIES)){
			return false;		
		}
		else{
			return true;
		}
	}
	
	protected void addOptionalItemField(String key){
		ItemField item = DataMappings.optionalFields.get(key);	
		includeOptionalItem(key);
		resolveAttribute(key, item.getSoleType());
	}
	
	//If data includes DOI, the url is overwritten according to it during the filling of the JSON template
	protected void resolveURL(){
		
		Elements URLCandidates = doc.select("input[name=cr_join_fld.osoite] ~ a");
		Element urlElement = null;
		String foundURL = "";
		String key = Constants.URL;
		
		includeOptionalItem(key);
		
		if(URLCandidates.size() > 0){
			urlElement = URLCandidates.first();	
			foundURL = urlElement.attr("href");
		}
		saveData(key, foundURL);
	}
	
	protected void resolveLanguage(){
		
		String key = Constants.LANGUAGE;		
		includeOptionalItem(key);

        String name = DataMappings.optionalFields.get(Constants.LANGUAGE).getSoleType();
		name = "input[name=" + name + "]";
		try{
			Element els = doc.select(name).first().parent();
			String language = els.text();

			saveData(key, language);
		}catch(NullPointerException e){}
	}

	public void resolveType(String typeKey){
		
		if(typeKey != null)
			saveData(Constants.TYPE, typeKey);
	}
	
	private String getValueByElementName(String name){
		
		String data = "";
		Element e = doc.select(createNameString(name)).first();
		
		if(e != null)
			data = e.attr("value");
		
		return data;
	}
	
	protected void resolveAttribute(String key, String value){
		
		String data = getValueByElementName(value);
		
		//log.log(Level.INFO, "Saving to:  " + key + ", " + data);	
		saveData(key, data);
	}	
	
}