/**
 * 
 */

var baseURL = "https://api.zotero.org";
var itemsId = "/items";
var groupId = "/groups/318216";
var versionAndKey = "&v=3&key=iXaNMci2uegaU9CFD1Iw2CHu";
var publicData = new Object();
var publicationLinks = new Object();

$(document).ready(function(){ 
	
});

function transferPublications(items){
	
	var data = {items:JSON.stringify(items)};
	getAjax("transferItems", "POST", data, "json")
	.done(function(data){
		statusSuccess(fileUrl);
		//$("#log_field").append(JSONStringify(data));

	}).fail(function(){
		$("#info_box").append("Request failed.");
	});;
}

function getAuthors(){
	
	var url = baseURL + groupId + "/collections/UIP7RBD8/collections?format=json" + versionAndKey;
	getAjax(url, "GET", "", "")
	.done(function(data){
		$("#author_table").empty();
		$("#author_table").append("<thead><tr><th><input type=checkbox id=select_all></th><th>Name</th><th>Items</th></tr></thead>");
		statusSuccess(url);

		for(var d in data){
			var key =  data[d].key;
			var name = data[d]["data"].name;
			var itemCount = data[d]["meta"].numItems;
			$("#author_table").append("<tr><td><input type=checkbox class=checkable id=" + key +  " value=" + name + "></td>" + 
					"<td><label class=transfer_name_label id=" + key + "label" +">" + name + "</label></td>"+
					"<td>" + itemCount + "</td></tr>");
		} 
		$("#select_all").click(function(){
			$(".checkable").prop("checked", this.checked);
		});
	}).fail(function(){
		$("#log_field").append("Request failed for: " + url);
	});;
}

function transferSingleItem(){
	
	var itemURL = $("#single_item_field").val();
	var authorKey = null;
	var authorName = null;
	var chosenAuthors = getCheckedNames();
	
	if(itemURL.length < 5){
		alert("Give proper url");
		return false;
	}
	
	if(chosenAuthors.length < 1)
		alert("You must select the author of the publication");
	else if(chosenAuthors.length > 1)
		alert("Choose only one author of the publication");
	else{
		authorKey = chosenAuthors[0].authorKey;
		authorName = chosenAuthors[0].authorName;
		var data = {link:itemURL, authorKey:authorKey, authorName:authorName};
		
		getAjax("transferSingleItem", "POST", data, "json")
		.done(function(data){
	
			$("#log_field").empty();
			$("#log_field").html(JSON.stringify(data));
		}).fail(function(){
			$("#info_box").append("Request failed for: " + url);
		});;
	}
}

function checkedElement(key, name){
	this.authorKey = key;
	this.authorName = name;
}

function getCheckedNames(){
	
	var checkedNames = new Array();
	
	$("input:checkbox").each(function(){
	    
	    if($(this).is(":checked")){
	       var author = $(this).attr("id");
	       var authorName = $("#" + author + "label").text();
	       checkedNames.push(new checkedElement(author, authorName));
	    }
	});
	
	return checkedNames;
}

function transferAllSelectedItems(){

	var checkedNames = getCheckedNames();	
	$("#log_field").append(JSON.stringify(checkedNames) + "<br>")
	transferPublications(checkedNames);
}

function statusSuccess(u){
	$("#log_field").html("Read successful from: " + u + "<br>");
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