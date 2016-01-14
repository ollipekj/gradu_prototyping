/**
 * 
 */
//Export scripts

function ReportGenerationHandler(){

	this.openInNewPage = function(){

		var ok = true;

		if($("#year_min").val() > $("#year_max").val()){
			alert("Minimum year cannot be greater than the maximum year");
			ok = false;
		}

		if(_getCheckedNames().length == 0){
			alert("Choose at least one author");
			ok = false;
		}

		if($("#year_min").val() == "" || $("#year_max").val() == ""){
			alert("Enter minimum and maximum years, please");
			ok = false;
		}

		if(ok){
			_resolveYearRange();
			var params = urlBuilder.getReportParameters(); 
			var authors = _getCheckedNames();
			var servletURL = "/clientServices/getBibliography?items=" + JSON.stringify(authors) + "&queryOptions=" +encodeURIComponent(params);

			window.open(servletURL);
		}
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