package tech.moai.mpcm.module;

import tech.moai.mpcm.Utils;

public class Button {
	public String buttonText;
	// 可选module或者url
	public String buttonType;
	public String target;
	
	public boolean isCompleted() {
		return Utils.checkAllFields(this, this.getClass());
	}
}