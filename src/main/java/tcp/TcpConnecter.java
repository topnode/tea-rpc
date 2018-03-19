package tcp;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class TcpConnecter {

	private static  Bootstrap boot = new Bootstrap();
	
	static{

        EventLoopGroup workerGroup = new NioEventLoopGroup();
       
        boot.group(workerGroup);
        boot.channel(NioSocketChannel.class);
        boot.handler(new ChannelInitializer<SocketChannel>() {
		            	
		            	     @Override
		            	     protected void initChannel(SocketChannel ch) throws Exception {
		            	    	 
		            	         ChannelPipeline pipeline = ch.pipeline();
		   
		                         pipeline.addLast("decoder", new MessageDecoder());
		            	         pipeline.addLast("encoder", new MessageEncoder());
		            	         // 自己的逻辑Handler
		            	         pipeline.addLast("handler", new TcpClientHandler());
		            	         
		            	         
		            	    }
		            	     
		             });
           
            }
	  
			public static Channel connect(String host,int port) throws InterruptedException, ExecutionException, TimeoutException{
		
			    return boot.connect(host, port).sync().channel();
//		        furture.get(3,TimeUnit.SECONDS);
//		        Channel channel=furture.channel();
//		        return channel;
		        
		    }
			
//			public static Channel reconnect(Channel channel) throws InterruptedException, ExecutionException, TimeoutException{
//			    ChannelFuture furture=b.connect(c);
//		        furture.get(3,TimeUnit.SECONDS);
//		        Channel channel=furture.channel();
//		        return channel;
//	        
//	         }
//			
			

}
