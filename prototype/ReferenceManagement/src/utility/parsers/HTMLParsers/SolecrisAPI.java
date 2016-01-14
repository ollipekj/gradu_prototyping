package utility.parsers.HTMLParsers;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;



//import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import utility.Constants;
import utility.DataMappings;
import utility.DataMappings.ItemField;
import utility.parsers.HTMLParsers.parsingStrategies.*;


public class SolecrisAPI {
	
	private String typeValue = null;
	private ParsingStrategy parser;
	private DataMappings mappings = new DataMappings();
	private Document doc;
	private static final Logger log  = Logger.getLogger(SolecrisAPI.class.getName());
	
	private String createNameString(String name){
		return "[name=" + name + "]";
	}
	
	public Map<String, ItemField> getParsedData(){	
		if(typeValue != null)
			return parser.getResults();
		else
			return null;
	}	
	
	public String getValueByName(String name){
		return getParsedData().get(name).getValue();
	}
	
	public String getType(){
		if(typeValue != null)
			return DataMappings.solecrisTypes.get(Integer.parseInt(typeValue));
		else
			return null;
	}
	
	private boolean isOfType(String value){
		//log.log(Level.INFO, "Value: " + value + ", TypeValue: " + typeValue);
		String typeString = DataMappings.solecrisTypes.get(Integer.parseInt(typeValue));
		
		if(typeString != null){
			if(typeString.equals(value))
				return true;
			else
				return false;
		}else 
			return false;
	}
	
	private void resolveTypeValue(String typeID){
		
		Element type = doc.select(createNameString(typeID)).first();
		
		if(type != null)
			typeValue = type.attr("value");
		else
			typeValue = null;
	} 
	
	public void parseHTML(String html){
		
		doc = Jsoup.parse(html);
		resolveTypeValue(mappings.itemFields.get(Constants.TYPE).getSoleType());
		
		if(typeValue == null || typeValue == "")
			resolveTypeValue(DataMappings.optionalFields.get(Constants.OPTIONAL_TYPE).getSoleType());		
		
		//log.log(Level.INFO, "Type element: " + typeValue);
				
		if(typeValue != null && typeValue.length() != 0){

			if(isOfType(Constants.JOURNAL_ARTICLE)){		
				parser = new JournalParsingStrategy(doc);
			}else if(isOfType(Constants.BOOK) || isOfType(Constants.BOOK_SECTION)){
				parser = new MonographParsingStrategy(doc);
			}else if(isOfType(Constants.CONFERENCE_PAPER)){
				parser = new ConferencePaperParsingStrategy(doc);
			}else if(isOfType(Constants.THESIS)){
				parser = new ThesisPaperParsingStrategy(doc);
			}else
				parser = new DefaultParsingStrategy(doc);				

			parser.parse(DataMappings.solecrisTypes.get(Integer.parseInt(typeValue)));

		}else
			typeValue = null;
	}
}	