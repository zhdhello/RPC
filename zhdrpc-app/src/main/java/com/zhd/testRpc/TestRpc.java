package com.zhd.testRpc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zhd.service.HelloWorld;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class TestRpc {
	@Autowired
	private HelloWorld helloWorld;
	@Test
	public void hello(){
		helloWorld.hello();	
	}
	
}
