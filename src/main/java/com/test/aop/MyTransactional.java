package com.test.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})//设置注解使用范围
@Retention(RetentionPolicy.RUNTIME)
public @interface MyTransactional { //注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在
}

