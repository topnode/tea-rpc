package tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
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
			   MessageStub.reconnect();
			   
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
	}

	
}
