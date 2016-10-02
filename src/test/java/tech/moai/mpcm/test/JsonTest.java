package tech.moai.mpcm.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.asm.Type;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.alibaba.fastjson.parser.deserializer.ExtraTypeProvider;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.JavaBeanInfo;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.types.ObjectId;
import org.junit.Test;
import tech.moai.mpcm.bot.Bot;
import tech.moai.mpcm.persistence.MongoUtils;
import tech.moai.mpcm.setting.MongoSettings;

public class JsonTest {
    @Test
    public void testBot() {
        Bot bot = Bot.getBot(1000);
        System.out.println(bot);
    }
    @Test
    public void testJson() {
        ParserConfig.getGlobalInstance().putDeserializer(
                Bot.class, new ObjectDeserializer() {
                    @Override
                    public <T> T deserialze(DefaultJSONParser parser, java.lang.reflect.Type type, Object fieldName) {
                        System.out.println(fieldName);
                        System.out.println(parser.getLexer());
                        return (T) parser.getLexer().stringVal();
                    }
                    @Override
                    public int getFastMatchToken() {
                        return 0;
                    }
                });

        String json = "{\"data\":{},\"global_connections\":[],\"nickname\":\"TestWebBot\",\"_id\":{\"$oid\":\"578f7fb322677a44881f5f83\"},\"deploy_type\":\"web\",\"ownerid\":{\"$oid\":\"5783537922677a5f3471432b\"},\"modules\":[{\"$oid\":\"5796ec6622677a224891391f\"},{\"$oid\":\"5796ed2722677a4d38975236\"}]}";
        System.out.println(json);
        JSONObject jo = JSON.parseObject(json);
        System.out.println(jo.toJSONString());

//        Bot bot = JSON.parseObject(json, Bot.class, new ExtraProcessor() {
//            @Override
//            public void processExtra(Object object, String key, Object value) {
////                System.out.println(object + " : " + key + " : " + value);
////                System.out.println(object instanceof ObjectId);
////                System.out.println(value instanceof  String);
////                System.out.println(((ObjectId) object).toHexString());
////                System.out.println(new ObjectId((String) value).toHexString());
//            }
//        });
//        Bot bot = JSON.parseObject(json, Bot.class, new ExtraTypeProvider() {
//            @Override
//            public java.lang.reflect.Type getExtraType(Object object, String key) {
////                if("$oid".equals(key)) {
////                    return ObjectId.class;
////                }
////                return null;
//                System.out.println(object instanceof ObjectId);
//                System.out.println(key);
//                return null;
//            }
//        });

        Bot bot = JSON.parseObject(json, Bot.class);
        System.out.println(bot);

//        ObjectId oid = new ObjectId("578f7fb322677a44881f5f83");
//        JSONObject jo = new JSONObject();
//        jo.put("_id", oid);
//        System.out.println(jo.toJSONString());
//        Bot bot = JSON.parseObject(jo.toJSONString(), Bot.class);
//        System.out.println(bot);
    }
}