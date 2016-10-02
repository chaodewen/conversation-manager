package tech.moai.mpcm.message;

import java.lang.reflect.Field;
import org.bson.Document;
import org.bson.types.ObjectId;

import tech.moai.mpcm.Utils;
import tech.moai.mpcm.persistence.MongoUtils;
import tech.moai.mpcm.setting.MongoSettings;

public class Message {
	public ObjectId _id;
	public String text;
	// [ '用户session', 'bot_id', 'dashboard' ]
	public String from;
	public String to;
	public String updated_at;
	public ObjectId conversation_id;
	// 外键module表主键_id
	public ObjectId module_id;
	// 外键bot表主键_id
	public ObjectId bot_id;
	public String direction;
	public Boolean archived;
	
	public Message(String text, String from, String to
			, ObjectId conversation_id, ObjectId module_id
			, ObjectId bot_id, String direction
			, Boolean archived) {
		this._id = new ObjectId();
		this.text = text;
		this.from = from;
		this.to = to;
		this.updated_at = Utils.genTime();
		this.conversation_id = conversation_id;
		this.module_id = module_id;
		this.bot_id = bot_id;
		this.direction = direction;
		this.archived = archived;
	}
	/**
	 * 字段均不为空或0
	 */
	public boolean isCompleted() {
		return Utils.checkAllFields(this, this.getClass());
	}
	/**
	 * 以Document类型返回对象，不包括文件流
	 * 出错时返回空的null
	 */
	public Document getDocument() {
		if(isCompleted()) {
			Document ret = new Document();
			try {
				Field[] fields = this.getClass().getDeclaredFields();
				for(Field field : fields) {
					ret.put(field.getName(), field.getType().cast(field.get(this)));
				}
				return ret;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 持久化到数据库
	 */
	public boolean persist() {
		Document document = getDocument();
		if(document != null) {
			if(MongoUtils.insertDocument(MongoSettings.DATABESE_NAME
					, MongoSettings.MESSAGE_COLLECTION_NAME, document)) {
				return true;
			}
		}
		return false;
	}
}