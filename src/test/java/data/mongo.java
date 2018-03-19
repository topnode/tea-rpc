package data;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.util.JSON;

import data.data.Action;

public class mongo {

	private static MongoClient mongoClient = null;

	@SuppressWarnings("deprecation")
	public static DB getDB() throws UnknownHostException {

				DB conn = null;
				if (mongoClient == null) {
					intializeMongoClient();
				}
				
				String dbName ="";// AppConfig.getValue("mongo_db");
				// String username = AppConfig.getValue(Const.MONGODB_USERNAME);
				// String password = AppConfig.getValue(Const.MONGODB_PASSWORD);
				conn = mongoClient.getDB(dbName);
				// conn.authenticate(username, password.toCharArray());
				return conn;

	}

	private static void intializeMongoClient() throws UnknownHostException {

		String host ="";// AppConfig.getValue("mongo_host");
		int port =0;// AppConfig.getIntValue("mongo_port");
		mongoClient = new MongoClient(host, port);

	}

	public static synchronized void closeConnection() {

		if (mongoClient != null) {
			mongoClient.close();
		}
		
	}

	public static List<DBObject> insertOne(Action action) {

		List<DBObject> ret = new ArrayList<DBObject>();
		BasicDBObject object = (BasicDBObject) JSON.parse(action.getFiter());
		try {
			DBCursor curor = mongo.getDB().getCollection(action.getSchema()).find(object);
			while (curor.hasNext()) {
				ret.add(curor.next());
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;

	}

	
	public static List<DBObject> updateOne(Action action) {

		List<DBObject> ret = new ArrayList<DBObject>();
		BasicDBObject object = (BasicDBObject) JSON.parse(action.getFiter());
		try {
			DBCursor curor = mongo.getDB().getCollection(action.getSchema()).find(object);
			while (curor.hasNext()) {
				ret.add(curor.next());
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;

	}

	public static List<DBObject> deleteOne(Action action) {

		List<DBObject> ret = new ArrayList<DBObject>();
		BasicDBObject object = (BasicDBObject) JSON.parse(action.getFiter());
		try {
			DBCursor curor = mongo.getDB().getCollection(action.getSchema()).find(object);
			while (curor.hasNext()) {
				ret.add(curor.next());
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;

	}

	public static List<DBObject> queryOne(Action action) {

		List<DBObject> ret = new ArrayList<DBObject>();
		BasicDBObject object = (BasicDBObject) JSON.parse(action.getFiter());
		try {
			DBCursor curor = mongo.getDB().getCollection(action.getSchema()).find(object);
			while (curor.hasNext()) {
				ret.add(curor.next());
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;

	}
	
	public static List<DBObject> queryPage(Action action,int pageNo,int pageSize) {

		List<DBObject> ret = new ArrayList<DBObject>();
		BasicDBObject object = (BasicDBObject) JSON.parse(action.getFiter());
		try {
			DBCursor curor = mongo.getDB().getCollection(action.getSchema()).find(object);
			while (curor.hasNext()) {
				ret.add(curor.next());
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;

	}
	
	public static List<DBObject> queryAll(Action action) {

		List<DBObject> ret = new ArrayList<DBObject>();
		BasicDBObject object = (BasicDBObject) JSON.parse(action.getFiter());
		try {
			DBCursor curor = mongo.getDB().getCollection(action.getSchema()).find(object);
			while (curor.hasNext()) {
				ret.add(curor.next());
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;

	}

	public static String excute(Action action) {

		switch (action.getType()) {
		case 0:
			BasicDBObject object = data.getSchema(action.getSchema()).clone();
			BasicDBObject replace = (BasicDBObject) JSON.parse(action.getUpdate());

			for (Entry<String, Object> entry : replace.entrySet()) {
				object.replace(entry.getKey(), entry.getValue());
			}
			try {
				mongo.getDB().getCollection(action.getSchema()).insert(object);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(object.toJson());
			break;
		case 2:
			System.out.println(mongo.queryAll(action).toString());
			break;

		}
		return "";

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
		// MongoClient client = new MongoClient("localhost", 27017);
		// MongoIterable<String> l = client.listDatabaseNames();
		// Iterator<String> it = l.iterator();
		// while (it.hasNext()) {
		// System.out.println(it.next());
		// }
		// MongoDatabase render_system = client.getDatabase("sns");
		// MongoCollection<Document> user =
		// render_system.getCollection("sns_user");

		// try {
		// DB db=mongo.getDB();
		// DBCollection coll=db.getCollection("test");
		// coll.insert(new BasicDBObject("",""));
		// } catch (UnknownHostException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		data.load();
		Map<String, String> set = new HashMap<String, String>();
		set.put("id", "50000");
		data.getAction("addSnsUserOnPhone").replaceUpdate(set);
		mongo.excute(data.getAction("addSnsUserOnPhone"));
		//System.out.println(data.getSchema("sns_user").clone().toJson());

		// render_system.getCollection("sns_user");
		// MongoDatabase admin = client.getDatabase("admin");
		// MongoCollection<Document> cc=admin.getCollection("test");
		// Document doc = Document
		// .parse("{user: \"abc\",pwd: \"abc123\",roles: [ { role:
		// \"readWrite\", db: \"abcdb\" } ]}");
		// cc.insertOne(doc);
		
	}

}
