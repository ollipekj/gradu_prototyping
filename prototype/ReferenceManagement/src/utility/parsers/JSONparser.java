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
	JSONObject jsonTemplate;

	private static final Logger log  = Logger.getLogger(JSONparser.class.getName());
	
	public JSONparser(){}

	public String enterValues(String template, String authorKey, Map<String, ItemField> values, ArrayList<ParsedDataHolder> authors){

		try {
			template = addDataToArray(template, "", "collections", authorKey);
			jsonTemplate = new JSONObject(template);

			for(Entry<String, ItemField> entry : values.entrySet())
			{
				String key = entry.getValue().getZoteroType();
				String value =  entry.getValue().getValue();
				//log.log(Level.INFO, "key: " + key + ", value: " + value);
				switch(key){
				case "creators":
					jsonTemplate.put(key , authorsToJson(authors));
					break;
				case "DOI": // Data might have doi but not url defined. Url is generated from doi just in case
					//log.log(Level.INFO, "Creating url from doi");
					if(value.length() > 3){ //Doi field might include characters such as '-' to indicate no DOI
						enterValueToTemplate("url", createDOIUrl(value));
						enterValueToTemplate(key, value);
					}
					break;
				default:
					enterValueToTemplate(key, value);
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
			return "error";
		}		
		return "[" + jsonTemplate.toString() + "]";
	}
	
	private void enterValueToTemplate(String key, String value){

		if(jsonTemplate.has(key)){
			try {
				if(value != "" && value != null)
					jsonTemplate.put(key, value);	
			} catch (JSONException e) {
				log.log(Level.INFO, "Error while entering a value to template");
				e.printStackTrace();
			}
		}
			//log.log(Level.INFO, "key: " + key + " not found in template");
	}
	
	public String addDataToArray(String jsonString, String parentObject, String arrayKey, String value){
		
		JSONObject json;
		
		try {
			log.log(Level.INFO, "adding data: " + jsonString);
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
	
	private String createDOIUrl(String doi){
		
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
		}
		return result;
	}
	
	public int getArrayLength(String jsonString){
		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return json.toString();
	}
	
//	public String addKeyValuePairToObject(String jsonString, String key, String value){
//		
//		JSONObject json = null;
//		
//		try {
//			json = new JSONObject(jsonString);
//			json.put(key, value);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return json.toString();
//	}
//	
//	public String addKeyValuePairToObject(String jsonString, String key, int value){
//		
//		JSONObject json = null;
//		
//		try {
//			json = new JSONObject(jsonString);
//			json.put(key, value);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return json.toString();
//	}
//	
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
	
	private JSONArray authorsToJson(ArrayList<ParsedDataHolder> authors){
		
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
		return result;
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
