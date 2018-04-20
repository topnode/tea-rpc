package tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import message.Message;
import message.MessageStub;

public class TcpClientHandler extends SimpleChannelInboundHandler<Message> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message reply) throws Exception {
		// TODO Auto-generated method stub
		try{
		     MessageStub.notify(reply);
		}catch(Exception e){
			 e.printStackTrace();
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		   System.out.println("连接RPC服务器异常.");
		   ctx.close();
		   int count=10;
		   while(count-->0){
			if(MessageStub.reconnect()) break;
			   
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				 System.out.println("尝试重新连接RPC服务...");
			}
		   }
	}

	
}
