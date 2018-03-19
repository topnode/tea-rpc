package bus;

import com.lmax.disruptor.EventHandler;

import bus.BusyQueue.BusyEvent;
import message.Message;
import message.MessageHook;

public class  BusyHandler implements EventHandler<BusyEvent>  {

	public void onEvent(BusyEvent event, long qid, boolean arg2) throws Exception {
		// TODO Auto-generated method stub
		Message reply=MessageHook.invoker(event.getMessage());
		
	}

//	public void onEvent(BusyEvent event, long qid, boolean arg2) throws Exception {
//		// TODO Auto-generated method stub
//		MessageHook.invoker(event.getMessage());
//	}

//	@Override
//	public void onEvent(LazyTask task, long arg1, boolean arg2) throws Exception {
//		
//		// TODO Auto-generated method stub
//		if(LogicServer.getHandler(task.getType())==null){
//			 System.out.println("未注册对类型"+Integer.toHexString(task.getType())+"的handler");
//		}else{
////		     LogicServer.getHandler(task.getType()).lazyHandle(task.getContext());
//		}
//		
//	}
	

}
