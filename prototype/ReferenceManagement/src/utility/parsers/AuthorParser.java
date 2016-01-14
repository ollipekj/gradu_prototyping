package utility.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class AuthorParser extends ParserUtilities {

	private String authors;
	public String authorSurname;
	private int authorCount;
	private ArrayList<ParsedDataHolder> names;
	private StringBuilder builder;
	private HashMap<String, String> codes;
	private HashMap<String, String> replaceableStrings;
	private boolean nameIdentified;
	
	public AuthorParser(){
		codes = new HashMap<String, String>();
		replaceableStrings = new HashMap<String, String>();

		codes.put("&#305;", "ı");
		codes.put("&#351;", "ş");
		codes.put("&uuml;", "ü");
		codes.put("&amp;", "&");	
		codes.put("&ouml;", "ö");	
		codes.put("&auml;", "ä");
		codes.put("&iacute;", "í");
		
		replaceableStrings.put("\\(eds\\.\\)", "");
	}
	
	public ArrayList<ParsedDataHolder> parseAuthorString(String authorString, String name, int count, boolean isFullCitation) {

		authors = authorString;		
		names = new ArrayList<ParsedDataHolder>();
		builder = new StringBuilder();
		authorCount = count;
		nameIdentified = false;
		
		resolveAuthorSurname(name);
		
		if(isFullCitation)
			authors = splitNamesFromCitation(authors);
		
		authors = replaceCharacterCode(authors);
		splitToAuthors(authors);
		formatNames();
		checkForInvertedNames();
		
		return names;
	}
	
	public boolean isRightAuthorCount(){
		if(names.size() == authorCount)
			return true;
		else 
			return false;
	}
	
	public boolean isNameIdentified(){
		return nameIdentified;
	}
	
	private String splitNamesFromCitation(String citation){
		
		int end = 0;
		
		for( int i=0; i<citation.length(); i++ ) {
		    if( citation.charAt(i) == '(' ) {
		        end = i;
		        break;
		    } 
		}
		return citation.substring(0,end);
	}
	
//	private void resolveAuthorSurname(String name){
//		
//		String[] aut;
//		aut = name.split(" ");
//		
//		if(aut.length > 0){
//			authorSurname = aut[aut.length-1];
//			trimString(authorSurname);
//		}
//		else authorSurname = null;		
//	}
	
	public void resolveAuthorSurname(String name){
		
		String[] aut;
		aut = name.split(",");
		
		if(aut.length > 0){
			authorSurname = aut[0];
			trimString(authorSurname, ',');
		}
		else authorSurname = null;		
		//System.out.println(authorSurname);
	}
	
	
	private String replaceCharacterCode(String name){

		for(Entry<String, String> code : codes.entrySet()){

			if(name.contains(code.getKey().toString()))
				name = name.replaceAll(code.getKey().toString(), code.getValue().toString());
		}

		for(Entry<String, String> replaceable : replaceableStrings.entrySet()){

			name = name.replaceAll(replaceable.getKey().toString(), replaceable.getValue().toString());
		}
		return name;
	}

	private void checkForInvertedNames(){
		for(ParsedDataHolder h : names){
			
			if(h.getSecond().equals(authorSurname))
				nameIdentified = true;
			
			if(h.getFirst().equals(authorSurname)){
				invertNames();
				nameIdentified = true;
				break;
			}
		}
	}
	
	private void invertNames(){
		
		for(ParsedDataHolder h : names){
			String temp = h.getFirst();
			h.setFirst(h.getSecond());
			h.setSecond(temp);
		}
	}
	
	private void splitToAuthors(String authorString) {
		
		String[] aut;
		String splitter = "";
		
		if(!hasSemicolon(authorString)){
			aut = authorString.split(", and | & | and |,| y ");
			splitter = " ";
		}
		else{
			aut = authorString.split("; | & | and ");
			if(hasColon(authorString))
				splitter = ", ";
			else
				splitter = " ";
		}
		if(authorCount == 1){
			if(aut.length > 1)
				saveItem(aut[0], aut[1]);
		}else
			splitNames(aut, splitter);
	}
	
	private boolean hasSemicolon(String testable){
		if(testable.contains(";"))
			return true;
		else 
			return false;
	}
	
	private boolean hasColon(String testable){
		if(testable.contains(","))
			return true;
		else 
			return false;
	}
	
	private boolean hasSpace(String testable){
		if(testable.contains(" "))
			return true;
		else 
			return false;
	}
	
	private String getSeparatorCharacter(String testable){
		if(hasColon(testable))
			return ",";
		else if(hasSemicolon(testable))
			return ";";
		else if(hasSpace(testable))
			return " ";
		else 
			return "-";
	}

	private void splitNames(String[] names, String rx){

		boolean noSplits = true;

		for(int i=0; i<names.length; i++){
			String NameElement = names[i];
			if(NameElement.length() > 0){
				NameElement = trimString(NameElement);
				String[] resolvedNames = NameElement.split(rx);

				if(resolvedNames.length == 1){
					String separator = getSeparatorCharacter(resolvedNames[0]);
					//System.out.println(temp[0] + " >"+separator+"<");
					if(separator != "-"){
						
						String[] result = resolvedNames[0].split(separator);
						//System.out.println(result.length + " >"+separator+"<");
						splitNames(result, separator);
					}
				}
				if(resolvedNames.length == 2){
					noSplits = false;
					saveItem(resolvedNames[0], resolvedNames[1]);
				}else if(resolvedNames.length >= 3){
					noSplits = false;
					builder.delete(0, builder.length());
					for(int j=1; j<resolvedNames.length; j++){
						builder.append(resolvedNames[j] + " ");
					}
					saveItem(resolvedNames[0], builder.toString());
				}
			}
		}	
		
		if(noSplits == true){
			for(int i=0; i<names.length; i=i+2){
				if(names.length >= (i+2)){
					saveItem(names[i], names[i+1]);
				}
			}
		}
	}

	private void saveItem(String f, String l){

		names.add(new ParsedDataHolder(l, f));		
	}

	private void formatNames(){

		for (ParsedDataHolder entry : names)
		{
			String f = entry.getFirst();
			String l = entry.getSecond();
			
			entry.setFirst(trimString(f));
			entry.setSecond(trimString(l));
		}		
	}
}
