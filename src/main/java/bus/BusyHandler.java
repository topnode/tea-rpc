package bus;

import com.lmax.disruptor.EventHandler;

import bus.BusyQueue.BusyEvent;
import message.Message;
import message.MessageHook;

public class  BusyHandler implements EventHandler<BusyEvent>  {

	public void onEvent(BusyEvent event, long qid, boolean arg2) throws Exception {
		// TODO Auto-generated method stub
		try{
			Message reply=MessageHook.invoker(event.getMessage());
			event.getChannel().writeAndFlush(reply);
		}catch(Exception e){
			event.getChannel().writeAndFlush(event.getMessage());
		}
		
	}	

}
