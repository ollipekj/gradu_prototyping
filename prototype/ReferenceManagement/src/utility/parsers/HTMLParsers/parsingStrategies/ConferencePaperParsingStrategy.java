package utility.parsers.HTMLParsers.parsingStrategies;

import java.util.Map;

import org.jsoup.nodes.Document;

import utility.Constants;
import utility.DataMappings.ItemField;

public class ConferencePaperParsingStrategy extends SolecrisParser implements ParsingStrategy {

	public ConferencePaperParsingStrategy(Document d){
		super.doc= d;
	}

	@Override
	public void parse(String type) {
		resolveConstantFields(type);
		resolveOptionalFields();
	}
	
	private void resolveOptionalFields(){

		String proceedingsKey = Constants.PROCEEDINGS_NAME;
		String publisherKey = Constants.EDITOR;
		String jufoPubKey = Constants.JUFO_EDITOR;
		
		addOptionalItemField(proceedingsKey);
		addOptionalItemField(publisherKey);
		addOptionalItemField(jufoPubKey);		
	}

	@Override
	public Map<String, ItemField> getResults() {
		return mappings.itemFields;
	}
}
