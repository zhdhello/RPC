package com.zhd.service.impl;

import com.zhd.server.RpcService;
import com.zhd.service.HelloWorld;
@RpcService(HelloWorld.class)
public class HelloWorldImpl implements HelloWorld{

	public String hello() {
		System.out.println("服务端已调用方法");
		return "hello world";
	}

}
