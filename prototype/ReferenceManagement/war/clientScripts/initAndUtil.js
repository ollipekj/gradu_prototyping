
var publicationLinks = Object.create(null);
var urlBuilder;
var pageLinkHandler;
var listHandler;
var reportHandler;

var navigationLinks = ["first", "prev", "next", "last"];
var linkDisabled = "disabled";
var READ_READY;
var BUSY_STATUS = {
		BUSY: 0,
		READY: 1
};

$(document).ready(function(){ 

	urlBuilder = new URLBuilder();
	pageLinkHandler = new PaginationLinkHandler();
	listHandler = new ListHandler();
	reportHandler = new ReportGenerationHandler();
	
	pageLinkHandler.initializeNavigationLinks();
	_initYearFields();
	pageLinkHandler.clearNavigationLinks();
	toggleExportOptions(true);
	toggleExportRadioButtons($("input[name=exportRadio]:checked").val());
		
	$(".query_field").each(function() {		
		_resolveProperty(this);	});
	
	$(".query_field").change(function() {		
		_resolveProperty(this);	});
	
	$("input[name=exportRadio]").on("click", function(e) {
		toggleExportRadioButtons($(this).val());
	});
	
	$("input[name=report_selector]").on("click", function(e) {
		toggleReportSelectorRadioButtons($(this).val());
	});
	
	changeReadyStatus(BUSY_STATUS.READY);
	listHandler.getAuthors(urlBuilder.getAuthorsURL());	
	listHandler.getProjects(urlBuilder.getProjectsURL());	
	
	function _initYearFields(){
		var year = new Date().getFullYear();
		$("#year_min").val(year - 1);
		$("#year_max").val(year);
	}
	
	function _resolveProperty(prop){
		
		var id = $(prop).attr("id");
		var value = $(prop).val();

		urlBuilder.setVariableValue(id, value);
	}
});

function getAjax(url, type, data, dataType, beforeSendFunc){

	if(beforeSendFunc === undefined ){
		beforeSendFunc = function(){};
	}
	
	return $.ajax({
		url: url,
		type: type,
		data: data,
		dataType: dataType,
		beforeSend: beforeSendFunc
	})
}

function statusFailure(u){
	alert("A server request has failed.");
}

function busyAlert(){
	alert("Retrieving results, please wait.");
}
