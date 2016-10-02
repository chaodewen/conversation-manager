package tech.moai.mpcm.bot;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.model.Filters;

import tech.moai.mpcm.Utils;
import tech.moai.mpcm.module.Child;
import tech.moai.mpcm.persistence.MongoUtils;
import tech.moai.mpcm.setting.MongoSettings;

public class Bot {
	public ObjectId _id;
	// 外键->user表的_id
	public ObjectId ownerid;
	// 外键->module表主键_id
	public List<ObjectId> modules;
	// 外键->global_connection表主键_id
	public List<Child> globalConnections;
	// ['web', 'wechat', 'api', 'app']
	public String deploy_type;
	public String nickname;
	public String data;
	public Integer id;

	@Override
	public String toString() {
		return "Bot{" +
				"_id=" + _id.toHexString() +
				", ownerid=" + ownerid +
				", modules=" + modules +
				", globalConnections=" + globalConnections +
				", deploy_type='" + deploy_type + '\'' +
				", nickname='" + nickname + '\'' +
				", data='" + data + '\'' +
				", id=" + id +
				'}';
	}

	/**
	 * 字段均不为空或0
	 * List的内容也会被检查
	 * modules至少有一个元素
	 */
	@JSONField(serialize=false)
	public boolean isCompleted() {
		return Utils.checkAllFields(this, this.getClass())
				&& modules.size() > 0;
	}
	/**
	 * 通过id从数据库得到Bot对象，若对象为多个或不存在则返回null
	 */
	public static Bot getBot(int id) {
		// 从数据库取得Bot
		List<Document> documents = MongoUtils.findDocument(
				MongoSettings.DATABESE_NAME
				, MongoSettings.BOT_COLLECTION_NAME
				, Filters.eq("id", id));
		if(documents.size() == 1) {
			Document doc = documents.get(0);
			System.out.println(doc);
			System.out.println(doc.toJson());
			JSONObject json = JSON.parseObject(documents.get(0).toJson());
			Bot bot = JSON.parseObject(json.toJSONString(), Bot.class);
			if(bot.isCompleted()) {
				return bot;
			}
		}
		return null;
	}

	public void set_id(String _id) {
		this._id = new ObjectId(_id);
	}
}