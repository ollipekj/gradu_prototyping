package clientServices;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.logging.Level;
import java.util.logging.Logger;

import utility.URLFetcher;
import utility.ZoteroURLBuilder;
import utility.parsers.JSONparser;

public class getBibliographyServlet extends HttpServlet {
	private static final Logger log  = Logger.getLogger(getBibliographyServlet.class.getName());
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		JSONparser parser = new JSONparser();	
		String items = req.getParameter("items");
		String queryOptions = req.getParameter("queryOptions");
		//queryOptions = URLDecoder.decode(queryOptions, "UTF-8");
		queryOptions = encodeQuery(queryOptions);
		log.log(Level.INFO, queryOptions);
		final String entryClass = "csl-entry";
		ZoteroURLBuilder urlBuilder = new ZoteroURLBuilder();
		URLFetcher fetcher = new URLFetcher();
		String returnMessage = null;
		
		if(items != null){		
			int length = parser.getArrayLength(items);
			String html = "<!doctype html><html><body><head>" +
					"<meta http-equiv='content-type' content='text/html; "
					+ "charset=UTF-8'></head><style>." + entryClass + "{font-size:11pt; line-height: 2; padding-left: 2em; text-indent:-2em;}</style></body></html>";				
			String authorItems = "";
			Document doc = null;
			
			for(int i=0; i<length; i++){
				String entry = parser.getValueFromArray(items, i);
				String name = parser.getStringValue(entry, "authorName");
				String key = parser.getStringValue(entry, "authorKey");
				if(length > 0){
					authorItems += "<h3>"+ name + "</h3>";					
					String url = urlBuilder.getBibliographySearchURL(key, queryOptions);
					//url = URLEncoder.encode(url, "utf-8");
					//url="https://api.zotero.org/groups/318216/collections/SZQTP9KK/items?include=bib&style=apa&tag=2014%20%7C%7C%202015";
					log.log(Level.INFO,"Search url " +  url);
					fetcher.fetch(url);
					String results = fetcher.getResult();
					int authorResultsLength = parser.getArrayLength(results);
					
					for(int j=0; j<authorResultsLength; j++){
						String pubEntry = parser.getValueFromArray(results, j);
						String bib = parser.getStringValue(pubEntry, "bib");
						//log.log(Level.INFO,"Bib " + bib);
						doc = Jsoup.parse(bib);
						Element bibEl = doc.select("div." +entryClass).first();
						authorItems += bibEl.toString() + "<br>";
						//log.log(Level.INFO,"Authoritems " +  authorItems);
					}							
				}				
			}	
			doc = Jsoup.parse(html);
			doc.select("body").append(authorItems);
			
//			for(Element e : doc.select("div."+entryClass)){
//				e.after("<br>");
//			}
			
			returnMessage = doc.toString();
			
		}else
			returnMessage = "<body><html><h1>Unable to resolve user data</h1></html></body>";
		
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(returnMessage);			
	}
	
	private String encodeQuery(String options){
		options = options.replace(" ", "%20");
		options = options.replace("|", "%7C");
		return options;
	}
//	private void getItemsBySortField(){
//		for(int j=0; j<parser.getArrayLength(result); j++){
//			String bibEntry = parser.getValueFromArray(result, j);
//			bibEntry = parser.getObjectByKey(bibEntry, "bib");
//			html += bibEntry;
//		}				
//	}
}
