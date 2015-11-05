package utility.parsers;

import java.util.ArrayList;

public class ParserUtilities {

	public ParsedDataHolder getItem(ArrayList<ParsedDataHolder> items, String key){
		
		ParsedDataHolder result = null;
		
		for(ParsedDataHolder holder : items){
			
			if(holder.getFirst().equals(key)){
				result = holder;
				break;						
			}
		}	
		return result;
	}
	
	protected String trimString(String subject){
		
		int start = 0;
		int end = subject.length();
		
		while(subject.charAt(start) == ' '){
			start++;
		}
		
		while(subject.charAt(end - 1) == ' '){
			end--;
		}
		return subject = (String) subject.subSequence(start, end);		
	}
	
	protected String trimString(String subject, char trimmer){
		
		int start = 0;
		int end = subject.length();
		
		while(subject.charAt(start) == trimmer){
			start++;
		}
		
		while(subject.charAt(end - 1) == trimmer){
			end--;
		}
		return subject = (String) subject.subSequence(start, end);		
	}
	
	protected String trimString(String subject, String trimmer){
		
		int length = trimmer.length();
		
		if(subject.startsWith(trimmer)){
			subject = subject.substring(length);
		}
		
		if(subject.endsWith(trimmer)){
			subject = subject.substring(0, subject.length() - length);
		}
		
		return subject;	
	}
	
	protected String[] splitItem(String s, String rx){
		return s.split(rx);
	}
	
	public class ParsedDataHolder{
		
		String first;
		String second;
		
		public ParsedDataHolder(String f, String s){
			this.first = f;
			this.second = s;
		}
		
		public void setFirst(String f){
			this.first = f;
		}
		
		public void setSecond(String s){
			this.second = s;
		}
		
		public String getFirst(){
			return this.first;
		}
		
		public String getSecond(){
			return this.second;
		}
		
	}

}
