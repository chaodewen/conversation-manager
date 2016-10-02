package tech.moai.mpcm.module;

import java.util.List;

import tech.moai.mpcm.Utils;

public class Child {
	// ['human', 'bot']
	public List<String> triggers;
	public String webhook;
	public Integer target;
	// ['inputMessage', 'extractedData', 'customVars']
	public String searchSource;
	// ['matches', 'containsAll', 'notContains', 'exactMatch', 'startsWith', 'endsWith', 'notStartsWith', 'notEndsWith']
	public String searchType;
	
	/**
	 * 字段均不为空或0
	 */
	public boolean isCompleted() {
		return Utils.checkAllFields(this, this.getClass());
	}
}