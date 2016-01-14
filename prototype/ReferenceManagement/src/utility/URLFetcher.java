package utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import utility.parsers.JSONparser;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.urlfetch.HTTPMethod;

public class URLFetcher {
	
	private JSONparser parser = new JSONparser();
	private String result = "";
	private Map<String, List<String>> headers = null;
	private static final Logger log  = Logger.getLogger(URLFetcher.class.getName());
	private ChannelService channelService = ChannelServiceFactory.getChannelService();
	
	public void fetch(String address){
		
		boolean success = false;
			
		try{
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			connection.setRequestProperty("Accept-Language", "fi-FI,fi;q=0.8,en-US;q=0.6,en;q=0.4");
			connection.setRequestProperty("Cache-Control", "max-age=0");
			connection.setRequestProperty("Connection", "close");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71");
			headers = connection.getHeaderFields();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line = "";	
			result = "";

			while ((line = reader.readLine()) != null) {
				result += line;
			}
			
			reader.close();
			connection.disconnect();
			success = true;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e){
			e.printStackTrace();
		}
		
		if(!success)
			result = "error";
	}
	
	public TransferStatus sendMetadata(String address, String data, HTTPMethod method){
		
		URL url;
		TransferStatus status = new TransferStatus();
		status.setResponseCode(1);

		try {
			url = new URL(address);
			log.log(Level.INFO, "Sending metadata: " + data);
			log.log(Level.INFO, "to: " + address);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			connection.setDoOutput(true);
			connection.setRequestMethod(method.toString());
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			writer.write(data);
			writer.close();

			int code = connection.getResponseCode();		
			status.setResponseCode(code);
			
			if(code == 200){
				StringBuilder responseBody = new StringBuilder();
				InputStreamReader ipw = new InputStreamReader(connection.getInputStream(), "UTF-8");
				int ch = ipw.read();
				
				while(ch != -1){
				    char readChar = (char) ch;
				    responseBody.append(readChar);
				    ch = ipw.read();
				
				}
				ipw.close();	
			    String zoteroLink = parser.getLinkFromResponseBody(responseBody.toString());
			    //log.log(Level.INFO, "Zotero link: " + responseBody.toString());
			    status.setZoteLink(zoteroLink);
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}

		return status;
	}

	public String getResult(){
		return result;
	}
	
	public List<String> getHeader(String key){
		return headers.get(key);
	}
	
//	public Map<String, List<String>> getHeaderList(){
//		return headers;
//	}
	
	public void sendStatusUpdate(String msg){
		String channelKey = "yyz";
		channelService.sendMessage(new ChannelMessage(channelKey, msg));
	}
}
