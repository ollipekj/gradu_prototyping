package clientServices;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import utility.URLFetcher;
import utility.parsers.JSONparser;
import utility.parsers.LinkHeaderParser;
import utility.parsers.URLParameterParser;
import utility.parsers.ParserUtilities.ParsedDataHolder;

public class ImportFileServlet extends HttpServlet{
	private static final Logger log  = Logger.getLogger(ImportFileServlet.class.getName());
	
	private boolean initOk = true;
	private boolean hasNext = true;		
	private String fileExtension = null;
	private String result = null;
	private String url = null;
	private String entry;
	private String value;
	private URLParameterParser parameterParser = new URLParameterParser();
	private URLFetcher fetcher = new URLFetcher();
	private JSONparser jsonParser = new JSONparser();
	private LinkHeaderParser linkParser = new LinkHeaderParser();

	
	protected void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {

		hasNext = true;		
		initOk = true;
		
		String linkURL = req.getParameter("url");
		String format = req.getParameter("format");
		String fileName = "publications.";
		int arrayLength;	
	
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");
		resolveFileExtension(format);
		
		if(initOk){
			fileName += fileExtension;
			parameterParser.parse(linkURL);
			url = parameterParser.createFileImportURL(format);
			response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
			log.log(Level.INFO, "Saving data from: " + url);

			while(hasNext == true){

				result = fetchData(url);
				log.log(Level.INFO, "From server: " + result);
				if(result != "error" && result != null){
					arrayLength = jsonParser.getArrayLength(result);
					
					for(int i=0; i<arrayLength; i++){						
						value = getWriteableValue(i, format);
						response.getWriter().write(value);	
						response.getWriter().write(System.getProperty("line.separator"));
					}					
					checkForNextUrlLink();					
				}
			}
		}else{
			response.getWriter().write("error");	
		}
	}
	
	private void checkForNextUrlLink(){
		
		String linkHeaders = fetcher.getHeader("link").toString();

		if(linkHeaders != null){	

			ParsedDataHolder linkItem = null;
			
			for(ParsedDataHolder holder : linkParser.parseLinks(linkHeaders)){
				
				if(holder.getSecond().equals("next")){
					linkItem = holder;
					log.log(Level.INFO, "Found next link ");
					break;						
				}
			}			

			if(linkItem == null)
				hasNext = false;
			else
				url = linkItem.getFirst();
		}					
	}
	
	private String getWriteableValue(int index, String format){
		entry = null;
		value = null;
		entry = jsonParser.getValueFromArray(result, index);
		value = jsonParser.getStringValue(entry, format);
		Document doc = Jsoup.parse(value);
		value = doc.text();
		return value;
	}
	
	private String fetchData(String url){
		fetcher.fetch(url);
		return fetcher.getResult();
	}
	
	private void resolveFileExtension(String format){
		
		switch(format){
		case "bib":
			fileExtension = "txt";
			break;
		case "bibtex":
			fileExtension = "bib";
			break;
		case "ris":
			fileExtension = "ris";
			break;
		default:	
			initOk = false;
			break;
		}
	}
}
