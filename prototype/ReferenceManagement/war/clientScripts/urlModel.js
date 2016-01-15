
function URLBuilder(){
			
	this.currentURL = "";	
	
	// URL constants
	this._baseURL = "https://api.zotero.org";
	this._groupId = "/groups/318216";
	this._itemsId = "/items";
	this._format = "format=json&include=data,bib,bibtex,ris&style=apa&limit=10";
	this._versionAndKey = "&v=3&key=CAxpsCKldJzU9PHWS9g2ePqK";		
	
	// URL variables
	var _urlVariables = {
		"authors": urlSection("/collections/", ""),
		"item_type": urlSection("&itemType=", ""),
		"projects": urlSection("/collections/", ""),
		"sort_type": urlSection("&sort=", ""),
		"search_field": urlSection("&q=", ""),
		"sort_direction": urlSection("&direction=", ""),
		"year_tags": urlSection("&tag=", "")
	};		
	
	this.setVariableValue = function(id, val){
		_urlVariables[id].value = val;
	}
	
	this.getVariableValue = function(id){
		return _urlVariables[id].value;
	}
	
	this.getVersionAndKey = function(){
		return this._versionAndKey;
	}

	this.getAuthorsURL = function(){
		return this._baseURL + this._groupId + 
		"/collections/UIP7RBD8/collections?format=json&limit=100" + this._versionAndKey;
	}
	
	this.getProjectsURL = function(){
		return this._baseURL + this._groupId + 
		"/collections/RH63FXAT/collections?format=json&limit=100" + this._versionAndKey;
	}

	this.getReportParameters = function(){
		return getSection(_urlVariables["sort_type"]) + 
		getSection(_urlVariables["sort_direction"]) + 
		getSection(_urlVariables["item_type"]) + 
		getSection(_urlVariables["year_tags"]);
	}

	this.getSearchURL = function(){
		return this._baseURL + this._groupId + 
		getSection(_urlVariables["authors"]) + 
		this._itemsId + "?" + this._format + 
		getSection(_urlVariables["item_type"]) + 
		getSection(_urlVariables["search_field"]) + 
		getSection(_urlVariables["sort_type"]) + 
		getSection(_urlVariables["sort_direction"]) +
		this._versionAndKey;
	}
	
	function urlSection(k, v){
		
		return {
			key:k,
			value:v			
		};
	}
	
	function getSection(sec){

		if(sec.value != "")
			return sec.key + sec.value;
		else
			return "";
	}
}