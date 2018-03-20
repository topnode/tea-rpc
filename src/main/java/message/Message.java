package message;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message implements Cloneable{

	private int serviceId;
	private int requestId;
	private int statusCode;
	private long consumer;
	private Object data=null;
	private LinkedHashMap<String,Object> dataMap=null;
	
	public int setService(String className, String methodName) {
		setServiceId((className + ":" + methodName).hashCode());
		return this.getServiceId();
	}

	public void setRequestId() {
		this.requestId = Sequencer.next();
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	
	public long getConsumer() {
		return consumer;
	}

	public void setConsumer(long consumer) {
		this.consumer = consumer;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public <T> T getData() {
		return (T)data;
	}
	
	public <T> void setData(T data) {
		this.data = data;
	}
	
	public Map<String,Object> getDataMap(){
		if(this.dataMap==null) this.dataMap=new LinkedHashMap<String,Object>();
		return this.dataMap;
	}
	
	public <T> void set(String name,T object){
		if(this.dataMap==null) this.dataMap=new LinkedHashMap<String,Object>();
		this.dataMap.put(name,object);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key){
		if(this.dataMap!=null){
			return 	(T)this.dataMap.get(key);
		}
		return null;
	}

	public String toString() {
		return toJson();
	}
	
	public String toJson() {
		
		ObjectMapper mapper=new ObjectMapper();
		try {
			return  mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
}
