package data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

/*
 * 
 * 模块名:NOSQL mongodb数据源
 * 开发者:王鹏
 * 日期:2018/1/17
 * 注意:访问NOSQL数据源
 * 
 */
public final class mongo {

	
	/**
	 * MongoClient的实例代表数据库连接池，是线程安全的，可以被多线程共享，客户端在多线程条件下仅维持一个实例即可
	 * Mongo是非线程安全的，目前mongodb API中已经建议用MongoClient替代Mongo
	 */
	private static MongoClient mongoClient = null;
	private static String SERVER_HOST = "localhost";
	private static int SERVER_PORT = 27017;// SnsConfig.getIntCnf("mongo_port");
	private static String SERVER_AUTH = "admin";
	
	private static String DB_NAME="sns";

	//
	public static void load() {

		if (mongoClient == null) {

			java.util.logging.Logger log = java.util.logging.Logger.getLogger("org.mongodb.driver");
			log.setLevel(Level.OFF);

		
			MongoClientOptions.Builder build = new MongoClientOptions.Builder();
			build.connectionsPerHost(50); // 与目标数据库能够建立的最大connection数量为50
			build.threadsAllowedToBlockForConnectionMultiplier(50); // 如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
			/*
			 * 一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为2分钟
			 * 这里比较危险，如果超过maxWaitTime都没有获取到这个连接的话，该线程就会抛出Exception
			 * 故这里设置的maxWaitTime应该足够大，以免由于排队线程过多造成的数据库访问失败
			 */
			build.maxWaitTime(1000 * 60 * 2);
			build.connectTimeout(1000 * 60 * 1); // 与数据库建立连接的timeout设置为1分钟

			MongoClientOptions myOptions = build.build();
			try {
 				// 数据库连接实例
				mongoClient = new MongoClient(SERVER_HOST, SERVER_PORT);

			} catch (MongoException e) {
				e.printStackTrace();
			}

		}

	}

	public static DB getDB(String dbName) {
		return mongoClient.getDB(dbName);
	}
	
	
	
    public static boolean insertOne(data.Action action) {

    	data.Schema  schema=data.getSchema(action.getSchema());
    	//logger.info(action.getSchema());
    	BasicDBObject proto=(BasicDBObject) JSON.parse(schema.getDefine());
    	BasicDBObject object=(BasicDBObject) proto.clone();
    	if(action.getValues()!=null){
    	   BasicDBObject values=(BasicDBObject) JSON.parse(action.getValues());
    	  // logger.info("values:"+values.toJson());
    	   for(Entry<String,Object> entry:values.entrySet()){
    		   object.replace(entry.getKey(), entry.getValue());
    	   }
    	}

    	
    	//logger.info("insert:"+object.toJson());

		DB db = mongo.getDB(DB_NAME);
		DBCollection set = db.getCollection(action.getSchema());
		//WriteResult update=null;
		try {
		  set.insert(object,WriteConcern.ACKNOWLEDGED);
		   //System.out.println("insert N:"+update.getN());
		}catch(MongoException e) {
			return false;
		}
		
		return true;
	}
    
	public static DBObject queryOne(data.Action action){
		
		DB db = mongo.getDB(DB_NAME);
		BasicDBObject filterObject = parseFilters(action.getFilter());
		BasicDBObject queryKey = parseSelects(action.getResult(),action.isGiveup());
		
		DBCollection set = db.getCollection(action.getSchema());
		
//		DBCursor cur=set.find();
//		while(cur.hasNext()){
//		logger.info(cur.next().toString());
//		}
		
		DBObject object = set.findOne(filterObject,queryKey);
		
		return object;
		
	}
	
	public static DBObject deleteOne(data.Action action){
		
		DB db = mongo.getDB(DB_NAME);
		BasicDBObject filterObject = parseFilters(action.getFilter());
		//BasicDBObject queryKey = parseSelects(action.getResult(),action.isGiveup());

		
		DBCollection set = db.getCollection(action.getSchema());

		return set.findAndRemove(filterObject);
		
	}
	
	public static WriteResult updateOne(data.Action action){
		
		DB db = mongo.getDB(DB_NAME);

		//logger.info(db.getCollectionNames());
		BasicDBObject filter=(BasicDBObject) JSON.parse(action.getFilter());
		BasicDBObject values=(BasicDBObject) JSON.parse(action.getValues());
		//logger.info("update:"+action.getSchema());
		DBCollection set = db.getCollection(action.getSchema());
	
//		logger.info("update values:"+values.toJson());
//		logger.info("update filter:"+filter.toJson());
		WriteResult update = set.update(filter,new BasicDBObject("$set",values),false,false,WriteConcern.MAJORITY);
		return update;

	
	}
	
	//
    public static List<DBObject> aggregate(data.Action action){
    	
    	List<DBObject>  list = new ArrayList<DBObject>();
    
    	BasicDBList opts=(BasicDBList)JSON.parse(action.getFilter());
    	
    	List<BasicDBObject>  pipeline= new ArrayList<BasicDBObject>();
    	for(int i=0;i<opts.size();i++){
    		pipeline.add((BasicDBObject)opts.get(i));
    	}
    	
    	DB db = mongo.getDB("sns");
		//logger.info(db.getCollectionNames());
		DBCollection set = db.getCollection(action.getSchema());
		AggregationOutput output=set.aggregate(pipeline);
	
		Iterator<DBObject> it=output.results().iterator();
		while(it.hasNext()){
				list.add(it.next());
		}
		//logger.info(list);
		return list;
		
    }
	
    //
    public static List<DBObject> queryAll(data.Action action){
		
		List<DBObject> list = new ArrayList<DBObject>();
		DB db = mongo.getDB(DB_NAME);
		
		BasicDBObject filterObject = parseFilters(action.getFilter());
		BasicDBObject queryKey = parseSelects(action.getResult(),action.isGiveup());
		BasicDBObject orderObject = parseOrders(action.getOrder());

		//logger.info(db.getCollectionNames());
		DBCollection set = db.getCollection(action.getSchema());
		
		DBCursor cursor = set.find(filterObject,queryKey).sort(orderObject);
		
		//logger.info(cursor.count());

		if(cursor.count()>0)
		while (cursor.hasNext()) {
			DBObject dboject = cursor.next();
			//logger.info(dboject.toString());
			list.add(dboject);

		}
		//logger.info(list);
		return list;
		
	}

	public static List<DBObject> queryPage(data.Action action,int page,int pagesize){
		
		List<DBObject> list = new ArrayList<DBObject>();
		DB db = mongo.getDB(DB_NAME);

		BasicDBObject filterObject = parseFilters(action.getFilter());
		BasicDBObject queryKey = parseSelects(action.getResult(),action.isGiveup());
		BasicDBObject orderObject = parseOrders(action.getOrder());

		//logger.info(db.getCollectionNames());
		DBCollection set = db.getCollection(action.getSchema());

		DBCursor cursor = set.find(filterObject, queryKey).limit(pagesize).skip((page - 1) * pagesize)
				.sort(orderObject);

		//logger.info(cursor.count());

		while (cursor.hasNext()) {
			DBObject dboject = cursor.next();
			//logger.info(dboject.toString());
			list.add(dboject);

		}
		return list;
		
	}
	
	
	// 解析排序结果集json参数
	private static BasicDBObject parseOrders(String jsonOrders) {

		BasicDBObject orderObject = new BasicDBObject();
		if(jsonOrders==null) return orderObject;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			JsonNode jsonObject = mapper.readTree(jsonOrders);
			Iterator<String> keyStrs = jsonObject.fieldNames();
			while (keyStrs.hasNext()) {

				String keyStr = keyStrs.next();
				if (jsonObject.get(keyStr).intValue() == 1)
					orderObject.append(keyStr, 1);
				else
					orderObject.append(keyStr, -1);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//logger.info(orderObject.toJson());
		return orderObject;

	}
	
	
	
	private static BasicDBObject parseSelects(String jsonSelects,boolean cut) {
		
	
		BasicDBObject selectObject = new BasicDBObject("_id", 0);
		if(jsonSelects==null) return selectObject;
		try {
			if(!"".equals(jsonSelects)){
				String[] keys=jsonSelects.split("\\,");
				for(int i=0;i<keys.length;i++){
					if (!cut)
						selectObject.append(keys[i], 1);
					else
						selectObject.append(keys[i], 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//logger.info(selectObject.toJson());
		return selectObject;

	}
	
	// 解析查询条件
	private static BasicDBObject parseFilters(String jsonFilters) {
		
		BasicDBObject queryObject = new BasicDBObject();
		queryObject=(BasicDBObject)JSON.parse(jsonFilters);
		//logger.info(queryObject.toJson());
		return queryObject;

	}

}
