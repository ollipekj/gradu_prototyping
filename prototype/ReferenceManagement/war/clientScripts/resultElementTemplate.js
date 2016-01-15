
function ResultElement(i, data){
	
	ResultElement.prototype._isEven = function(value){
		if(value % 2 == 0)
			return true;
		else
			return false;
	}
	
	var container = $("<div></div>").addClass("result_list");
	var contentContainer = $("<div></div>").addClass("content_container").attr({id: "element" + i}).append(data.bib);
	var optionContainer = $("<form></form>").addClass("list_options pure-form pure-g");
	var formatOptions = $("<select></select>").addClass("height_auto formatOptions");
	var href = "";
	var text = "";

	if(! ResultElement.prototype._isEven(i))
		container.addClass("even_result_item");

	formatOptions.append(
			"<option value=bib>Citation</option>" +
			"<option value=bibtex>Bibtex</option>" +
			"<option value=ris>RIS</option>");

	if(data["data"].url.length > 0){
		href = data["data"].url;
		text = "Get the publication";
	}else{
		href = "https://scholar.google.fi/scholar" + encodeURI("?hl=en&q=" + data["data"].title);
		text = "Find the publication";
	}

	optionContainer.append($("<div></div>").addClass("pure-u-1-2 pure-control-group").append($("<label>Format&nbsp;&nbsp;</label>").addClass("float_label_left")).append(formatOptions));
	optionContainer.append($("<div></div>").addClass("pure-u-1-2").append($("<label></label>").addClass("float_label_right").append($("<a></a>").attr({href: href, target: "_blank", id: "pub_link"}).text(text))));

	container.append(contentContainer);
	container.append(optionContainer);
	$("#result_field").append(container);

	formatOptions.change(function (){
		contentContainer.html(data[formatOptions.val()]);
	});
}
