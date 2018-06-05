package com.zhd.server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.zhd.common.RpcRequest;
import com.zhd.common.RpcResponse;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends  SimpleChannelInboundHandler<RpcRequest> {
	private Map<String, Object> handlerMap = new HashMap<String, Object>();
	public ServerHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
		RpcResponse responese = new RpcResponse();
		responese.setRequestId(msg.getRequestId());
		try{
			Object result = hanlde(msg);
			responese.setResult(result);
		}catch(Exception e){
			responese.setError(e);
		}
		
		ctx.writeAndFlush(responese).addListener(ChannelFutureListener.CLOSE);
	}

	private Object hanlde(RpcRequest msg) throws Exception {
		//获取类名
		String classname = msg.getClassName();
		//获取Bean
		Object service = handlerMap.get(classname);
		//获取方法名
		String methodName = msg.getMethodName();
		//获取参数
		Object[] params = msg.getParams();
		//获取参数类型
		Class<?>[] paramsType = msg.getParamsType();
		Class<?> forName = Class.forName(classname);
		//获取方法
		Method method = forName.getMethod(methodName, paramsType);
		return method.invoke(service, params);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.out.println(cause);
		ctx.close();
	}
}
