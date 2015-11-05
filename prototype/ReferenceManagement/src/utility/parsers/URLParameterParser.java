package utility.parsers;

import java.util.ArrayList;

public class URLParameterParser extends ParserUtilities implements UtilityParser{

	private ArrayList<ParsedDataHolder> parameterData = null;
	String addressPart = null;
	String parameterPart = null;
	StringBuilder builder = null;
	
	public String createFileImportURL(String format){
		
		builder = new StringBuilder();
		builder.append(addressPart);
		builder.append('?');
		
		if(parameterData != null){
		
			for(ParsedDataHolder p : parameterData){
				String paramKey = p.getFirst();
				if(!paramKey.equals("include") && !paramKey.equals("limit")){
					builder.append(p.getFirst());
					builder.append('=');
					builder.append(p.getSecond());
					builder.append('&');
				}
			}
			builder.append("include=" + format);
		}	
		return builder.toString();
	}
	
	public ArrayList<ParsedDataHolder> getResults(){
		return parameterData;
	}

	@Override
	public void parse(String url) {
		parameterData = new ArrayList<ParsedDataHolder>();
		addressPart = null;
		parameterPart = null;
		String[] urlParts = splitItem(url, "\\?");
		
		if(urlParts.length > 1){
			addressPart = urlParts[0];
			parameterPart = urlParts[1];
			String[] parameters = splitItem(parameterPart, "&");
			
			for(int i=0; i<parameters.length; i++){
				String[] keyValuePair = splitItem(parameters[i], "=");
				
				if(keyValuePair.length > 1){
					String key = keyValuePair[0];
					String value = keyValuePair[1];
					parameterData.add(new ParsedDataHolder(key, value));
				}
			}
		}
		
	}
}
