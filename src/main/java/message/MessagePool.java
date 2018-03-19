package message;

public class MessagePool {
	
	private static final ThreadLocal<Message> messageLocal = new ThreadLocal<Message>();  
	
	public static Message apply(){
		
		Message //message
//		=messageLocal.get();
//		if(message==null) 
			message=new Message();
		message.setRequestId();
//		messageLocal.set(message);
		return message;
		
	}

	
}
