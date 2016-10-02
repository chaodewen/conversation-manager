package tech.moai.mpcm.test;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Test;

import tech.moai.mpcm.message.Message;
import tech.moai.mpcm.persistence.MongoUtils;
import tech.moai.mpcm.setting.MongoSettings;

import java.util.List;

@SuppressWarnings("unused")
public class DBTest {
	@Test
	public void testFind() {
		List<Document> doc = MongoUtils.findDocument(MongoSettings.DATABESE_NAME
				, MongoSettings.BOT_COLLECTION_NAME, Filters.eq("id", 1000));
		System.out.println(doc.get(0).get("_id"));
		System.out.println(doc.get(0).getObjectId("_id").toHexString());
	}
//	@Test
//	public void testUpdate() {
//		MongoUtils.updateDocument(MongoSettings.DATABESE_NAME
//                , MongoSettings.BOT_COLLECTION_NAME, Filters.eq("id", 1000)
//                , Updates.set("ownerid", new ObjectId("5783537922677a5f3471432b")));
//	}
//	@Test
//	public void testDocument() {
//		Message message = new Message("text", "12345", "human"
//				, new ObjectId(), new ObjectId()
//				, new ObjectId(), "out", false);
//		Document doc = message.getDocument();
//		System.out.println(doc);
//	}
//	@Test
//	public void testInsert() {
//		Bot bot = new Bot();
//		bot._id = new ObjectId();
//		bot.ownerid = new ObjectId();
//		bot.nickname = "test";
//		bot.data = "data";
//		
//		JSON.toJSONString(bot, new ValueFilter() {
//			@Override
//			public Object process(Object object, String name, Object value) {
//				if(value instanceof ObjectId) {
//					return ((ObjectId) value).toHexString();
//				}
//				else {
//					return value;
//				}
//			}
//		});
//		System.out.println(JSON.toJSONString(bot));
//		Document doc = Document.parse(JSON.toJSONString(bot));
//		System.out.println(doc);
////		MongoUtils.insertDocument(MongoSettings.DATABESE_NAME
////				, MongoSettings.BOT_COLLECTION_NAME, document);
//	}
}