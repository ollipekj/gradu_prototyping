package utility.parsers;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utility.Constants;
import utility.URLFetcher;

public class PublistParser extends ParserUtilities implements UtilityParser {

	UtilityParser urlParser = new URLParameterParser();
	ArrayList<ParsedDataHolder> result;
	
	public PublistParser(){}
	
	@Override
	public void parse(String url){
		result = null;
		URLFetcher fetcher = new URLFetcher();
		fetcher.fetch(url);
		String html = fetcher.getResult();
		result = new ArrayList<ParsedDataHolder>();
		
		Document doc = Jsoup.parse(html);
		Element els = doc.select("div[id$=suorite]").first();
		
		if(els != null){
			Elements entries = els.select("a");
			for(Element e : entries){
				String link = e.attr("href");
				String text = e.text();
				urlParser.parse(link);

				for(ParsedDataHolder item : urlParser.getResults()){
					if(item.getFirst().equals("id")){
						link = Constants.DETAIL_PAGE_BASE_URL + item.getSecond();
						result.add(new ParsedDataHolder(link, text));
					}
				}						
			}
		}		
	}

	@Override
	public ArrayList<ParsedDataHolder> getResults() {
		return result;
	}
}
