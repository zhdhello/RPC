package com.zhd.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import com.zhd.common.RpcRequest;
import com.zhd.common.RpcResponse;
import com.zhd.discover.ServiceDiscovery;

public class RpcProxy {
	//ע�����ĵ�ַ
	private ServiceDiscovery discovery;
	public RpcProxy(ServiceDiscovery discovery) {
		this.discovery = discovery;
	}
	/**
	 * �����������
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
					//��ע�����Ļ�ȡ����ĵ�ַ
					path = discovery.discover();
				}
				String[] hostAndPort = path.split(":");
				RpcClient client = new RpcClient(hostAndPort[0], Integer.parseInt(hostAndPort[1]));		
				//ͨ��netty�����˷�������
				RpcResponse response = client.send(request);
				//������Ϣ
				if (response.isError()) {
					throw response.getError();
				} else {
					return response.getResult();
				}
			}
		});
	}
}
