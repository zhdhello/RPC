package com.zhd.client;

import com.zhd.common.Decode;
import com.zhd.common.Encode;
import com.zhd.common.RpcRequest;
import com.zhd.common.RpcResponse;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcClient extends SimpleChannelInboundHandler<RpcResponse>{
	private String host;
	private int port;
	private final Object obj = new Object();//Ëø¶ÔÏó
	private RpcResponse rpcResponse;
	public RpcClient() {
	}
	public RpcClient(String host,int port) {
		this.host=host;
		this.port = port;
	}
	
	public RpcResponse send(RpcRequest request)throws Exception {
		EventLoopGroup worker = new NioEventLoopGroup();
		Bootstrap bootStrap = new Bootstrap();
		bootStrap.group(worker).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel channel) throws Exception {
				channel.pipeline().addLast(new Encode(RpcRequest.class))
								  .addLast(new Decode(RpcResponse.class))
								  .addLast(RpcClient.this);
			}
			
			
		}).option(ChannelOption.SO_KEEPALIVE, true);
		try {
			ChannelFuture future = bootStrap.connect(host, port).sync();
			future.channel().writeAndFlush(request).sync();
			synchronized (obj) {
				obj.wait();
			}
			if(rpcResponse!=null){
				future.channel().closeFuture().sync();
			}
		} finally {
			worker.shutdownGracefully();
		}
		return rpcResponse;
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
		this.rpcResponse = msg;
		synchronized(obj){
			obj.notifyAll();
		}
		System.out.println("msg"+msg);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.out.println(cause);
		ctx.close();
	}
	
}
