package com.zhd.service.impl;

import com.zhd.server.RpcService;
import com.zhd.service.HelloWorld;
@RpcService(HelloWorld.class)
public class HelloWorldImpl implements HelloWorld{

	public String hello() {
		System.out.println("������ѵ��÷���");
		return "hello world";
	}

}
