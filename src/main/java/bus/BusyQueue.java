package bus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import bus.BusyQueue.BusyEvent;
import io.netty.channel.Channel;
import message.Message;

public class BusyQueue {

	public static class BusyEvent {

		private Message message;
		private Channel channel;

		public Message getMessage() {
			return message;
		}

		public void setMessage(Message message) {
			this.message = message;
		}

		public Channel getChannel() {
			return channel;
		}

		public void setChannel(Channel channel) {
			this.channel = channel;
		}
		

	}

	public static class BusyEventFactory implements EventFactory<BusyEvent> {

		public BusyEvent newInstance() {
			return new BusyEvent();
		}

	}


	private ExecutorService taskExecutor = Executors.newFixedThreadPool(5);
	private Disruptor<BusyEvent> disruptor = null;
	private int eventQueueSize = 4096;
	private RingBuffer<BusyEvent> eventQueue = null;

	public BusyQueue(int queueSize, int workerNum) {
		
		this.taskExecutor = Executors.newFixedThreadPool(workerNum);
		this.eventQueueSize = queueSize;

	}

	@SuppressWarnings("unchecked")
	public void start(EventHandler<BusyEvent> handler) {

		disruptor = new Disruptor<BusyEvent>(new BusyEventFactory(), eventQueueSize, taskExecutor);
		disruptor.handleEventsWith(handler);
		disruptor.start();
		eventQueue = disruptor.getRingBuffer();

	}

	public void submit(Channel channel,Message message) {

		long sequence = eventQueue.next();
		try {
			BusyEvent event = eventQueue.get(sequence);// for the sequence
			event.setMessage(message);
			event.setChannel(channel);

		} finally {
			eventQueue.publish(sequence);
		}

	}

}
