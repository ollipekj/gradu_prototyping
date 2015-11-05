package utility.parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class XMLparser extends ParserUtilities implements UtilityParser{
	
	ArrayList<String> links = new ArrayList<String>();
	ArrayList<ParsedDataHolder> items;
	private static final Logger log  = Logger.getLogger(XMLparser.class.getName());
	Document doc;
	
	public boolean checkConnectionStatus(){
		if(doc == null)
			return false;
		else
			return true;
	}
	
	public ArrayList<String> getLinks(){
		Elements els = doc.getElementsByTag("guid");

		for (Element link : els) {
			links.add(link.text());
			log.log(Level.INFO, link.text());
		}		
		return links;
	}
	
	private String trimTags(String entry){
		
		entry = trimString(entry, "<![CDATA[");
		entry = trimString(entry, "]]>");
		
		return entry;		
	}

	@Override
	public void parse(String address) {
		
		try{
			doc = Jsoup.connect(address).ignoreContentType(true).get();		
		}catch (HttpStatusException e){
			e.printStackTrace();
			doc = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public ArrayList<ParsedDataHolder> getResults() {
		Elements els = doc.getElementsByTag("item");
		items = new ArrayList<ParsedDataHolder>();
		
		for (Element item : els) {
			String link = item.getElementsByTag("guid").first().text();
			String title = item.getElementsByTag("title").first().text();
			title = trimTags(title);
			items.add(new ParsedDataHolder(link, title));
		}		
		return items;
	}

}
