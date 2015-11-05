/**
 * 
 */

var channel = new Object();
var resultField = new Object();

$(document).ready(function(){
	resultField = $("#log_field");	
	getToken();
});

function onOpened() {
	 resultField.append("<br>Ready to receive messages from server");
}

function onMessage(msg) {
    resultField.append(msg.data + "<br>");
}

function onError(err) {
	 resultField.append(err.data + "<br>");
}

function onClose() {
    resultField.append("<br>Channel closed");
}

function initializeChannel(tkn){
	
	channel = new goog.appengine.Channel(tkn);
    var socket = channel.open();
    
    socket.onopen = onOpened;
    socket.onmessage = onMessage;
    socket.onerror = onError;
    socket.onclose = onClose;
}

function getToken(){

	var data = {tokenSeed:"yyz"};

	getAjax("handleChannel", "GET", data, "json")
	.done(function(data){
		
		var token = data.token;
		initializeChannel(token);		

	}).fail(function(){
		$("#info_box").append("Request failed.");
	});;
}

function getAjax(url, type, data, dataType){
	
	return $.ajax({
		url: url,
		type: type,
		data: data,
		dataType: dataType
	})
}