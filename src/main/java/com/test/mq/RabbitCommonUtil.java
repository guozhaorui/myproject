package com.test.mq; /**
 * Copyright(C) 2017 Hangzhou Differsoft Co., Ltd. All rights reserved.
 *
 */


import com.test.mq.PropertiesUtil;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.core.env.Environment;

/**
 * @since 2017年12月4日 下午2:42:48
 * @author CaiYH
 * @desc rabbit公共静态方法
 */
public class RabbitCommonUtil {

    /**
     * rabbit属性解析
     *
     * @param env
     *            环境变量
     * @param prefix
     *            rabbit属性前缀
     * @return rabbit属性实例
     */
    public static RabbitProperties buildRabbitProperties(Environment env, String prefix) {

        RabbitProperties props = new RabbitProperties();

        // 数据绑定
        PropertiesUtil.dataBind(env, prefix, props);

        return props;
    }

    /**
     * rabbit连接工厂 带自定义线程工厂
     *
     * @param props rabbit属性
     * @return 连接工厂
     * @throws Exception
     */
    static ConnectionFactory buildConnectionFactory(RabbitProperties props) throws Exception {

        RabbitConnectionFactoryBean factoryBean = buildRabbitConnectionFactoryBean(props);

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(factoryBean.getObject());

        connectionFactory.setAddresses(props.determineAddresses());
        connectionFactory.setPublisherConfirms(props.isPublisherConfirms());
        connectionFactory.setPublisherReturns(props.isPublisherReturns());
        if (props.getCache().getChannel().getSize() != null) {
            connectionFactory.setChannelCacheSize(props.getCache().getChannel().getSize());
        }
        if (props.getCache().getConnection().getMode() != null) {
            connectionFactory.setCacheMode(props.getCache().getConnection().getMode());
        }
        if (props.getCache().getConnection().getSize() != null) {
            connectionFactory.setConnectionCacheSize(props.getCache().getConnection().getSize());
        }
        if (props.getCache().getChannel().getCheckoutTimeout() != null) {
            connectionFactory.setChannelCheckoutTimeout(props.getCache().getChannel().getCheckoutTimeout());
        }
        return connectionFactory;
    }

    /**
     * 构建MQ连接工厂bean
     *
     * @param props
     * @return
     * @throws Exception
     */
    private static RabbitConnectionFactoryBean buildRabbitConnectionFactoryBean(RabbitProperties props) throws Exception {
        RabbitConnectionFactoryBean factoryBean = new RabbitConnectionFactoryBean();
        if (props.determineHost() != null) {
            factoryBean.setHost(props.determineHost());
        }
        factoryBean.setPort(props.determinePort());

        if (props.determineUsername() != null) {
            factoryBean.setUsername(props.determineUsername());
        }
        if (props.determinePassword() != null) {
            factoryBean.setPassword(props.determinePassword());
        }
        if (props.determineVirtualHost() != null) {
            factoryBean.setVirtualHost(props.determineVirtualHost());
        }
        if (props.getRequestedHeartbeat() != null) {
            factoryBean.setRequestedHeartbeat(props.getRequestedHeartbeat());
        }
        RabbitProperties.Ssl ssl = props.getSsl();
        if (ssl.isEnabled()) {
            factoryBean.setUseSSL(true);
            if (ssl.getAlgorithm() != null) {
                factoryBean.setSslAlgorithm(ssl.getAlgorithm());
            }
            factoryBean.setKeyStore(ssl.getKeyStore());
            factoryBean.setKeyStorePassphrase(ssl.getKeyStorePassword());
            factoryBean.setTrustStore(ssl.getTrustStore());
            factoryBean.setTrustStorePassphrase(ssl.getTrustStorePassword());
        }
        if (props.getConnectionTimeout() != null) {
            factoryBean.setConnectionTimeout(props.getConnectionTimeout());
        }
        factoryBean.afterPropertiesSet();

        return factoryBean;
    }
}
