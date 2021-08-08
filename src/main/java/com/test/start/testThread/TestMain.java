package com.test.start.testThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestMain {
    public static void main(String[] args) {
        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0; i < 100; i++) {
            es.submit(new MyTask());
        }
        //  Thread thd = new Thread(new MyTask());
        //  thd.start();
        // System.out.println(Thread.currentThread().getName());
        //    es.submit(new MyTask());
    }
}
