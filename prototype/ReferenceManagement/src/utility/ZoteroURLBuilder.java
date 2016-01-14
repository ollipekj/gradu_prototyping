package utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ZoteroURLBuilder {
	
	private final String groupURL = "/groups/318216";
	private final String key = "key=iXaNMci2uegaU9CFD1Iw2CHu";
	private final String searchURL = "/items?format=json&q=";
	private final String version = "v=3";
	private final String baseURL = "https://api.zotero.org";
	private StringBuilder builder = new StringBuilder();
	
	public String getTemplateURL(String type){
		
		builder = new StringBuilder();
		builder.append(baseURL);
		builder.append("/items/new?itemType=");
		builder.append(type);
		return builder.toString();
	}
	
	public String getBibliographyURL(String authorKey){
		
		builder = new StringBuilder();
		builder.append(baseURL);
		builder.append(groupURL);
		builder.append("/collections/");
		builder.append(authorKey);
		builder.append("/items?format=bib&style=apa");
		builder.append("&" + key);
		return builder.toString();
	}	
	
	public String getBibliographySearchURL(String authorKey, String operands){
		
		builder = new StringBuilder();
		builder.append(baseURL);
		builder.append(groupURL);
		builder.append("/collections/");
		builder.append(authorKey);
		builder.append("/items");
		builder.append("?include=bib&style=apa&limit=100" + operands);
		builder.append("&" + key);
		
		return builder.toString();
	}
	
	public String getCollectionDeleteURL(String collectionKeys){
		
		builder = new StringBuilder();
		builder.append(baseURL);
		builder.append(groupURL);
		builder.append("/collections?collectionKey=" + collectionKeys);
		builder.append("&" + key);
		return builder.toString();
	}
	
	public String getSubCollectionsURL(String collectionKey){
		
		builder = new StringBuilder();
		builder.append(baseURL);
		builder.append(groupURL);
		builder.append("/collections/" + collectionKey + "/collections");
		return builder.toString();
	}
	
	public String getCollectionCreationURL(){
		builder = new StringBuilder();
		builder.append(baseURL);
		builder.append(groupURL);
		builder.append("/collections");
		builder.append("?" + key);
		return builder.toString();
	}
	
	public String getSendUrl(){
		
		builder = new StringBuilder();
		builder.append(baseURL);
		builder.append(groupURL);
		builder.append("/items?");
		builder.append(version);
		builder.append("&" + key);
		return builder.toString();
	}
	
	public String getUpdateUrl(String itemKey){
		
		builder = new StringBuilder();
		builder.append(baseURL);
		builder.append(groupURL);
		builder.append("/items/");
		builder.append(itemKey + "?");
		builder.append(version);
		builder.append("&" + key);;
		return builder.toString();
	}
	
	public String getXmlFileUrl(String collectionKey){
		
		builder = new StringBuilder();
		builder.append(baseURL);
		builder.append(groupURL);
		builder.append("/collections/");
		builder.append(collectionKey);
		builder.append("/collections?");
		builder.append(version);
		builder.append("&" + key);
		return builder.toString();
	}
	
	public String getCollectionInfo(String authorCode){
		
		builder = new StringBuilder();
		builder.append(baseURL);
		builder.append(groupURL);
		builder.append("/collections/");
		builder.append(authorCode + "?");
		builder.append(version);
		builder.append("&" + key);
		return builder.toString();
	}
	
	public String getSearchUrl(String searchTerm){
		try {
			builder = new StringBuilder();
			builder.append(baseURL);
			builder.append(groupURL);
			builder.append(searchURL);
			builder.append(URLEncoder.encode(searchTerm, "UTF-8"));
			builder.append("&" + version);
			builder.append("&" + key);
			return builder.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "error";
		}
	}
}
