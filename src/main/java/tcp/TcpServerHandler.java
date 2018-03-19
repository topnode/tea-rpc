package tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import message.Message;
import message.MessageHook;

public class TcpServerHandler  extends SimpleChannelInboundHandler<Message> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx,Message request) throws Exception {
		// TODO Auto-generated method stub
		try{
			
			MessageHook.getBusyQueue().submit(ctx.channel(), request);
//			//request=MessageHook.filterFirst(request);		
//			System.out.println(request);
//			Message reply=MessageHook.invoker(request);
//			if(reply!=null){
//				//reply=MessageHook.filterLast(reply);
//				ctx.writeAndFlush(reply);
//			}else{
//				ctx.writeAndFlush(request);
//			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		   System.out.println("远端关闭连接."+cause.getMessage());
		   ctx.close();
	}

}
