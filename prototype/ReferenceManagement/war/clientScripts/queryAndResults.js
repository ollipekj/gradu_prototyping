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
		var elements = new Object();
		getAjax(url, "GET", "", "", function(){changeReadyStatus(BUSY_STATUS.BUSY)})
		.done(function(data, textStatus, xhr){

			var headerLinks = xhr.getResponseHeader('Link'); 
			var totalResults = xhr.getResponseHeader('Total-Results');

			$("#result_field").empty();
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

function _createResultList(data){
	
	for(var d in data){
		new ResultElement(d, data[d]);
	}
}

function AuthorListHandler(){
	
	this.getAuthors = function(url){
		getAjax(url, "GET", "", "")
		.done(function(data){
			_populateAuthorList(data);
			updateExportAuthors();
		}).fail(function(){
			statusFailure(url);
		});
	}
	
	function _sortAuthors(data){

		for(var d in data){
			var key =  data[d].key;
			var name = data[d]["data"].name;
			var itemCount = data[d]["meta"].numItems;
			sortedAuthors.push({key:key, name:name, itemCount:itemCount});
		}

		sortedAuthors.sort(function(a, b){
			var tempA=a.name.toLowerCase(), tempB=b.name.toLowerCase();
			if (tempA < tempB)
				return -1;
			if (tempA > tempB)
				return 1;
			return 0;
		});
	}

	function _populateAuthorList(data){

		$("#result_field").empty();
		_sortAuthors(data);

		for(var i=0; i<sortedAuthors.length; i++){
			$("#authors").append("<option value=" + sortedAuthors[i].key + ">" + sortedAuthors[i].name + "</option>");
		} 
	}
}