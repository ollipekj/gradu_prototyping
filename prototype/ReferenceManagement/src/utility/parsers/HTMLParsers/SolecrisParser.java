package utility.parsers.HTMLParsers;

import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	
	protected void setNewItem(String key){
		
		ItemField newItem = DataMappings.optionalFields.get(key);
		mappings.itemFields.put(key, newItem);
	}	
		
	protected void resolveConstantFields(String type){
		resolveDefaultItemFields();
		resolveType(type);
		resolveURL();
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
			log.log(Level.INFO, testable + " is not " + Constants.EX_JUFO + " or " + Constants.EX_SERIES);		
			return true;
		}
	}
	
	protected void resolveUnwantedDefaultValues(){
		excludeDefaultValue(Constants.EX_JUFO, Constants.JUFO_EDITOR);
		excludeDefaultValue(Constants.EX_SERIES, Constants.SERIES);
	}
	
	private void excludeDefaultValue(String exclude, String key){

		if(mappings.itemFields.containsKey(key)){
			String name = mappings.itemFields.get(key).getSoleType();
			String value = getValueByElementName(name);
			
			if(value.equals(exclude)){
				log.log(Level.INFO, "For exclusion:  " + value);	
				value = "";
			}			
			saveData(key, value);	
		}
	}
	
	protected void addOptionalItemField(String key){
		ItemField item = DataMappings.optionalFields.get(key);	
		setNewItem(key);
		resolveAttribute(key, item.getSoleType());
	}
	
	//If data includes DOI, the url is overwritten according to it during the filling of the JSON template
	protected void resolveURL(){
		
		Elements URLCandidates = doc.select("input[name=cr_join_fld.osoite] ~ a");
		Element urlElement = null;
		String foundURL = "";
		String key = Constants.URL;
		
		setNewItem(key);
		
		if(URLCandidates.size() > 0){
			urlElement = URLCandidates.first();	
			foundURL = urlElement.attr("href");
		}
		saveData(key, foundURL);
	}

	public void resolveType(String typeKey){
		
//		String typeIdentifier = mappings.itemFields.get(Constants.TYPE).getSoleType();
//		Element e = doc.select(createNameString(typeIdentifier)).first();
//					
		int key = Integer.parseInt(typeKey);
		String type = DataMappings.solecrisTypes.get(key);
		
		if(type != null)
			saveData(Constants.TYPE, type);
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