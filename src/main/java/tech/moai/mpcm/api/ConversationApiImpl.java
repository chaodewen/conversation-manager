package tech.moai.mpcm.api;

import javax.ws.rs.core.Response;

import tech.moai.mpcm.ConversationPool;
import tech.moai.mpcm.Utils;
import tech.moai.mpcm.conversation.Conversation;

public class ConversationApiImpl implements IConversationApi {
	@Override
	public Response getAllConversations(String from, String to) {
		System.out.print("Entered getAllConversations() ...");
		return null;
	}
	
	@Override
	public Response getConversations(String key, int botID, String botType
			, String session, int module, String direction, boolean archived) {
		return null;
	}

	@Override
	public Response messageBot(int botID, String msg, String session
			, String key) {
		System.out.println(botID);
		System.out.println(msg);
		System.out.println(session);
		System.out.println(key);
		// 第一次建立对话
		if(!ConversationPool.contains(session)) {
			Conversation conversation = new Conversation(
					botID, session, key);
			if(!conversation.isValid() || 
					!ConversationPool.add(conversation)) {
				return Utils.genErrorResponse("Args Error", 400);
			}
			// 生成回复
			if(conversation.init(msg) 
					&& conversation.genReplyMessage()) {
				return Utils.genMessageResponse(conversation);
			}
			return Utils.genErrorResponse("Message Generating Error ", 400);
		}
		// 之前已经建立好对话
		Conversation conversation = ConversationPool.get(session);
		// 清空残留值，生成receiveMessage
		conversation.init(msg);
		// 提取数据
		if(conversation.extracted()) {
			// 发送WebHook，不考虑成功与否
			conversation.sendWebHook();
			// 进行跳转并生成回复
			if(conversation.skip() && conversation.genReplyMessage()) {
				return Utils.genMessageResponse(conversation);
			}
		}
		// 返回failureResponse
		return Utils.genFailureResponse(conversation, "Unknown Error", 400);
	}

	@Override
	public Response messageHuman(String to, int botID, String msg, String key) {
		// TODO Auto-generated method stub
		return null;
	}
}