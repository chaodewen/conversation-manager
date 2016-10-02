package tech.moai.mpcm.module;

import java.time.Instant;
import java.util.List;
import java.util.Random;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import tech.moai.mpcm.Utils;
import tech.moai.mpcm.persistence.MongoUtils;
import tech.moai.mpcm.setting.MongoSettings;

public class Module {
	public ObjectId _id;
	public Integer id;
	// 外键->数据库里bot的_id
	public ObjectId ownerid;
	// [datetime, duration, emailaddress, geoaddress, getnumbers, multiplechoice, name, parseurl, phonenumber, sentiment, welcome]
	public String moduleType;
	public List<String> initialResponse;
	public String nickname;
	// ['human', 'bot', 'chatbot']
	public String options;
	public List<String> failureResponse;
	public String visualPosition;
	// module id
	public Integer elseGoTo;
	public Boolean immediateNext;
	public String webHook;
	public List<String> multipleChoiceOptions;
	public List<Card> cards;
	public List<Child> children;
	
	/**
	 * "multipleChoiceOptions", "visualPosition", "webHook", "elseGoTo"可以为空
	 * 其余字段均不为空，initialResponse必须有元素
	 */
	public boolean isCompleted() {
		return Utils.checkAllFields(this, this.getClass()
				, "multipleChoiceOptions", "visualPosition"
				, "webHook", "elseGoTo") && !initialResponse.isEmpty();
	}
	/**
	 * 随机返回initialResponse中的一个
	 * 出错时返回null
	 */
	public String genInitResponse() {
		if(this.initialResponse.size() > 0) {
			Random random = new Random();
			random.setSeed(Instant.now().getEpochSecond());
			int index = random.nextInt(this.initialResponse.size() - 1);
			return this.initialResponse.get(index);
		}
		return null;
	}
	/**
	 * 随机返回failureResponse中的一个
	 * 出错时返回null
	 */
	public String genFailureResponse() {
		if(this.failureResponse.size() > 0) {
			Random random = new Random();
			random.setSeed(Instant.now().getEpochSecond());
			int index = random.nextInt(this.failureResponse.size() - 1);
			return this.failureResponse.get(index);
		}
		return null;
	}
	/**
	 * 通过_id从数据库得到module对象，若对象为多个或不存在则返回null
	 */
	public static Module getModule(Bson bson) {
		List<Document> documents = MongoUtils.findDocument(
				MongoSettings.DATABESE_NAME
				, MongoSettings.MODULE_COLLECTION_NAME
				, bson);
		if(documents.size() == 1) {
			JSONObject json = JSON.parseObject(documents.get(0).toJson());
			Module module = JSON.parseObject(json.toJSONString(), Module.class);
			if(module.isCompleted()) {
				return module;
			}
		}
		return null;
	}
}