package com.test.mq;

/**
 * @since 2020年10月23日
 * @author differ_wk
 * @desc 用于记录解析的注解里的队列配置信息
 */
public class MyQueue {
    private boolean anonymous;
    private String exchange;
    private String queue;
    private String queuePattern;
    private String routingKey;

    /**
     * @return the queue
     */
    public String getQueue() {
        return queue;
    }

    /**
     * @param queue
     *            the queue to set
     */
    public void setQueue(String queue) {
        this.queue = queue;
    }

    /**
     * @return the anonymous
     */
    public boolean isAnonymous() {
        return anonymous;
    }

    /**
     * @param anonymous
     *            the anonymous to set
     */
    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    /**
     * @return the exchange
     */
    public String getExchange() {
        return exchange;
    }

    /**
     * @param exchange
     *            the exchange to set
     */
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    /**
     * @return the queuePattern
     */
    public String getQueuePattern() {
        return queuePattern;
    }

    /**
     * @param queuePattern
     *            the queuePattern to set
     */
    public void setQueuePattern(String queuePattern) {
        this.queuePattern = queuePattern;
    }

    /**
     * @return the routingKey
     */
    public String getRoutingKey() {
        return routingKey;
    }

    /**
     * @param routingKey
     *            the routingKey to set
     */
    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

}
