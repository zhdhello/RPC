package com.zhd.server;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.zhd.common.Decode;
import com.zhd.common.Encode;
import com.zhd.common.RpcRequest;
import com.zhd.common.RpcResponse;
import com.zhd.regist.ServiceRegistor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RpcServer implements ApplicationContextAware,InitializingBean{
	private Map<String,Object> handleMap = new HashMap<String, Object>();
	private String serverAddress;
	private ServiceRegistor serviceRegistor;

	public RpcServer(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public RpcServer(String serverAddress, ServiceRegistor serviceRegistor) {
		this.serverAddress = serverAddress;
		this.serviceRegistor = serviceRegistor;
	}
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(RpcService.class);
		if(beansWithAnnotation!=null){
			for(Object service:beansWithAnnotation.values()){
				String interfaceName = service.getClass()
						.getAnnotation(RpcService.class).value().getName();				
				handleMap.put(interfaceName, service); 
			}
		}
	}
	public void afterPropertiesSet() throws Exception {
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		try{
			
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new Decode(RpcRequest.class))
								 .addLast(new Encode(RpcResponse.class))
								 .addLast(new ServerHandler(handleMap));
				}
			}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			String[] splits = serverAddress.split(":");
			ChannelFuture future = bootstrap.bind(splits[0], Integer.parseInt(splits[1])).sync();
			if (serviceRegistor != null) {
				serviceRegistor.register(serverAddress);
			}

			future.channel().closeFuture().sync();
		}finally {
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
		
	}

	

}
