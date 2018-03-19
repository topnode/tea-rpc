package tcp;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class TcpServer {
	
	public static void start(int port) throws InterruptedException{
		
		    EventLoopGroup bossGroup = new NioEventLoopGroup();
	        EventLoopGroup workerGroup = new NioEventLoopGroup();
	        try {
	            ServerBootstrap b = new ServerBootstrap();
	            b.group(bossGroup, workerGroup);
	            b.channel(NioServerSocketChannel.class);
	            b.childHandler(new ChannelInitializer<SocketChannel>() {
	            	
	            	     @Override
	            	     protected void initChannel(SocketChannel ch) throws Exception {
	            	    	 
	            	         ChannelPipeline pipeline = ch.pipeline();
	            	
	                         pipeline.addLast("decoder", new MessageDecoder());
	            	         pipeline.addLast("encoder", new MessageEncoder());
	            	         // 自己的逻辑Handler
	            	         pipeline.addLast("handler", new TcpServerHandler());
	            	         
	            	    }
	            	     
	             });

	            // 服务器绑定端口监听
	            ChannelFuture f = b.bind(port).sync();
	            // 监听服务器关闭监听
	            f.channel().closeFuture().sync();

	            // 可以简写为
	            /* b.bind(portNumber).sync().channel().closeFuture().sync(); */
	        } finally {
	            bossGroup.shutdownGracefully();
	            workerGroup.shutdownGracefully();
	        }
	        
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
