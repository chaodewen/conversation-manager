package tech.moai.mpcm.conversation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import tech.moai.mpcm.Utils;
import tech.moai.mpcm.bot.Bot;
import tech.moai.mpcm.message.Message;
import tech.moai.mpcm.module.Child;
import tech.moai.mpcm.module.Module;
import tech.moai.mpcm.persistence.MongoUtils;
import tech.moai.mpcm.setting.ManagerSettings;
import tech.moai.mpcm.setting.MongoSettings;
import tech.moai.mpcm.setting.SearchSource;

public class Conversation {
	// 数据库字段
	public ObjectId _id;
	public String created_time;
	public String updated_time;
	public String finished_time;
	public Bot bot;
	public List<ObjectId> message_ids;
	
	// 非数据库字段
	public String session;
	public String key;
	public Module curModule;
	public Message receiveMessage;
	public Message replyMessage;
	public ObjectId user_id;
	public String extractedData;
	public JSONObject customVars;
	
	/**
	 * 根据Bot创建Conversation
	 */
	public Conversation(Integer botID, String session
			, String key) {
		this._id = new ObjectId();
		this.session = session;
		this.key = key;
		this.bot = Bot.getBot(botID);
		this.curModule = Module.getModule(Filters.eq("_id"
				, bot.modules.get(0)));
		this.user_id = bot.ownerid;
		this.created_time = Utils.genTime();
		System.out.println(this._id.toHexString());
	}
	/**
	 * 检查字段完整性
	 * 不包括"receiveMessage", "replyMessage", "updated_time", "finished_time", "extractedData", "customVars"
	 */
	public boolean isValid() {
		return Utils.checkAllFields(this, this.getClass()
				, "receiveMessage", "replyMessage", "updated_time"
				, "finished_time", "extractedData", "customVars"
				, "message_ids");
	}
	/**
	 * 对话未响应时间是否超时
	 */
	public boolean isOverTime() {
		return Instant.now().getEpochSecond() 
				- Utils.genEpochSecond(updated_time)
				> ManagerSettings.CONVERSATION_OVERTIME;
	}
	/**
	 * 每次收到请求时初始化对话状态：清空即将变化的量，生成receiveMessage
	 */
	public boolean init(String rcvText) {
		receiveMessage = null;
		replyMessage = null;
		extractedData = null;
		if(genReceiveMessage(rcvText)) {
			return true;
		}
		return false;
	}
	/**
	 * 生成ReceiveMessage实例
	 * receiveMessage有值时不再重新生成
	 */
	public boolean genReceiveMessage(String rcvText) {
		if(this.receiveMessage.isCompleted()) {
			return true;
		}
		else if(this.isValid()) {
			Message message = new Message(rcvText, session
					, bot.id.toString(), _id, curModule._id
					, bot._id, "in", false);
			if(message.isCompleted()) {
				this.receiveMessage = message;
				this.message_ids.add(message._id);
				return true;
			}
		}
		return false;
	}
	/**
	 * 生成ReplyMessage实例
	 * replyMessage有值时不再重新生成
	 */
	public boolean genReplyMessage() {
		if(this.replyMessage.isCompleted()) {
			return true;
		}
		else if(this.isValid()) {
			Message message = new Message(curModule.genInitResponse()
					, bot.id.toString(), session, _id, curModule._id
					, bot._id, "out", false);
			if(message.isCompleted()) {
				this.replyMessage = message;
				this.message_ids.add(message._id);
				return true;
			}
		}
		return false;
	}
	/**
	 * 为Conversation对象中的extractedData生成内容
	 */
	public Boolean extracted() {
		if(Utils.isValid(extractedData)) {
			return true;
		}
		else {
			// 调用抽取数据接口处理rcvMessage
		}
		return false;
	}
	/**
	 * 有WebHook时发送，没有时直接返回true
	 * 出错时返回false
	 */
	public boolean sendWebHook() {
		if(!Utils.isValid(curModule.webHook)) {
			return true;
		}
		else if(replyMessage.isCompleted() && extracted()) {
			List<NameValuePair> pairList = new ArrayList<>();
			pairList.add(new BasicNameValuePair("from", replyMessage.from));
			pairList.add(new BasicNameValuePair("to", replyMessage.to));
			pairList.add(new BasicNameValuePair("reply", replyMessage.text));
			pairList.add(new BasicNameValuePair("replyData", extractedData));
			pairList.add(new BasicNameValuePair("botID", bot.id.toString()));
			pairList.add(new BasicNameValuePair("moduleID", curModule.id.toString()));
			pairList.add(new BasicNameValuePair("direction", replyMessage.direction));
			// 暂时为空
			pairList.add(new BasicNameValuePair("attachedMedia", ""));
			JSONObject result = Utils.genCustomVars(curModule.webHook, pairList);
			if(result != null) {
				customVars.putAll(result);
				return true;
			}
		}
		return false;
	}
	/**
	 * 跳转
	 */
	public boolean skip() {
		if(this.isValid() && Utils.isValid(this.receiveMessage)) {
			List<Child> children = bot.globalConnections;
			children.addAll(curModule.children);
			for(Child child : children) {
				boolean skip = false;
				if(SearchSource.INPUT_MESSAGE.equals(child.searchSource)) {
					if(Utils.isMatched(receiveMessage.text, child.searchType
							, child.triggers)) {
						skip = true;
					}
				}
				else if(SearchSource.EXTRACTED_DATA.equals(child.searchSource)) {
					if(extracted()) {
						if(Utils.isMatched(extractedData, child.searchType
								, child.triggers)) {
							skip = true;
						}
					}
				}
				else if(SearchSource.CUSTOM_VARS.equals(child.searchSource)) {
					if(Utils.isMatched(receiveMessage.text, child.searchType
							, customVars)) {
						skip = true;
					}
				}
				if(skip) {
					curModule = Module.getModule(Filters.eq(
							"id", child.target));
					return true;
				}
			}
			if(Utils.isValid(this.curModule.elseGoTo)) {
				curModule = Module.getModule(Filters.eq(
						"id", this.curModule.elseGoTo));
				return true;
			}
		}
		return false;
	}
	/**
	 * 以Document类型返回要插入数据库的对象
	 * 出错时返回空的null
	 */
	public Document genDocument() {
		if(isValid()) {
			updated_time = Utils.genTime();
			Document ret = new Document();
			ret.append("_id", _id);
			ret.append("created_time", created_time);
			ret.append("updated_time", updated_time);
			ret.append("finished_time", finished_time);
			ret.append("bot_id", bot._id);
			ret.append("message_ids", message_ids);
			return ret;
		}
		else {
			return null;
		}
	}
	/**
	 * 以Bson类型返回updated_time和message_ids的更新量
	 * 出错时返回空的null
	 */
	public Bson genDocumentUpdates() {
		if(isValid()) {
			List<Bson> updates = new ArrayList<Bson>();
			updated_time = Utils.genTime();
			updates.add(Updates.set("updated_time", updated_time));
			updates.add(Updates.set("message_ids", message_ids));
			return Updates.combine(updates);
		}
		else {
			return null;
		}
	}
	/**
	 * receiveMessage、replyMessage和conversation的持久化
	 */
	public boolean persist() {
		// 插入receiveMessage
		if(!receiveMessage.persist()) {
			return false;
		}
		// 插入replyMessage
		if(!replyMessage.persist()) {
			return false;
		}
		List<Document> list = MongoUtils.findDocument(MongoSettings.DATABESE_NAME
				, MongoSettings.CONVERSATIONS_COLLECTION_NAME
				, Filters.eq("_id", _id));
		if(list.isEmpty()) {
			Document document = genDocument();
			if(document != null) {
				return MongoUtils.insertDocument(MongoSettings.DATABESE_NAME
						, MongoSettings.CONVERSATIONS_COLLECTION_NAME, document);
			}
		}
		else {
			Bson updates = genDocumentUpdates();
			if(updates != null) {
				return MongoUtils.updateDocument(MongoSettings.DATABESE_NAME
						, MongoSettings.CONVERSATIONS_COLLECTION_NAME
						, Filters.eq("_id", _id), updates);
			}
		}
		return false;
	}
}