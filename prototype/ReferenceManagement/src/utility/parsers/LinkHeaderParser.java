package utility.parsers;

import java.util.ArrayList;

public class LinkHeaderParser extends ParserUtilities{

	ArrayList<ParsedDataHolder> results = null;
	
	public ArrayList<ParsedDataHolder> parseLinks(String parameter){
		
		results = new ArrayList<ParsedDataHolder>();
		String[] links;
		parameter = trimString(parameter, ']');
		parameter = trimString(parameter, '[');
		links = splitItem(parameter, ",");
		
		for(int i=0; i<links.length; i++){
			String[] parts = splitItem(links[i], ";");
			if(parts.length > 1){
				results.add(new ParsedDataHolder(trimString(parts[1], ' '), trimString(parts[0], ' ')));
			}
		}		
		
		for(ParsedDataHolder l : results){
			
			l.setSecond(trimString(l.getSecond(), '<'));
			l.setSecond(trimString(l.getSecond(), '>'));
			
			String[] parts = splitItem(l.getFirst(), "=");
			l.setFirst(trimString(parts[1], '"'));
		}
		
		return results;
	}
	
	public ArrayList<ParsedDataHolder> getResults(){
		return results;
	}

}
