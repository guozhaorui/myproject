package com.test.start.testThread;

public class MyTask implements Runnable {
    public  Integer s = 1;
    public static Object lock = new Object();

    @Override
    public void run() {
        synchronized (MyTask.class){
            s++;
            System.out.println("线程名字:"+ Thread.currentThread().getName());
            System.out.println("S的值:"+s);
        }
    }
}
