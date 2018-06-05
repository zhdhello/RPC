package com.zhd.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import com.zhd.common.RpcRequest;
import com.zhd.common.RpcResponse;
import com.zhd.discover.ServiceDiscovery;

public class RpcProxy {
	//注册中心地址
	private ServiceDiscovery discovery;
	public RpcProxy(ServiceDiscovery discovery) {
		this.discovery = discovery;
	}
	/**
	 * 创建代理对象
	 * @param interfaceClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T create(Class<T> interfaceClass){
		return (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
			
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				RpcRequest request = new RpcRequest();
				request.setRequestId(UUID.randomUUID().toString());
				request.setMethodName(method.getName());
				request.setClassName(method.getDeclaringClass().getName());
				request.setParams(method.getParameters());
				request.setParamsType(method.getParameterTypes());
				String path = null;
				if(discovery!=null){
					//从注册中心获取服务的地址
					path = discovery.discover();
				}
				String[] hostAndPort = path.split(":");
				RpcClient client = new RpcClient(hostAndPort[0], Integer.parseInt(hostAndPort[1]));		
				//通过netty向服务端发送请求
				RpcResponse response = client.send(request);
				//返回信息
				if (response.isError()) {
					throw response.getError();
				} else {
					return response.getResult();
				}
			}
		});
	}
}
