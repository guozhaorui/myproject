package com.test.mq.myTest; /**
 * Copyright(C) 2017 Hangzhou Differsoft Co., Ltd. All rights reserved.
 */


import com.rabbitmq.client.Channel;
import com.test.mq.AbstractDFRabbitListener;
import com.test.mq.myTest.task.MyTask;
import com.test.task.HandleTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 售后线上递交消息监听工厂类
 *
 * @author lijunmin
 * @since 2018年9月6日 下午6:47:43
 */
@Configuration
public class AssRabbitListenerContainerFactory extends AbstractDFRabbitListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(AssRabbitListenerContainerFactory.class);

    @Override
    @Bean("spring.test.rabbitmq.container")
    protected SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory submitRabbitListenerContainerFactory = super.buildListenerContainerFactory("spring.test.rabbitmq");
        submitRabbitListenerContainerFactory.setMaxConcurrentConsumers(1);
        submitRabbitListenerContainerFactory.setConcurrentConsumers(1);
        submitRabbitListenerContainerFactory.setPrefetchCount(16);
        // 设置确认模式手工确认
        submitRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return submitRabbitListenerContainerFactory;
    }

    /**
     * DIRECT 模式测试
     *
     * @param message
     * @param channel
     * @throws Exception
     */

    @RabbitListener(admin = "spring.test.rabbitmq",
            bindings = @QueueBinding(exchange = @Exchange(value = "test.exchange", durable = "true", type = ExchangeTypes.DIRECT),
                    key = "routing.key.test",
                    value = @Queue(value = "spring.rabbit.test", autoDelete = "false", durable = "true", ignoreDeclarationExceptions = "true")),
            containerFactory = "spring.test.rabbitmq.container")
    public void receiveMessageForUploadSellerMemo(Message message, Channel channel) throws Exception {
        HandleTaskExecutor.me().execute(new MyTask(channel, message));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * fanout 模式测试
     *
     * @param message
     * @param channel
     * @throws Exception
     */
    @RabbitListener(admin = "spring.test.rabbitmq",
            bindings = @QueueBinding(exchange = @Exchange(value = "test.exchange.fanout", durable = "true", type = ExchangeTypes.FANOUT),
                    key = "",
                    value = @Queue(value = "queue1", autoDelete = "false", durable = "true", ignoreDeclarationExceptions = "true")),
            containerFactory = "spring.test.rabbitmq.container")
    public void receiveMessageForFanout1(Message message, Channel channel) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(">>> UploadSellerMemo message body : \n" + new String(message.getBody()));
        }
        System.out.println("接受到了广播模式的消息1");

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * fanout 模式测试
     *
     * @param message
     * @param channel
     * @throws Exception
     */
    @RabbitListener(admin = "spring.test.rabbitmq",
            bindings = @QueueBinding(exchange = @Exchange(value = "test.exchange.fanout", durable = "true", type = ExchangeTypes.FANOUT),
                    key = "",
                    value = @Queue(value = "queue2", autoDelete = "false", durable = "true", ignoreDeclarationExceptions = "true")),
            containerFactory = "spring.test.rabbitmq.container")
    public void receiveMessageForFanout2(Message message, Channel channel) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(">>> UploadSellerMemo message body : \n" + new String(message.getBody()));
        }
        System.out.println("接受到了广播模式的消息2");

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * topic 模式测试
     *
     * @param message
     * @param channel
     * @throws Exception
     */
    @RabbitListener(admin = "spring.test.rabbitmq",
            bindings = @QueueBinding(exchange = @Exchange(value = "test.exchange.topic", durable = "true", type = ExchangeTypes.TOPIC),
                    key = "stock.usdddfdf.122",
                    value = @Queue(value = "queue5", autoDelete = "false", durable = "true", ignoreDeclarationExceptions = "true")),
            containerFactory = "spring.test.rabbitmq.container")
    public void receiveMessageForTopic(Message message, Channel channel) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(">>> UploadSellerMemo message body : \n" + new String(message.getBody()));
        }
        System.out.println("接受到了订阅模式0");

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * topic 模式测试
     *
     * @param message
     * @param channel
     * @throws Exception
     */
    @RabbitListener(admin = "spring.test.rabbitmq",
            bindings = @QueueBinding(exchange = @Exchange(value = "test.exchange.topic", durable = "true", type = ExchangeTypes.TOPIC),
                    key = "stock.usd.#",
                    value = @Queue(value = "queue6", autoDelete = "false", durable = "true", ignoreDeclarationExceptions = "true")),
            containerFactory = "spring.test.rabbitmq.container")
    public void receiveMessageForTopic1(Message message, Channel channel) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(">>> UploadSellerMemo message body : \n" + new String(message.getBody()));
        }
        System.out.println("接受到了订阅模式1");

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * topic 模式测试
     *
     * @param message
     * @param channel
     * @throws Exception
     */
    @RabbitListener(admin = "spring.test.rabbitmq",
            bindings = @QueueBinding(exchange = @Exchange(value = "test.exchange.topic", durable = "true", type = ExchangeTypes.TOPIC),
                    key = "stock.usd.112",
                    value = @Queue(value = "queue7", autoDelete = "false", durable = "true", ignoreDeclarationExceptions = "true")),
            containerFactory = "spring.test.rabbitmq.container")
    public void receiveMessageForTopic2(Message message, Channel channel) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(">>> UploadSellerMemo message body : \n" + new String(message.getBody()));
        }
        System.out.println("接受到了订阅模式2");

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
