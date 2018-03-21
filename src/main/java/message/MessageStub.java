package message;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.tuple.Pair;
import io.netty.channel.Channel;
import tcp.TcpConnecter;

public class MessageStub {

	private static ConcurrentLinkedQueue<Channel> channels = new ConcurrentLinkedQueue<Channel>();
	private static ConcurrentHashMap<Integer, CompletableFuture<Message>> furtures = new ConcurrentHashMap<Integer, CompletableFuture<Message>>();
	private static ConcurrentHashMap<Integer, MessageListener> callbacks = new ConcurrentHashMap<Integer, MessageListener>();
	private static ConcurrentLinkedQueue<Pair<Integer, Long>> timeouts = new ConcurrentLinkedQueue<Pair<Integer, Long>>();
	private static ScheduledExecutorService keepers = null;
	private static ExecutorService workers = null;
	public static Message heartbeat=new Message();
	
	public static String hhost;
	public static int pport;
	
	//重新链接
	public static boolean reconnect(){
		try {
			channels.clear();
			channels.add(TcpConnecter.connect(hhost, pport));
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
			
			
	
	// @SuppressWarnings("unchecked"）
	public static void keep(String host, int port) {

		try {
			hhost=host;
			pport=port;
			channels.add(TcpConnecter.connect(host, port));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		heartbeat.setService("MessageService", "join");
		heartbeat.setConsumer(UUID.randomUUID().getLeastSignificantBits());
		
		workers = Executors.newScheduledThreadPool(2);
		keepers = Executors.newScheduledThreadPool(1);
		keepers.scheduleAtFixedRate(new Runnable() {

		
			public void run() {
				// TODO Auto-generated method stub

				long now = System.currentTimeMillis();
//				System.out.println("keep clean:" + now);
//				System.out.println("furtures:" + furtures.size());
				
				for(Channel channel:channels){
					channel.writeAndFlush(heartbeat);
				}
				
				
				//删除垃圾数据
				try {
					while (!timeouts.isEmpty()) {

						Pair<Integer, Long> last = timeouts.peek();
						if (last.getValue() > now) {
							timeouts.remove(last);
							CompletableFuture<Message> furture = furtures.remove(last.getKey());
							if (furture != null)
								furture.complete(null);// 超时返回为空
							callbacks.remove(last.getKey());
						} else {
							break;
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				

			}

		}, 1, 2, TimeUnit.SECONDS);

	}

	private static CompletableFuture<Message> addFurture(int id, int timeout) {

		CompletableFuture<Message> furture = new CompletableFuture<Message>();
		furtures.put(id, furture);
		// timeouts.offer(Pair.of(id, System.currentTimeMillis()+timeout));
		return furture;

	}

	private static CompletableFuture<Message> addFurture(int id) {

		CompletableFuture<Message> furture = new CompletableFuture<Message>();
		furtures.put(id, furture);
		// timeouts.offer(Pair.of(id, System.currentTimeMillis()+10));
		return furture;

	}

	public static void notify(final Message message) {

		if (furtures.get(message.getRequestId()) != null){
			furtures.remove(message.getRequestId()).complete(message);
			//System.out.println(message.getRequestId()+":"+message);
		}else{
			 //System.out.println("loss:"+message);
		}
		
		if (callbacks.get(message.getRequestId()) != null)
			workers.submit(new Runnable() {

		
				public void run() {
					// TODO Auto-generated method stub
					MessageListener callback = callbacks.remove(message.getRequestId());
					try {
						callback.handle(message);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//callback.exception(message, e.getCause());
					}
				}

			});
		
	   

	}

	public static void addChannel(Channel channel) {
		channels.offer(channel);
	}

	private static Channel loadBalance() {
		return channels.peek();
	}

	// 同步调用 带超时
	public static Message send(Message request, int waitTime)
			throws InterruptedException, ExecutionException, TimeoutException {

		request.setRequestId();
		Channel channel = loadBalance();
		CompletableFuture<Message> furture = MessageStub.addFurture(request.getRequestId(), waitTime);
		channel.writeAndFlush(request);
		Message reply = furture.get(waitTime, TimeUnit.SECONDS);
		return reply;

	}

	
	// 异步调用
	public static CompletableFuture<Message> send(Message request) throws InterruptedException {

		request.setRequestId();
		Channel channel = loadBalance();
		CompletableFuture<Message> furture = MessageStub.addFurture(request.getRequestId());
		channel.writeAndFlush(request);
		return furture;

	}

	// 异步调用 带回调
	public static void send(Message request, MessageListener callback) throws InterruptedException {

		request.setRequestId();
		Channel channel = loadBalance();
		channel.writeAndFlush(request);
		callbacks.put(request.getRequestId(), callback);
		// timeouts.offer(Pair.of(request.getRequestId(),
		// System.currentTimeMillis()+10));//10S后系统自动删除

	}
	
	
	private static ConcurrentHashMap<Integer,Object> services=new ConcurrentHashMap<Integer,Object>();
	
	//
	public static <T> T getService(Class<T> clazz){
		
		String name=clazz.getName().substring(clazz.getName().lastIndexOf(".")+1);
		Object o=services.get(name.hashCode());
		if(o==null){
			o=MessageInvoker.newInstance(new Class[]{clazz},name);
			services.put(name.hashCode(),o);
		}
		return (T)o;
		
	}

}
