/**
 * 
 */
//Navigation scripts
function PaginationLinkHandler(){ 
	
	PaginationLinkHandler.prototype.clearNavigationLinks = function(){

		for(var link in navigationLinks){
			var id = '#' + navigationLinks[link];
			$(id).addClass(linkDisabled).removeAttr("href");
		}
	}
	
	this.updateNavigationPane = function(headerLinks, totalResults, length){

		var data = {linkHeader:headerLinks, url:urlBuilder.currentURL};

		getAjax("/clientServices/getPaginationInfo", "GET", data, "json")
		.done(function(data){

			PaginationLinkHandler.prototype.clearNavigationLinks();
			_createNavigationList(data.links);
			_showTotalResults(totalResults, length, data.start);		
		});
	}

	function _showTotalResults(total, range, min){

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

	this.initializeNavigationLinks = function(){
		
		for(var link in navigationLinks){
			var id = '#'+navigationLinks[link];
			$(id).on("click", function(e) {e.preventDefault();

												if(! $(this).hasClass(linkDisabled)){
													if(READ_READY ==  BUSY_STATUS.READY)
														readRequest($(this).attr("href") + urlBuilder.getVersionAndKey());
													else 
														busyAlert(); 
												} return false;});
		}
	}
	
	function _createNavigationList(data){

		for(var d in data){
			var id = '#' + data[d].action;
			$(id).removeClass(linkDisabled).attr({href:data[d].link});
		}
	}
}