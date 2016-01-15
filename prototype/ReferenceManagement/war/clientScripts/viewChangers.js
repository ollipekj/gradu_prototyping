
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
	
	if(value == "results")
		cond = true;
	else if(value == "authors")
		cond = false;
	
	$(".multiauthor_query").toggleClass("hide", cond);
	$(".normal_query").toggleClass("hide", !cond);	
	$(".query_field").toggleClass("pure-input-1-3", cond);	
	$(".query_field").toggleClass("pure-input-1-2", !cond);	
	$("#query_form").toggleClass("pure-form-aligned", cond);	
	$("#query_form").toggleClass("pure-form-stacked", !cond);	
	
	toggleReportSelectorRadioButtons($("input[name=report_selector]:checked").val());
}

function toggleReportSelectorRadioButtons(value){
	//alert(value);
	var projectViewChosen;
	
	if(value == "project_view")
		projectViewChosen = true;
	else if(value == "author_view")
		projectViewChosen = false;
	
	$("#project_selector_container").toggleClass("hide", !projectViewChosen);
	$("#export_table_container").toggleClass("hide", projectViewChosen);
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