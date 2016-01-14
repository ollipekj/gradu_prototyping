package utility;

public class TransferStatus{
	
	private int responseCode;
	private int count = -1;
	private String soleLink = null;
	private String zoteLink = null;
	private String citation = null;
	private String authorParsingStatus;
	private String title;
	
	public TransferStatus(int cd, String aps, String ttl){
		this.responseCode = cd;
		this.authorParsingStatus = aps;			
		this.title = ttl;
	}	
	
	public TransferStatus(){}
	
	public int getResponseCode(){
		return responseCode;
	}
	
	public void setResponseCode(int code){
		this.responseCode = code;
	}
	
	public String getAuthorParsingStatus(){
		return authorParsingStatus;
	}
	
	public void setAuthorParsingStatus(String parsingStatus){
		this.authorParsingStatus = parsingStatus;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String ttl){
		this.title = ttl;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getSoleLink() {
		return soleLink;
	}

	public void setSoleLink(String linkURL) {
		this.soleLink = linkURL;
	}

	public String getCitation() {
		return citation;
	}

	public void setCitation(String citation) {
		this.citation = citation;
	}

	public String getZoteLink() {
		return zoteLink;
	}

	public void setZoteLink(String zoteLink) {
		this.zoteLink = zoteLink;
	}
}
