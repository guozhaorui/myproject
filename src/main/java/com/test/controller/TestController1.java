package com.test.controller;


import com.test.service.test.PrototypeObj;
import com.test.service.test.RequestObj;
import com.test.service.test.SessionObj;
import com.test.service.test.SingletonObj;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/testone", produces = {"application/json;charset=utf-8"})
public class TestController1 implements ApplicationContextAware {

    private RequestObj requestObj;

    private SessionObj sessionObj;

    private PrototypeObj prototypeObj;

    private SingletonObj singletonObj;

    private ApplicationContext applicationContext;

    @RequestMapping(value = "/test")
    public void test() {

        System.out.println(applicationContext.getBean("singletonObj"));
        System.out.println(applicationContext.getBean("singletonObj"));

        System.out.println(applicationContext.getBean("prototypeObj"));
        System.out.println(applicationContext.getBean("prototypeObj"));

        System.out.println(applicationContext.getBean("sessionObj"));
        System.out.println(applicationContext.getBean("sessionObj"));

        System.out.println(applicationContext.getBean("requestObj"));
        System.out.println(applicationContext.getBean("requestObj"));

        System.out.println("--------------------------------");

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
