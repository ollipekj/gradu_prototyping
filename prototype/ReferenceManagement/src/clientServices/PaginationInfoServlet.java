package clientServices;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utility.parsers.JSONparser;
import utility.parsers.LinkHeaderParser;
import utility.parsers.URLParameterParser;
import utility.parsers.ParserUtilities.ParsedDataHolder;

public class PaginationInfoServlet  extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		JSONparser jsonParser = new JSONparser();		
		LinkHeaderParser parser = new LinkHeaderParser();
		URLParameterParser parameterParser = new URLParameterParser();
		String linkString = req.getParameter("linkHeader");
		String url = req.getParameter("url");
		
		ArrayList<ParsedDataHolder> links = parser.parseLinks(linkString);
		String returnMessage = jsonParser.linkHeadersToJSON(links);
		
		parameterParser.parse(url);
		String startKey = "start";
		String startIndex = "0";
		ParsedDataHolder par = parameterParser.getItem(parameterParser.getResults(), startKey);
		if(par != null){
			startIndex = par.getSecond();
		}
		returnMessage = jsonParser.addDataToObject(returnMessage, startKey, startIndex);
		
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(returnMessage);	
		
	}
}
