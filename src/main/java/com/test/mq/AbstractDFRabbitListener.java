package com.test.mq; /**
 * Copyright(C) 2017 Hangzhou Differsoft Co., Ltd. All rights reserved.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

/**
 * @author CaiYH
 * @desc rabbitmq接收监听器(备注 ： 因项目中有引用 ， 修改会导致项目重新修改 ， 不影响性能 ， 故扫描不解决)
 * @since 2017年12月4日 下午2:34:28
 */
public abstract class AbstractDFRabbitListener {

    /**
     * 日志
     **/
    private static final Logger LOG = LoggerFactory.getLogger(AbstractDFRabbitListener.class);

    /**
     * 默认的线程名定义
     */
    private static final String DEFAULT_THREAD_PREFIX = "DFRabbitConsumer";

    /**
     * 环境变量
     **/
    @Autowired
    private Environment env;

    /**
     * 子类重写rabbitmq监听器容器工厂
     *
     * @return
     */
    protected abstract SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory();

    /**
     * 构建监听器容器工厂    内部实现
     *
     * @param prefix rabbit属性前缀
     * @return 监听工厂
     * @throws Exception
     */
    protected SimpleRabbitListenerContainerFactory buildListenerContainerFactory(String prefix) {

        RabbitProperties properties = RabbitCommonUtil.buildRabbitProperties(env, prefix);

        ConnectionFactory connectionFactory = null;

        try {
            connectionFactory = RabbitCommonUtil.buildConnectionFactory(properties);
        } catch (Exception e) {
            LOG.error("mq监控器构造异常", e);
        }
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);

        // 自定义线程名称  由模板方法customizeThreadNamePrefix给出
        factory.setTaskExecutor(new SimpleAsyncTaskExecutor(consumerThreadNamePrefix() + "-"));

        // 默认使用手动确认消费
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        DfSimpleRabbitListenerContainerFactoryConfigurer configurer = this.buildRabbitListenerContainerFactoryConfigurer(properties);

        // 可通过配置文件进行重新修改
        configurer.configure(factory, connectionFactory);

        return factory;
    }

    /**
     * 构建监听工厂配置类
     *
     * @param rabbitProperties rabbit属性
     * @return
     */
    private DfSimpleRabbitListenerContainerFactoryConfigurer buildRabbitListenerContainerFactoryConfigurer(RabbitProperties rabbitProperties) {
        DfSimpleRabbitListenerContainerFactoryConfigurer configurer = new DfSimpleRabbitListenerContainerFactoryConfigurer();
        configurer.setRabbitProperties(rabbitProperties);
        return configurer;
    }

    /**
     * 自定义消费者线程名称方法，如需更改线程名需要重写该方法
     *
     * @return
     */
    protected String consumerThreadNamePrefix() {
        return DEFAULT_THREAD_PREFIX;
    }
}
