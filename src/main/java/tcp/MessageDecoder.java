package tcp;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import message.Message;
import message.Protocol;

public class MessageDecoder extends ByteToMessageDecoder{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		
	    if(in.readableBytes()>=4){
	    	in.markReaderIndex();
	    	int len=in.readInt();
//	    	System.out.println("BODY BYTES:"+len);
	    	if(in.readableBytes()>=len){
	    		byte[] body=new byte[len];
	    		in.readBytes(body);
	    		//System.out.println("RECV BYTES:"+body.length);
	    		out.add(Protocol.deserializer(body, Message.class));		
	    		return;
	    	}
	    		
	    	in.resetReaderIndex();
	    	
	    }
	
	}

}
