package utility.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class AuthorParser extends ParserUtilities {

	private String authors;
	public String authorSurname;
	private ArrayList<ParsedDataHolder> names;
	private StringBuilder builder;
	private HashMap<String, String> codes;
	private HashMap<String, String> replaceableStrings;
	
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
	
	public ArrayList<ParsedDataHolder> parseAuthorString(String authorString, String name, boolean isFullCitation) {

		authors = authorString;		
		names = new ArrayList<ParsedDataHolder>();
		builder = new StringBuilder();
		
		resolveAuthorSurname(name);
		
		if(isFullCitation)
			authors = splitNamesFromCitation(authors);
		
		authors = replaceCharacterCode(authors);
		splitToAuthors();
		formatNames();
		checkForInvertedNames();
		
		return names;
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
			
			if(h.getFirst().equals(authorSurname)){
				invertNames();
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
	
	private void splitToAuthors() {
		
		String[] aut;
		boolean hasColon = false;
		boolean hasSemiColon = false;

		if(authors.contains(";"))
			hasSemiColon = true;
		
		if(authors.contains(","))
			hasColon = true;
		
		if(!hasSemiColon){
			aut = authors.split(", and | & | and |,| y ");
			splitNames(aut, " ");
		}
		else{
			aut = authors.split("; | & | and ");
			if(hasColon)
				splitNames(aut, ", ");
			else
				splitNames(aut, " ");
		}	
	}

	private void splitNames(String[] a, String rx){

		boolean noSplits = true;

		for(int i=0; i<a.length; i++){
			String var = a[i];
			if(var.length() > 0){
				var = trimString(var);
				String[] temp = var.split(rx);

				if(temp.length == 2){
					noSplits = false;
					saveItem(temp[0], temp[1]);
				}else if(temp.length >= 3){
					noSplits = false;
					builder.delete(0, builder.length());
					for(int j=1; j<temp.length; j++){
						builder.append(temp[j] + " ");
					}
					saveItem(temp[0], builder.toString());
				}
			}
		}	
		
		if(noSplits == true){
			for(int i=0; i<a.length; i=i+2){
				if(a.length >= (i+2)){
					saveItem(a[i], a[i+1]);
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
