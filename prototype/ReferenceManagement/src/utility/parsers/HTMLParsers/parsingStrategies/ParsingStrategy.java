package utility.parsers.HTMLParsers.parsingStrategies;

import java.util.Map;

import utility.DataMappings.ItemField;

public interface ParsingStrategy {

	public void parse(String type);
	
	public Map<String, ItemField> getResults();
}
