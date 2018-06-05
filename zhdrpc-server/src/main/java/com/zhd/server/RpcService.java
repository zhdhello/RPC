package com.zhd.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

@Target({ElementType.TYPE})//ע�����ӿ�
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
	Class<?> value();
}
