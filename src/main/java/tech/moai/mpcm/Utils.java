package tech.moai.mpcm;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import tech.moai.mpcm.bot.Bot;
import tech.moai.mpcm.conversation.Conversation;
import tech.moai.mpcm.message.Message;
import tech.moai.mpcm.module.Button;
import tech.moai.mpcm.module.Card;
import tech.moai.mpcm.module.Child;
import tech.moai.mpcm.module.Module;
import tech.moai.mpcm.setting.SearchType;

public class Utils {
	/**
	 * 发送Webhook生成CustomVars，出错时返回null
	 */
	public static JSONObject genCustomVars(String url
			, List<NameValuePair> pairList) {
		CloseableHttpResponse response = sendPost(url, pairList);
		int status = response.getStatusLine().getStatusCode(); 
		if(status >= 200 && status < 300) {
			try {
				String entity = EntityUtils.toString(response.getEntity()
						, "UTF-8");
				return JSON.parseObject(entity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 根据searchType查看customVars中的内容与text是否匹配
	 */
	public static boolean isMatched(String text, String searchType
			, JSONObject customVars) {
		List<String> triggers = new ArrayList<>();
		for(Object object : customVars.values()) {
			if(object instanceof String) {
				triggers.add((String) object);
			}
		}
		return isMatched(text, searchType, triggers);
	}
	/**
	 * 根据searchType查看triggers中的内容与text是否匹配
	 */
	public static boolean isMatched(String text, String searchType
			, List<String> triggers) {
		switch(searchType) {
		case SearchType.MATCHES:
			for(String trigger : triggers) {
				if(text.contains(trigger)) {
					return true;
				}
			}
		case SearchType.CONTAINS_ALL:
			for(String trigger : triggers) {
				if(!text.contains(trigger)) {
					return false;
				}
			}
			return true;
		case SearchType.NOT_CONTAINS:
			for(String trigger : triggers) {
				if(text.contains(trigger)) {
					return false;
				}
			}
			return true;
		case SearchType.EXACT_MATCH:
			for(String trigger : triggers) {
				if(text.equals(trigger)) {
					return true;
				}
			}
			return false;
		case SearchType.STARTS_WITH:
			for(String trigger : triggers) {
				if(text.startsWith(trigger)) {
					return true;
				}
			}
			return false;
		case SearchType.ENDS_WITH:
			for(String trigger : triggers) {
				if(text.endsWith(trigger)) {
					return true;
				}
			}
			return false;
		case SearchType.NOT_STARTS_WITH:
			for(String trigger : triggers) {
				if(text.startsWith(trigger)) {
					return false;
				}
			}
			return true;
		case SearchType.NOT_ENDS_WITH:
			for(String trigger : triggers) {
				if(text.endsWith(trigger)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	/**
	 * 检查clazz的object对象的所有字段是否满足非空切不为0
	 * 除过变量名在names中的字段
	 */
	public static boolean checkAllFields(Object object
			, Class<?> clazz, String ... names) {
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			try {
				boolean inNames = false;
				for(String name : names) {
					if(field.getName().equals(name)) {
						inNames = true;
						break;
					}
				}
				if(!inNames && !Utils.isValid(
						field.get(object))) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	/**
	 * object不为null
	 * object类型是String时不为空
	 * object类型是Integer时不等于0
	 * object类型是List时每个元素递归调用isValid
	 * object类型是Child, Card, Button等对象时调用isCompleted()
	 * object类型是Boolean时始终返回true
	 */
	public static boolean isValid(Object ... objects) {
		for(Object object : objects) {
			if(object == null) {
				return false;
			}
			else {
				if(object instanceof List<?>) {
					for(Object obj : (List<?>) object) {
						if(!isValid(obj)) {
							return false;
						}
					}
				}
				else if(object instanceof Module) {
					if(!((Module) object).isCompleted()) {
						return false;
					}
				}
				else if(object instanceof Bot) {
					if(!((Bot) object).isCompleted()) {
						return false;
					}
				}
				else if(object instanceof Child) {
					if(!((Child) object).isCompleted()) {
						return false;
					}
				}
				else if(object instanceof Card) {
					if(!((Card) object).isCompleted()) {
						return false;
					}
				}
				else if(object instanceof Button) {
					if(!((Button) object).isCompleted()) {
						return false;
					}
				}
				else if(object instanceof Message) {
					if(!((Message) object).isCompleted()) {
						return false;
					}
				}
				else if(object instanceof String) {
					if(((String) object).isEmpty()) {
						return false;
					}
				}
				else if(object instanceof Integer) {
					if(((Integer) object) == 0) {
						return false;
					}
				}
			}
		}
		return true;
	}
	/**
	 * 生成failureResponse
	 * 正常返回会调用持久化方法
	 * 如果没有预先设置failureResponse就生成一个错误的Response
	 * reason是原因，code是状态码
	 */
	public static Response genFailureResponse(Conversation conversation
			, String reason, int code) {
		String entity = conversation.curModule.genFailureResponse();
		if(entity != null) {
			return Utils.genMessageResponse(entity, conversation);
		}
		return Utils.genErrorResponse(reason, code);
	}
	/**
	 * 生成回复错误的Response
	 */
	public static Response genErrorResponse(String err
			, int code) {
		ResponseBuilder rb = Response.status(code);
		
		rb.header("Content-Type", "application/json; charset=UTF-8");
		rb.header("Content-Encoding", "UTF-8");
		
		JSONObject entity = new JSONObject();
		entity.put("err", err);
		entity.put("code", code);
		rb.entity(entity.toJSONString());
		
		return rb.build();
	}
	/**
	 * 生成回复并调用持久化方法
	 */
	public static Response genMessageResponse(String botResponse
			, Conversation conversation) {
		if(conversation.isValid() && Utils.isValid(
				conversation.receiveMessage)) {
			if(conversation.persist()) {
				ResponseBuilder rb = Response.status(200);
				
				rb.header("Content-Type", "application/json; charset=UTF-8");
				rb.header("Content-Encoding", "UTF-8");
				
				JSONObject entity = new JSONObject();
				entity.put("botResponse", botResponse);
				entity.put("inReplyTo", conversation.receiveMessage.text);
				entity.put("session", conversation.session);
				entity.put("code", 200);
				rb.entity(entity.toJSONString());
				
				return rb.build();
			}
			else {
				return Utils.genErrorResponse("Persistence Error", 500);
			}
		}
		return Utils.genErrorResponse("Response Generating Error", 500);
	}
	/**
	 * 生成回复Message的Response
	 * 正常返回时会调用持久化方法
	 */
	public static Response genMessageResponse(Conversation conversation) {
		if(conversation.isValid() && Utils.isValid(conversation.replyMessage)) {
			return genMessageResponse(conversation.replyMessage.text
					, conversation);
		}
		return Utils.genErrorResponse("Response Generating Error", 500);
	}
	/**
	 * 生成当前时间，位置是中国
	 * 格式是yyyy-MM-dd HH:mm:ss
	 */
	public static String genTime() {
		return LocalDateTime.now().format(
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"
						, Locale.CHINA));
	}
	/**
	 * 解析当前genTime()生成的LocalDateTime字符串
	 * 返回epoch秒
	 * 时区是东八区
	 */
	public static long genEpochSecond(String time) {
		LocalDateTime localDateTime = LocalDateTime.parse(
				time, DateTimeFormatter.ofPattern(
						"yyyy-MM-dd HH:mm:ss", Locale.CHINA));
		return localDateTime.toEpochSecond(ZoneOffset.ofHours(8));
	}
	/**
	 * 发送HTTP/HTTPS的POST请求，出错时返回null
	 */
	public static CloseableHttpResponse sendPost(String url
			, List<NameValuePair> pairList) {
		CloseableHttpResponse response;
		try {
			// 实现将请求的参数封装到表单中，即请求体中
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
					pairList, "UTF-8");
			// 使用post方式提交数据
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(entity);
			// 执行post请求，并获取服务器端的响应HttpResponse
			CloseableHttpClient httpClient;
			if(url.startsWith("https")) {
				// https
				SSLContext sslContext = SSLContexts.custom()
						.loadTrustMaterial(null, new TrustSelfSignedStrategy())
						.useTLS()
						.build();
				SSLConnectionSocketFactory sslSocketFactory = 
						new SSLConnectionSocketFactory(sslContext
								, new AllowAllHostnameVerifier());
				httpClient = HttpClients.custom().setSSLSocketFactory(
						sslSocketFactory).build();
			}
			else {
				httpClient = HttpClients.createDefault();
			}
			response = httpClient.execute(httpPost);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return response;
	}
}