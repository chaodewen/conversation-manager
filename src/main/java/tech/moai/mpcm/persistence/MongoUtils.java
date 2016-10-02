package tech.moai.mpcm.persistence;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

public class MongoUtils {
	/**
	 * 插入一个Document
	 */
	public static boolean insertDocument(String databaseName
			, String collectionName, Document document) {
		try {
			MongoDatabase mongodb = MongoConnectionFactory
					.getDatabase(databaseName);
			mongodb.getCollection(collectionName).insertOne(
					document);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * 根据filter条件返回List集合，并用projection过滤
	 * 例如filter为Filters.eq("author", author)
	 * projection为Projections.fields(include("api")))
	 */
	public static List<Document> findDocument(String databaseName
			, String collectionName, Bson filter, int limit
			, int skip) {
		MongoDatabase database = MongoConnectionFactory
				.getDatabase(databaseName);
		return database.getCollection(collectionName)
				.find(filter)
				.skip(skip)
				.limit(limit)
				.into(new ArrayList<>());
	}
	/**
	 * 根据filter条件返回List集合，并用projection过滤
	 * 例如filter为Filters.eq("author", author)
	 * projection为Projections.fields(include("api")))
	 */
	public static List<Document> findDocument(String databaseName
			, String collectionName, Bson filter, Bson projection) {
		MongoDatabase database = MongoConnectionFactory
				.getDatabase(databaseName);
		return database
				.getCollection(collectionName).find(filter)
				.projection(projection).into(new ArrayList<>());
	}
	/**
	 * 根据filter条件返回List集合
	 * 例如filter为Filters.eq("author", author)
	 */
	public static List<Document> findDocument(String databaseName
			, String collectionName, Bson filter) {
		MongoDatabase database = MongoConnectionFactory
				.getDatabase(databaseName);
		return database.getCollection(collectionName).find(filter)
				.into(new ArrayList<>());
	}
	/**
	 * 对所有符合filter的字段进行update操作
	 */
	public static boolean updateDocument(String databaseName
			, String collectionName, Bson filter, Bson update) {
		try {
			MongoDatabase database = MongoConnectionFactory
					.getDatabase(databaseName);
			database.getCollection(collectionName).updateMany(
					filter, update);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	/**
	 * 删除所有符合filter的字段
	 */
	public static boolean deleteDocument(String databaseName
			, String collectionName, Bson filter) {
		try {
			MongoDatabase database = MongoConnectionFactory
					.getDatabase(databaseName);
			database.getCollection(collectionName).deleteMany(
					filter);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	/**
	 * 根据FindIterable生成List
	 * 会对ObjectId做处理
	 */
	public static <T> List<T> genList(FindIterable<T> iterable) {
		List<T> results = new ArrayList<T>();
		iterable.forEach(new Block<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public void apply(T t) {
				if(t instanceof Document) {
					Document doc = (Document) t;
					if(doc.containsKey("_id")) {
						final ObjectId id = (ObjectId)doc.get("_id");
						doc.put("_id", id.toHexString());
					}
					results.add((T) doc);
				}
				else
					results.add(t);
			}
		});
		return results;
	}
	/**
	 * 生成用于数据库中存放的Hash后的密码
	 */
	public static String genHashPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt(12));
	}
	/**
	 * 检查输入password是否正确
	 */
	public static boolean verfyHashPassword(String password, String hashPassword) {
		return BCrypt.checkpw(password, hashPassword);
	}
}