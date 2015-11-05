package utility.parsers.HTMLParsers.parsingStrategies;

import java.util.Map;

import org.jsoup.nodes.Document;

import utility.Constants;
import utility.DataMappings.ItemField;
import utility.parsers.HTMLParsers.SolecrisParser;

public class BookParsingStrategy  extends SolecrisParser implements ParsingStrategy {

	public BookParsingStrategy(Document d){
		super.doc= d;
	}

	@Override
	public void parse(String type) {
		resolveConstantFields(type);
		resolveOptionalFields();
	}

	private void resolveOptionalFields(){
		
		String bookKey = Constants.BOOK_TITLE;
		String editorKey = Constants.EDITOR;
		String jufoPubKey = Constants.JUFO_EDITOR;
		
		addOptionalItemField(bookKey);
		addOptionalItemField(editorKey);		
		addOptionalItemField(jufoPubKey);	
	}
	
	@Override
	public Map<String, ItemField> getResults() {
		return mappings.itemFields;
	}
}