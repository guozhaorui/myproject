package com.test.mq.myTest.task;

import com.rabbitmq.client.Channel;
import com.test.task.handle.AbstractHandler;
import org.springframework.amqp.core.Message;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 测试线程安全的任务
 */
public class MyTask extends AbstractHandler {

    public MyTask(Channel channel, Message message) {
        super(channel, message);
    }

    private static int m = 0;

    private static  HashMap<String, String> testHashMap = new HashMap<>();

    private static Object lock = new Object();

    private final String taskName = "测试线程安全的任务";

    @Override
    protected void handle(String body) {
            for (int i = 0; i < 100; i++) {
               // testHashMap.put(String.valueOf(i), String.valueOf(i));
                if (i == 2) {
                    testHashMap.put(String.valueOf(2), String.valueOf(2));
                } else if (i == 6) {
                    testHashMap.put(String.valueOf(2), String.valueOf(6));
                } else if (i == 20) {
                    testHashMap.put(String.valueOf(2), String.valueOf(20));
                } else if (i == 25) {
                    testHashMap.put(String.valueOf(2), String.valueOf(25));
                } else if (i == 60) {
                    testHashMap.put(String.valueOf(2), String.valueOf(60));
                }
            }
            System.out.println(testHashMap.get(String.valueOf(2)));
    }

    @Override
    public String rejected() {
        return null;
    }

    @Override
    protected String getTaskName() {
        return this.taskName;
    }
}
