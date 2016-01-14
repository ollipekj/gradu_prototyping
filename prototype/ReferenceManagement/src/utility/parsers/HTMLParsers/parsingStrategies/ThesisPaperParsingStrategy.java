package utility.parsers.HTMLParsers.parsingStrategies;

import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import utility.Constants;
import utility.DataMappings.ItemField;

public class ThesisPaperParsingStrategy extends SolecrisParser implements ParsingStrategy {

	public ThesisPaperParsingStrategy(Document d){
		super.doc= d;
	}

	@Override
	public void parse(String type) {
		resolveConstantFields(type);
		resolveOptionalFields();
	}
	
	private void resolveOptionalFields(){
		 Element thesisTypeElement = doc.select("span[id*=ajax_info.state_label.cr_suorite.lajpoim_radio.34]").first().parent().parent().parent().children().select("td").first();
         String thesisType = thesisTypeElement.text();
         includeOptionalItem(Constants.THESIS_TYPE);
         saveData(Constants.THESIS_TYPE, thesisType);
	}

	@Override
	public Map<String, ItemField> getResults() {
		return mappings.itemFields;
	}
}
