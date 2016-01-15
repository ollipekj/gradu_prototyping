/**
 * 
 */
// Query and results scripts
function executeQuery(){
	readRequest(urlBuilder.getSearchURL());
}

function readRequest(url){
	
	if(READ_READY == BUSY_STATUS.READY){
		
		urlBuilder.currentURL = url;

		getAjax(url, "GET", "", "", function(){changeReadyStatus(BUSY_STATUS.BUSY)})
		.done(function(data, textStatus, xhr){

			var headerLinks = xhr.getResponseHeader('Link'); 
			var totalResults = xhr.getResponseHeader('Total-Results');

			console.time("Create result list"); //for debug
			_createResultList(data);	
			console.timeEnd("Create result list"); //for debug
			pageLinkHandler.updateNavigationPane(headerLinks, totalResults, data.length);		

		}).fail(function(){
			statusFailure(url);
		}).complete(function(){
			changeReadyStatus(BUSY_STATUS.READY);	
		});
	}else
		busyAlert();
	
	function _createResultList(data){
		
		$("#result_field").empty();
		
		for(var d in data){
			new ResultElement(d, data[d]);
		}
	}
}

function getFile(){
	
	var servletURL = "/clientServices/getImportFile";
	var format = $("#importFormat").val();
	var sendURL = null;
	var firstURL = $("#first").attr("href");
	
	if(typeof firstURL === "undefined" && urlBuilder.currentURL != ""){
		sendURL = urlBuilder.currentURL;
	}else if(typeof firstURL !== "undefined" && publicData != null){
		sendURL = firstURL;
	}
	
	if(sendURL != null){
		window.location = servletURL + "?format=" + format + "&url=" + encodeURIComponent(sendURL);
	}else
		alert("No results to download");	
}

function ListHandler(){
	
	var sortedAuthors = [];
	var sortedProjects = [];
	
	this.getAuthors = function(url){
		getAjax(url, "GET", "", "")
		.done(function(data){
			_populateAuthorList(data);
			_updateExportAuthors();
		}).fail(function(){
			statusFailure(url);
		});
	}
	
	this.getProjects = function(url){
		getAjax(url, "GET", "", "")
		.done(function(data){
			_populateProjectSelector(data);
		}).fail(function(){
			statusFailure(url);
		});
	}
	
	function _sortItems(data, sorted){

		for(var d in data){
			var key =  data[d].key;
			var name = data[d]["data"].name;
			var itemCount = data[d]["meta"].numItems;
			sorted.push({key:key, name:name, itemCount:itemCount});
		}

		sorted.sort(function(a, b){
			var tempA=a.name.toLowerCase(), tempB=b.name.toLowerCase();
			if (tempA < tempB)
				return -1;
			if (tempA > tempB)
				return 1;
			return 0;
		});
	}

	function _updateExportAuthors(){
		
		$("#export_authors").empty();
		$("#export_authors").append("<thead><tr><th></th><th>Name</th><th>Publications</th></tr></thead>");
		
		for(var i=0; i<sortedAuthors.length; i++){
			$("#export_authors").append("<tr><td><input type=checkbox class=checkable id=" + sortedAuthors[i]["key"] +  " value=" + sortedAuthors[i]["name"] + "></td>" + 
					"<td><label class=transfer_name_label id=" + sortedAuthors[i]["key"] + "label" +">" + sortedAuthors[i]["name"] + "</label></td>"+
					"<td>" + sortedAuthors[i]["itemCount"] + "</td></tr>");
		} 
	}
	
	function _populateProjectSelector(data){

		_sortItems(data, sortedProjects);

		for(var i=0; i<sortedProjects.length; i++){
			$("#projects").append("<option value=" + sortedProjects[i].key + ">" + sortedProjects[i].name + "</option>");
		} 
	}
	
	function _populateAuthorList(data){

		$("#result_field").empty();
		_sortItems(data, sortedAuthors);

		for(var i=0; i<sortedAuthors.length; i++){
			$("#authors").append("<option value=" + sortedAuthors[i].key + ">" + sortedAuthors[i].name + "</option>");
		} 
	}
}