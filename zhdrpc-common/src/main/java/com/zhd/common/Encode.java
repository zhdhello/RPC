package com.zhd.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class Encode extends MessageToByteEncoder{

	private Class<?> generateClass;

	// 构造函数传入向反序列化的class
	public Encode(Class<?> generateClass) {
		this.generateClass = generateClass;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, Object obj, ByteBuf out)
			throws Exception {
		//序列化
		if (generateClass.isInstance(obj)) {
			byte[] data = SerializationUtils.serialize(obj);
			out.writeInt(data.length);
			out.writeBytes(data);
		}
		System.out.println("编码");
	}

}
