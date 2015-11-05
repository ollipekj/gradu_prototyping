/**
 * 
 */

var baseURL = "https://api.zotero.org";
var itemsId = "/items";
var groupId = "/groups/318216";
var format = "format=json&include=data,bib,bibtex,ris&style=apa&limit=10";
var versionAndKey = "&v=3";

var author = "";
var itemType = "";
var sortType = "";
var search = "";
var sort_direction = "";

var publicData = Object.create(null);
var publicationLinks = Object.create(null);

var navigationLinks = ["first", "prev", "next", "last"];
var fileName = "";
var readReady = true;
var clickFunction;

var linkDisabled = "disabled";
var currentURL = "";

$(document).ready(function(){ 

	resolveSortDirection();
	resolveItemType();
	resolveSortType();
	resolveAuthor();	
	
	initializeNavigationLinks();
	clearNavigationLinks();
	toggleResultsDisplay(false);
	toggleImportOptions(true);
	
	$("#sort_direction").change(function() {
		resolveSortDirection(); });
	
	$("#item_type").change(function() {		
		resolveItemType();	});
	
	$("#sort_type").change(function() {		
		resolveSortType();	});
	
	$("#authors").change(function() {		
		resolveAuthor();	});
	
	changeReadyStatus(true);
	getAuthors(baseURL + groupId + "/collections/UIP7RBD8/collections?format=json" + versionAndKey);	
});

function toggleResultsDisplay(show){
	if(show == false)
		$("#results_container").addClass("hide");
	else
		$("#results_container").removeClass("hide");
}

function toggleImportOptions(init){
	
	var labelText = "";
	var isHidden;
	var importOptions =  $("#file_import_options");
	
	if(init == true)
		isHidden = false;
	else
		isHidden = importOptions.hasClass("hide");
	
	if(isHidden == false){
		importOptions.addClass("hide");
		labelText = "Show import options";
	}else{
		importOptions.removeClass("hide");
		labelText = "Hide import options";
	}
	$("#import_label").text(labelText);	
}

function changeReadyStatus(isReady){
	
	readReady = isReady;
	var labelText = "";
	
	if(readReady == true)
		$("#search_label").empty();
	else
		$("#search_label").append("<i class='fa fa-spinner fa-spin fa-2x'></i>");
}

function resolveSortDirection(){
	sort_direction = "&direction=" + $("#sort_direction").val();
}

function resolveSortType(){
	sortType = "&sort=" + $("#sort_type").val();
}

function resolveAuthor(){
	
	var temp = $("#authors").val();
	if(temp != "")
		author = "/collections/" + temp;
	else
		author = "";
}

function resolveItemType(){
	
	itemType = $("#item_type").val();
	if(itemType != "")
		itemType = "&itemType=" + itemType;
	else
		itemType = "";
}

function getURL(){
	
	search = $("#search_field").val();
	
	if(search != "")
		search = "&q=" + search
		
	var url = baseURL + groupId + author + itemsId + 
				"?" + format + itemType + search + sortType + sort_direction;
	return url;
}

function getFile(){
	
	var servletURL = "getImportFile";
	var format = $("#importFormat").val();
	var sendURL = null;
	var firstURL = $("#first").attr("href");
	
	if(typeof firstURL === "undefined" && currentURL != ""){
		sendURL = currentURL;
	}else if(typeof firstURL !== "undefined" && publicData != null){
		sendURL = firstURL;
	}
	
	if(sendURL != null){
		window.location = servletURL + "?format=" + format + "&url=" + encodeURIComponent(sendURL);
	}else
		alert("No results to download");	
}

function readRequest(url){
	
	if(readReady == true){
		console.time("Create result list"); //for debug
		changeReadyStatus(false);
		currentURL = url;
		var elements = new Object();

		getAjax(url, "GET", "", "")
		.done(function(data, textStatus, xhr){

			statusSuccess(url);
			var headerLinks = xhr.getResponseHeader('Link'); 
			var totalResults = xhr.getResponseHeader('Total-Results');
			publicData = data;		

			$("#result_field").empty();
			createResultList(data);	
			console.timeEnd("Create result list"); //for debug
			updateNavigationPane(headerLinks, totalResults, data.length);		
			changeReadyStatus(true);

		}).fail(function(){
			statusFailure(url);
			changeReadyStatus(true);	
		});;
	}else
		busyAlert();
		
}

function busyAlert(){
	alert("Retrieving results, please wait.");
}

function isEven(value){
	if(value % 2 == 0)
		return true;
	else
		return false;
}

function createResultList(data){
	
	for(var d in data){
			
		var container = $("<div></div>").addClass("result_list");
		var contentContainer = $("<div></div>").addClass("content_container").attr({id: "element" + d}).append(data[d].bib);
		var optionContainer = $("<form></form>").addClass("list_options pure-form pure-g");
		var formatOptions = $("<select></select>").addClass("height_auto formatOptions");
		var href = "";
		var text = "";
		
		if(!isEven(d))
			container.addClass("even_result_item");
		
		formatOptions.append(
				"<option label=Citation value=bib>" + d + "</option>" +
				"<option label=Bibtex value=bibtex>" + d + "</option>" +
				"<option label=RIS value=ris>" + d + "</option>");

		if(data[d]["data"].url.length > 0){
			href = data[d]["data"].url;
			text = "Get the publication";
		}else{
			href = "https://scholar.google.fi/scholar" + encodeURI("?hl=en&q=" + data[d]["data"].title);
			text = "Find the publication";
		}
		
		optionContainer.append($("<div></div>").addClass("pure-u-1-2 pure-control-group").append($("<label>Format&nbsp;&nbsp;</label>").addClass("float_label_left")).append(formatOptions));
		optionContainer.append($("<div></div>").addClass("pure-u-1-2").append($("<label></label>").addClass("float_label_right").append($("<a></a>").attr({href: href, target: "_blank", id: "pub_link"}).text(text))));
		
		container.append(contentContainer);
		container.append(optionContainer);
		$("#result_field").append(container);
	}
	
	 $("select.formatOptions").change(function () {
	     var selectedOption = $(this).find("option:selected");
	     var dataType = selectedOption.val();
	     var index = selectedOption.text();
	     
	     $("#element"+index).html(publicData[index][dataType]);
	 });
	 
	 toggleResultsDisplay(true);

}

function showTotalResults(total, range, min){
	
	var ceiling = parseInt(min) + parseInt(range);
	var actualMin = parseInt(min) +1;
	var resultText = "";
	
	if(ceiling == 0)
		resultText = "No results found";
	else
		resultText = "Showing results " + actualMin + "-"  + ceiling;
	
	$("#navigation_label").text(resultText);
	if(ceiling != total)
		$("#navigation_label").append(" of " + total);
}

function updateNavigationPane(headerLinks, totalResults, length){

	var data = {linkHeader:headerLinks, url:currentURL};

	getAjax("getPaginationInfo", "GET", data, "json")
	.done(function(data){
		
		clearNavigationLinks();
		createNavigationList(data.links);
		showTotalResults(totalResults, length, data.start);
		
	}).fail(function(){
		statusFailure(url);
	});;
}

function createNavigationList(data){
		
	for(var d in data){
		var id = '#' + data[d].action;
		$(id).removeClass(linkDisabled).attr({href:data[d].link});
	}
}

function clearNavigationLinks(){
	
	for(var link in navigationLinks){
		var id = '#' + navigationLinks[link];
		$(id).addClass(linkDisabled).removeAttr("href");
	}
}

function initializeNavigationLinks(){
	
	for(var link in navigationLinks){
		var id = '#'+navigationLinks[link];
		$(id).on("click", function(e) {  e.preventDefault(); 
											if(! $(this).hasClass(linkDisabled)){
												if(readReady == true)
													readRequest($(this).attr("href"));
												else 
													busyAlert(); 
											} });
	}
}

function getAuthors(url){
	getAjax(url, "GET", "", "")
	.done(function(data){

		statusSuccess(url);
		$("#result_field").empty();

		for(var d in data){
			$("#authors").append("<option value=" + data[d].key + ">" + data[d]["data"].name + "</option>");
		} 
	}).fail(function(){
		statusFailure(url);
	});;
}

function getPublicationListURL(){

	if(author != ""){
		url = baseURL + groupId + author + "/collections";
		getAjax(url, "GET", "", "")
		.done(function(data){

			statusSuccess(url);
			$("#result_field").empty();
			setFileName(data[0]["data"].name);
			getLinks();
		}).fail(function(){
			statusFailure(url);
		});;
	}else
		alert("No author selected");
}

function setFileName(name){
	this.fileName = name;
}

function statusSuccess(u){
	$("#info_box").html("Read successful from: " + u + "<br>");
}

function statusFailure(u){
	$("#info_box").append("Request failed for: " + u);
}

function executeQuery(){
	readRequest(getURL());
}

function getAjax(url, type, data, dataType){
	
	return $.ajax({
		url: url,
		type: type,
		data: data,
		dataType: dataType
	})
}
