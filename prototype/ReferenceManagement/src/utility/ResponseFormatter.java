package utility;

public class ResponseFormatter {

	StringBuilder builder;
	
	public String createHTMLFormattedResponse(TransferStatus status){
				
		builder = new StringBuilder();
		builder.append("<br>");
		builder.append(status.getCount());
		builder.append(". Status code: " + status.getResponseCode());
		
		builder.append("<br>Data transfer from ");
		builder.append("<a target='blank' href='" + status.getSoleLink() + "' a>");
		builder.append(status.getSoleLink() + "</a>: ");
		
		if(status.getZoteLink() != null){
			builder.append("<br>Stored in: ");
			builder.append("<a target='blank' href='" + status.getZoteLink() + "' a>");
			builder.append(status.getZoteLink() + "</a>: ");	
		}
		
		if(status.getAuthorParsingStatus() != ""){
			builder.append("<br>Author parsing notification: ");
			builder.append(status.getAuthorParsingStatus());
		}		
		return builder.toString();
	}
	
	public String createIncompleteLinkNotification(TransferStatus status){
		
		builder = new StringBuilder();
		builder.append(status.getCount());
		builder.append(". Missing link for an article requires manual input: ");
		builder.append(status.getCitation());
		
		return builder.toString();
	}
}
