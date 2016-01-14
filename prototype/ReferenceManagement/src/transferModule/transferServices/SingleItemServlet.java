package transferModule.transferServices;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import transferModule.MetadataTransferModule;
import utility.TransferStatus;
import utility.parsers.JSONparser;

public class SingleItemServlet extends HttpServlet {
	
	String returnMessage = "";
	int returnCode = 0;
	String authorParsingStatus = "";
	String url = null;
	String authorKey = null;
	String authorName = null;
	JSONparser jsonHandler;
	MetadataTransferModule transferer;
	private static final Logger log  = Logger.getLogger(SingleItemServlet.class.getName());
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		
		jsonHandler = new JSONparser();
		transferer = new MetadataTransferModule();
		url = req.getParameter("link");
		authorKey = req.getParameter("authorKey");
		authorName = req.getParameter("authorName");
		
        log.log(Level.INFO, "URL = " + url + ", key: "+authorKey+", name: "+authorName);
        
        if(url != null && url != "" && authorKey != null && authorName != null){    
        	transferer.setAuthorKey(authorKey);
        	TransferStatus status = transferer.transferData(url, null, authorName);
        	returnCode = status.getResponseCode();
        	authorParsingStatus = status.getAuthorParsingStatus();
           	returnMessage = jsonHandler.stringsToJson("status", Integer.toString(returnCode));
        }else
        	returnMessage = jsonHandler.stringsToJson("status", "Error resolving url");
                
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(returnMessage);
	}
}
