package utility.parsers.HTMLParsers.parsingStrategies;

import java.util.Map;

import org.jsoup.nodes.Document;

import utility.DataMappings.ItemField;
import utility.parsers.HTMLParsers.SolecrisParser;

public class DefaultParsingStrategy extends SolecrisParser implements ParsingStrategy {

	public DefaultParsingStrategy(Document d){
		super.doc= d;
	}

	@Override
	public void parse(String type) {
		resolveConstantFields(type);
		resolveOptionalFields();
	}
	
	private void resolveOptionalFields(){}

	@Override
	public Map<String, ItemField> getResults() {
		return mappings.itemFields;
	}
}
