package com.test.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {
	public static final String MIAOSHA_QUEUE = "seckill.queue";
	public static final String QUEUE = "gzrqueue";
	public static final String TOPIC_QUEUE1 = "topic.queue1";
	public static final String TOPIC_QUEUE2 = "topic.queue2";
	public static final String HEADER_QUEUE = "header.queue";
	public static final String TOPIC_EXCHANGE = "topicExchage";
	public static final String FANOUT_EXCHANGE = "fanoutxchage";
	public static final String HEADERS_EXCHANGE = "headersExchage";

	@Bean
	public MessageConverter getMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	/**
	 * Direct模式 交换机Exchange
	 */
	 @Bean
	public Queue queue() {
		return new Queue(QUEUE, true);
	}



}
