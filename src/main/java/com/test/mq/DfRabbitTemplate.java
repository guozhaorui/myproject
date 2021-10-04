package com.test.mq; /**
 * Copyright(C) 2018 Hangzhou Differsoft Co., Ltd. All rights reserved.
 */

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;

/**
 * @since 2018年7月10日 下午6:46:37
 * @author CaiYH
 * @desc 自定义rabbitTemplate，重写send方法，增加消息头
 */
public class DfRabbitTemplate extends RabbitTemplate {

    public DfRabbitTemplate() {

    }

    public DfRabbitTemplate(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Override
    public void send(String exchange, String routingKey, Message message, CorrelationData correlationData) throws AmqpException {
        // 重写rabbitTemplate的send方法，在send之前设置会员消息头信息
        MessageProperties props = message.getMessageProperties();

        super.send(exchange, routingKey, message, correlationData);
    }

}
