package message;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bus.BusyHandler;
import bus.BusyQueue;
import tcp.TcpServer;


public final class MessageHook {
		
	private static  class MyLoader extends ClassLoader{
		
		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			return findClass(name);
		}
		
	}
	
	private static BusyQueue busyQueue = new BusyQueue(4096,2);
	
	private static ConcurrentHashMap<Integer,String > serviceNames=new ConcurrentHashMap<Integer,String>();
	private static ConcurrentHashMap<Integer,Object > instances=new ConcurrentHashMap<Integer,Object>();
	private static ConcurrentHashMap<Integer,Method>  invokers=new ConcurrentHashMap<Integer,Method>();
	
	private static List<MessageFilter>  firstFilters=new ArrayList<MessageFilter>();
	private static List<MessageFilter>  lastFilters=new ArrayList<MessageFilter>();
	
	public String name(int serviceId){
		return serviceNames.get(serviceId);
	}
	
	public static void addFilter(int index,MessageFilter filter){
		if(index>0) firstFilters.add(filter);
		else lastFilters.add(filter);
	}
	
	public static Message filterFirst(Message message){
		return message;
	}
	
	public static Message filterLast(Message message){
		return message;
	}
	
	
	public static BusyQueue getBusyQueue(){
		return busyQueue;
	};
	
    public static void start(int port){
    	
    	busyQueue.start(new BusyHandler());
    	MessageHook.register("message.MessageService", "join");
    	MessageHook.register("message.MessageService", "service");
        try {
			TcpServer.start(port);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	public static Message invoker(Message message){
		

		try {
			Method method=invokers.get(message.getServiceId());
			if( method==null ) return message;
			return 	(Message)method.invoke(instances.get(message.getServiceId()),new Object[]{ message});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("调用异常#######################");
			e.printStackTrace();
		}
		return message;
		
	}
	
	public static void unregister(String fullClassName,String methodName){
		
		String className=fullClassName.substring(fullClassName.lastIndexOf(".")+1);
		int serviceId=(className+":"+methodName).hashCode();
		serviceNames.remove(serviceId);
		instances.remove(serviceId);
		
	}
	
	private static void register(String fullClassName,String methodName){
		
		String className=fullClassName.substring(fullClassName.lastIndexOf(".")+1);
		int serviceId=(className+":"+methodName).hashCode();
		serviceNames.put(serviceId, (className+":"+methodName));
		if(instances.get(serviceId)==null){
			//String appPath =System.getProperty("java.class.path");
			try {
					Class<?> classType =Class.forName(fullClassName);//new MyLoader().loadClass(appPath+fullClassName.replace(".", "/")+".class");
					Object instance = classType.newInstance();
					instances.put(serviceId, instance);
					//动态构造InvokeTest类的add(int num1, int num2)方法，标记为addMethod的Method对象
					Method method = classType.getMethod(methodName, new Class[]{Message.class});
					invokers.put(serviceId, method);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	private static Map<Integer,String >  getServiceNames(){
		return serviceNames;
	}
	

}
