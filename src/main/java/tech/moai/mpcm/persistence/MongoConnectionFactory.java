package tech.moai.mpcm.persistence;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import tech.moai.mpcm.setting.MongoSettings;

public class MongoConnectionFactory {
	private static MongoClient mongoClient = null;
	
	private MongoConnectionFactory() {
		super();
	}
	
	private static synchronized MongoClient getMongoClient() {
		if (mongoClient == null) {
			mongoClient = new MongoClient(new MongoClientURI(
					MongoSettings.CONNECTION_STRING));
		}
		return mongoClient;
	}
	
	public static MongoDatabase getDatabase(String databaseName) {
		return MongoConnectionFactory.getMongoClient().getDatabase(databaseName);
	}
}
