/**
 * 
 */
//Export scripts

function ReportGenerationHandler(){

	this.openInNewPage = function(){

		var target;

		if($("#author_view").is(":checked")){
			
			if(_selectionIsValid()){
				target = _getCheckedNames();
				_resolveYearRange();
				_sendReportRequest(target);
			}
		}else{
			target = new Array();
			target.push(new _checkedElement(urlBuilder.getVariableValue("projects"), ""));
			_sendReportRequest(target);
		}
	}
	
	function _sendReportRequest(target){
		
		var params = urlBuilder.getReportParameters(); 
		var servletURL = "/clientServices/getBibliography?items=" + JSON.stringify(target) + "&queryOptions=" +encodeURIComponent(params);

		window.open(servletURL);
	}
	
	function _selectionIsValid(){
		
		if($("#year_min").val() > $("#year_max").val()){
			alert("Minimum year cannot be greater than the maximum year");
			return false;
		}else if(_getCheckedNames().length == 0){
			alert("Choose at least one author");
			return false;
		}else if($("#year_min").val() == "" || $("#year_max").val() == ""){
			alert("Enter minimum and maximum years, please");
			return false;
		}else
			return true;
	}

	function _resolveYearRange(){

		var yearMin = $("#year_min").val();
		var yearMax = $("#year_max").val();
		var yearRange = "&tag=" + yearMin;

		while(yearMin < yearMax){
			yearMin++;
			yearRange += " || " + yearMin;
		}	

		urlBuilder.setVariableValue("year_tags", yearRange);	
	}

	function _checkedElement(key, name){
		this.authorKey = key;
		this.authorName = name;
	}

	function _getCheckedNames(){

		var checkedNames = new Array();

		$("input:checkbox").each(function(){

			if($(this).is(":checked")){
				var authorKey = $(this).attr("id");
				var authorName = $("#" + authorKey + "label").text();
				checkedNames.push(new _checkedElement(authorKey, authorName));
			}
		});	
		return checkedNames;
	}
}