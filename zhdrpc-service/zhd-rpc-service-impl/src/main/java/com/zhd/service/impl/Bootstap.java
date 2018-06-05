package com.zhd.service.impl;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Bootstap {
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("spring.xml");
	}
}
