package message;

import io.netty.channel.ChannelHandlerContext;

public interface MessageFilter {
	public boolean process(ChannelHandlerContext ctx,Message request)throws Exception;
}
