package com.zhd.common;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class Decode extends ByteToMessageDecoder{
	public Class<?> generateClass; 
	public Decode(Class<?> generateClass) {
		this.generateClass = generateClass;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if(in.readableBytes()<4){
			return;
		}
		in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
        }
		byte[] bytes = new byte[dataLength];		
		in.readBytes(bytes);
		Object obj = SerializationUtils.deserialize(bytes, generateClass);
		out.add(obj);
		System.out.println("½âÂë");
	}

}
