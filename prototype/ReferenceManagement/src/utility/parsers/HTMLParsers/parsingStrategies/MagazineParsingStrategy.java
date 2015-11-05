package utility.parsers.HTMLParsers.parsingStrategies;

import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utility.Constants;
import utility.DataMappings.ItemField;
import utility.parsers.HTMLParsers.SolecrisParser;

public class MagazineParsingStrategy extends SolecrisParser implements ParsingStrategy {

	public MagazineParsingStrategy(Document d){
		super.doc= d;
	}
	
	@Override
	public void parse(String type) {
		resolveConstantFields(type);
		resolveOptionalFields();
	}
	
	private void resolveOptionalFields(){
		
		Elements pubCandidates = super.doc.select("a");
		
		for(Element e : pubCandidates){
			String link = e.attr("href");
			if(link.toLowerCase().contains("cris_lehti".toLowerCase())){
				String key = Constants.MAGAZINE_TITLE;
				setNewItem(key);
				saveData(key, e.text());
				//log.log(Level.INFO, "Saved magazine:  " + e.text());	
			}
		}
	}

	@Override
	public Map<String, ItemField> getResults() {
		return mappings.itemFields;
	}
}
