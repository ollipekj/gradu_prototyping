package utility.parsers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utility.DataMappings.ItemField;
import utility.parsers.ParserUtilities.ParsedDataHolder;

public class JSONparser {

	JSONObject jsonObject;
	private static final Logger log  = Logger.getLogger(JSONparser.class.getName());
	
	public JSONparser(){}	
	
	public String enterValueToTemplate(String jsonTemplate, String key, String value){

		JSONObject templt;
		
		try {
			templt = new JSONObject(jsonTemplate);
			if(templt.has(key)){
				//log.log(Level.INFO, "saving to key: " + key + ", value: " + value);				
				if(value != "" && value != null){
					templt.put(key, value);	
					jsonTemplate = templt.toString();
				}				
			}
		} catch (JSONException e1) {
			//e1.printStackTrace();
			log.log(Level.WARNING, "Error while entering a value to template");
		}
		return jsonTemplate;			
	}
	
	public String addDataToArray(String jsonString, String parentObject, String arrayKey, String value){
		
		JSONObject json;
		
		try {			
			json = new JSONObject(jsonString);
			JSONArray subArray;
			
			if(parentObject != "")
				subArray = json.getJSONObject(parentObject).getJSONArray(arrayKey);
			else
				subArray = json.getJSONArray(arrayKey);
			
			subArray.put(value.toString());
			//log.log(Level.INFO, "Subarray: " + subArray.toString());
			json.put(arrayKey, subArray);
			jsonString = json.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		return jsonString;
	}
	
	public String addDataToObject(String jsonString, String key, String value){
		JSONObject json = null;
		
		try {
			json = new JSONObject(jsonString);
			json.put(key, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json.toString();
	}
	
	public String addArrayToObject(String jsonString, String key, String value){
		JSONObject json = null;
		JSONArray tempArray = null;
		
		try {
			json = new JSONObject(jsonString);
			tempArray = new JSONArray(value);
			json.put(key, tempArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json.toString();
	}
	
	public String createDOIUrl(String doi){
		
		if(doi.length() > 0){
			return "http://dx.doi.org/" + doi;
		}
		else 
			return "";
	}
	
	public String stringsToJson(String key, String value){
		
		jsonObject = new JSONObject();
	
		try {
			jsonObject.put(key, value.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	public String getObjectByKey(String jsonString, String key){
		
		String result = null;
		
		try {
			jsonObject = new JSONObject(jsonString);
			result = jsonObject.get(key).toString();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch(NullPointerException ne){
			ne.printStackTrace();
		}
		return result;
	}
	
	public int getArrayLength(String jsonString){
		//log.log(Level.INFO, "Array length string: " + jsonString);
		int l = -1;
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(jsonString);
			l = jsonArray.length();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return l;
	}
		
	public String getCollectionsFromDataField(String jsonString, String arrayKey){
		
		JSONObject json = null;
		JSONArray subArray = null;		
		
		try {
			json = new JSONObject(jsonString);
			JSONObject data = json.getJSONObject("data");
			subArray = data.getJSONArray(arrayKey);
			json = new JSONObject();
			json.put(arrayKey, subArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json.toString();
	}
	
	public String getStringFromDataField(String jsonString, String itemKey){
		
		JSONObject json = null;	
		String result = null;
		
		try {
			json = new JSONObject(jsonString);
			JSONObject data = json.getJSONObject("data");
			result = data.getString(itemKey);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
		
	public String getStringValue(String jsonString, String key){
				
		String result = null;
		
		try {
			jsonObject = new JSONObject(jsonString);
			result = jsonObject.getString(key).toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int getIntValue(String jsonString, String key){
		
		int result = 0;
		
		try {
			jsonObject = new JSONObject(jsonString);
			result = jsonObject.getInt(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String getLinkFromResponseBody(String body){
		
		String result = "";
		try{
		String temp = getObjectByKey(body, "successful");
		temp = getObjectByKey(temp, "0");
		temp = getObjectByKey(temp, "links");
		temp = getObjectByKey(temp, "alternate");
		result = getStringValue(temp, "href");
		}catch(NullPointerException e){
			result = null;
		}
		return result;
	}
	
	public String getCollectionCreationString(String name, String parent){
		
		String result = null;
		
		try {
			jsonObject = new JSONObject();
			jsonObject.put("name", name.toString());
			jsonObject.put("parentCollection", parent.toString());
			result = "[" + jsonObject.toString() + "]";
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;		
	}
	
	public String getArrayFromObject(String jsonString, String arrayKey){
		//log.log(Level.INFO, jsonString);
		String result = null;
				
		try {
			jsonObject = new JSONObject(jsonString);
			result = jsonObject.getJSONArray(arrayKey).toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;		
		
	}
	
	public String getValueFromArray(String jsonString, int index){
		
		String result = null;
		
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			result = jsonArray.getJSONObject(index).toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		return result;
	}

	public String getResult(){
		if(jsonObject != null)
			return jsonObject.toString();
		else
			return "json is null";
	}

	public String arrayToJson(String key, ArrayList<String> links){
		
		jsonObject = new JSONObject();
		boolean ok = true;
		
		for(String l : links){
			try {
				jsonObject.append(key, l);
			} catch (JSONException e) {			
				e.printStackTrace();
				ok = false;
				break;
			}
		}
		
		if(ok == true)
			return jsonObject.toString();
		else 
			return "Error in array to json conversion";
	}
	
	public String itemFieldsToJson(Map<String, ItemField> values){
		
		jsonObject = new JSONObject();
		
		for (Entry<String, ItemField> entry : values.entrySet())
		{
			try {
				jsonObject.put(entry.getValue().getZoteroType(), entry.getValue().getValue());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return jsonObject.toString();		
	}
	
	public String authorsToJson(ArrayList<ParsedDataHolder> authors){
		
		JSONObject entry;
		JSONArray result = new JSONArray();
		
		for(ParsedDataHolder a : authors){
			try {
				entry = new JSONObject();
				entry.put("creatorType", "author"); //All authors are considered as the main creators
				entry.put("firstName", a.first);
				entry.put("lastName", a.second);
				result.put(entry);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}		
	
		return result.toString();
	}
	
	public String linkHeadersToJSON(ArrayList<ParsedDataHolder> links){

		JSONObject entry;
		JSONObject result = new JSONObject();
		JSONArray linkList = new JSONArray();
		
		try {
			for(ParsedDataHolder l : links){

				entry = new JSONObject();
				entry.put("action", l.getFirst());
				entry.put("link", l.getSecond());				
				linkList.put(entry);
			}		
			result.put("links", linkList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
		return result.toString();
	}
}
