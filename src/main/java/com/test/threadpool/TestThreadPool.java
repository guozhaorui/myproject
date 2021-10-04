package com.test.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestThreadPool {

    public static void main(String[] args) {
        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(8,new TestBeanFactory());
        for (int i = 0; i < 10000; i++) {
            threadPoolExecutor.execute(() -> {
                System.out.println(Thread.currentThread().getName());
            });
        }
    }

}
