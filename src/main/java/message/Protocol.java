package message;


public class Protocol {
	
	public enum TYPE{
		PROTOBUFF,
		STRING
	}
	
	public static  final TYPE type=TYPE.STRING;
	
	@SuppressWarnings("unchecked")
	public static <T> byte[] serializer(T obj) {
		
	   return SerializationUtils.serializer(obj);
//		  switch(type){
//		  	case PROTOBUFF: return SerializationUtils.serializer(obj);
//		  	default: return obj.toString().getBytes();
//		  }
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserializer(byte[] data, Class<T> cls) {
		
		return SerializationUtils.deserializer(data, cls);
//		  switch(type){
//		  	case PROTOBUFF: return SerializationUtils.deserializer(data, cls);
//		  	default:  return (T)new String(data);
//		  }
	}
	

}
