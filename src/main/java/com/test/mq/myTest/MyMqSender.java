package com.test.mq.myTest;

import com.test.mq.AbstractDFRabbitSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Order(19)
public class MyMqSender  extends AbstractDFRabbitSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyMqSender.class);

    protected RabbitAdmin rabbitAdmin;

    @Override
    public RabbitAdmin getRabbitAdmin() {
        return rabbitAdmin;
    }

    /**
     *
     */
    @Override
    @Bean("spring.test.rabbitmq")
    protected RabbitAdmin rabbitAdmin() {
        rabbitAdmin = super.buildRabbitAdmin("spring.test.rabbitmq");
        return rabbitAdmin;
    }

    @Override
    public void send(String exchange, String route, String message) {
        Message m = new Message(message.getBytes(), new MessageProperties());
        this.send(exchange, route, m, null);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        LOGGER.info("消息确认回调");
        if (ack) {
            LOGGER.info(">>> confirm success ");
        } else {
        }
    }

}
