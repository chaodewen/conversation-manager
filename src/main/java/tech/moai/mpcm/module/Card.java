package tech.moai.mpcm.module;

import java.util.List;

import tech.moai.mpcm.Utils;

public class Card {
	public String cardSubtitle;
	public String cardTitle;
	public String cardImage;
	public List<Button> buttons;
	
	public boolean isCompleted() {
		return Utils.checkAllFields(this, this.getClass());
	}
}