package tech.moai.mpcm;

import java.util.HashMap;
import java.util.Map;

import tech.moai.mpcm.conversation.Conversation;

public class ConversationPool {
	public static Map<String, Conversation> conversations = new HashMap<>();
	
	public static boolean contains(String session) {
		return conversations.containsKey(session);
	}
	public static boolean add(Conversation conversation) {
		if(conversation.isValid() && !conversations.containsKey(
				conversation.session)) {
			conversations.put(conversation.session, conversation);
			return true;
		}
		return false;
	}
	/**
	 * 出错时返回null
	 */
	public static Conversation get(String session) {
		if(contains(session)) {
			return conversations.get(session);
		}
		return null;
	}
	public static boolean remove(String session) {
		if(conversations.containsKey(session)) {
			conversations.remove(session);
		}
		return true;
	}
}