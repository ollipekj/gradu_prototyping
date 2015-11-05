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
import utility.parsers.HTMLParsers.parsingStrategies.BookParsingStrategy;
import utility.parsers.HTMLParsers.parsingStrategies.ConferencePaperParsingStrategy;
import utility.parsers.HTMLParsers.parsingStrategies.DefaultParsingStrategy;
import utility.parsers.HTMLParsers.parsingStrategies.MagazineParsingStrategy;
import utility.parsers.HTMLParsers.parsingStrategies.ParsingStrategy;

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
	
	public int getType(){
		return Integer.parseInt(typeValue);
	}
	
	private boolean isOfType(String value){
		log.log(Level.INFO, "Value: " + value + ", TypeValue: " + typeValue);
		if(value.length() == 0)
			return false;
		
		if(DataMappings.solecrisTypes.get(Integer.parseInt(typeValue)).equals(value))
			return true;
		else
			return false;
	}
	
	public void parseHTML(String html){
		
		doc = Jsoup.parse(html);
		Element type = doc.select(createNameString(mappings.itemFields.get(Constants.TYPE).getSoleType())).first();
		typeValue = type.attr("value");

		if(typeValue.length() == 0){
			type = doc.select(createNameString(mappings.itemFields.get(Constants.OPTIONAL_TYPE).getSoleType())).first();
			typeValue = type.attr("value");
		}		
		log.log(Level.INFO, "Type element: " + type.toString());
				
		if(typeValue.length() != 0 && typeValue != null){

			if(isOfType(Constants.MAGAZINE_ARTICLE) || isOfType(Constants.JOURNAL_ARTICLE)){		
				parser = new MagazineParsingStrategy(doc);
			}else if(isOfType(Constants.BOOK) || isOfType(Constants.BOOK_SECTION)){
				parser = new BookParsingStrategy(doc);
			}else if(isOfType(Constants.CONFERENCE_PAPER)){
				parser = new ConferencePaperParsingStrategy(doc);
			}else
				parser = new DefaultParsingStrategy(doc);				

			parser.parse(typeValue);

		}else
			typeValue = null;
	}
}	