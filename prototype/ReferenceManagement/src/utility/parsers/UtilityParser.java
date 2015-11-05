package utility.parsers;

import java.util.ArrayList;

import utility.parsers.ParserUtilities.ParsedDataHolder;

public interface UtilityParser {
	public void parse(String parameter);
	public ArrayList<ParsedDataHolder> getResults();
}
