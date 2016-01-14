
function updateExportAuthors(){
	
	$("#export_authors").empty();
	$("#export_authors").append("<thead><tr><th></th><th>Name</th><th>Publications</th></tr></thead>");
	
	for(var i=0; i<sortedAuthors.length; i++){
		$("#export_authors").append("<tr><td><input type=checkbox class=checkable id=" + sortedAuthors[i]["key"] +  " value=" + sortedAuthors[i]["name"] + "></td>" + 
				"<td><label class=transfer_name_label id=" + sortedAuthors[i]["key"] + "label" +">" + sortedAuthors[i]["name"] + "</label></td>"+
				"<td>" + sortedAuthors[i]["itemCount"] + "</td></tr>");
	} 
} 

function toggleExportOptions(init){
	
	var labelText = "";
	var isHidden;
	var exportOptions =  $("#file_import_options");
	
	if(init == true)
		isHidden = false;
	else
		isHidden = exportOptions.hasClass("hide");
	
	if(isHidden == false){
		exportOptions.addClass("hide");
		labelText = "Show export options";
	}else{
		exportOptions.removeClass("hide");
		labelText = "Hide export options";
	}
	$("#import_label").text(labelText);	
}

function toggleExportRadioButtons(value){
	
	var cond;
	var legendText;
	
	if(value == "results"){
		cond = true;
		legendText = "Search options";
	}else{
		cond = false;
		legendText = "Filters";
	}
	
	$("#form_legend").html("<b>" + legendText + "</b>");
	
	$(".multiauthor_query").toggleClass("hide", cond);
	$(".normal_query").toggleClass("hide", !cond);	
	$(".query_field").toggleClass("pure-input-1-3", cond);	
	$(".query_field").toggleClass("pure-input-1-2", !cond);	
	$("#query_form").toggleClass("pure-form-aligned", cond);	
	$("#query_form").toggleClass("pure-form-stacked", !cond);	
}

function toggleResultsDisplay(show){
	if(show == false)
		$("#results_container").addClass("hide");
	else
		$("#results_container").removeClass("hide");
}

function changeReadyStatus(status){
	
	READ_READY = status;
	var labelText = "";
	
	if(READ_READY == BUSY_STATUS.READY)
		$("#search_label").empty();
	else if(READ_READY == BUSY_STATUS.BUSY)
		$("#search_label").append("<i class='fa fa-spinner fa-spin fa-2x'></i>");
}

function statusFailure(u){
	alert("A server request has failed.");
}

function busyAlert(){
	alert("Retrieving results, please wait.");
}
