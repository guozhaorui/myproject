package com.test.task.handle;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

public abstract class AbstractHandler implements Runnable {
    private Channel channel;

    private Message message;

    public AbstractHandler(Channel channel, Message message) {
        this.channel = channel;
        this.message = message;
    }

    /**
     * 任务被线程池拒绝时的日志信息
     *
     * @return 日志信息
     */
    public abstract String rejected();

    /**
     * 任务名称(子类重写)
     *
     * @return
     */
    protected String getTaskName() {
        return null;
    }

    @Override
    public void run() {
        System.out.println("开始执行任务:" + this.getTaskName());
        try {
            this.handle(new String(message.getBody(), "UTF-8"));
        } catch (Exception e) {
            System.out.println("获取message中body错误" + e.getMessage());
        }
        System.out.println("结束执行任务:" + this.getTaskName());
    }

    protected abstract void handle(String message);
}
