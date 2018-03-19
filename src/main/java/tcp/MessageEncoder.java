package tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import message.Message;
import message.Protocol;

public class MessageEncoder extends MessageToByteEncoder<Message> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
		// TODO Auto-generated method stub
//		if(msg.getServiceId()==0) {
//			System.out.println("SEND BYTES:");
//			out.writeInt(0); return;
//		}
		byte[] body=Protocol.serializer(msg);
		//System.out.println("SEND BYTES:"+body.length);
		out.writeInt(body.length);
		out.writeBytes(body);
	}

}
