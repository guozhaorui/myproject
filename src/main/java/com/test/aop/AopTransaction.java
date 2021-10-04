package com.test.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AopTransaction {
    @Autowired
    private AopAspectUtil transactionUtils;

    @Around("execution(* com.test.service.TestService.test(..))")
    public void around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //调用方法之前执行
        System.out.println("开启事务");
        transactionUtils.begin();
        proceedingJoinPoint.proceed();
        //调用方法之后执行
        System.out.println("提交事务");
        transactionUtils.commit();

    }

    @AfterThrowing(pointcut="execution(* com.test.service.TestService.test(..))")
    public void afterThrowing() {
        System.out.println("异常通知  ");
        //获取当前事务进行回滚
        //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        transactionUtils.rollback();
    }
}
