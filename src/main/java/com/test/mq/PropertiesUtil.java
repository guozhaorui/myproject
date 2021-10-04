package com.test.mq; /**
 * Copyright(C) 2018 Hangzhou Differsoft Co., Ltd. All rights reserved.
 */


import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.ClassUtils;

/**
 * @since 2018年7月11日 下午3:55:53
 * @author CaiYH
 * @desc 属性工具类
 */
public class PropertiesUtil {
    /**
     * 绑定rabbit属性
     *
     * @param env：配置
     * @param prefix：配置前缀
     * @param instance：绑定的实体类
     */
    public static <T> void dataBind(Environment env, String prefix, T instance) {

        MutablePropertySources propertySources = ((ConfigurableEnvironment) env).getPropertySources();

        PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<Object>(instance);

        factory.setPropertySources(propertySources);

        factory.setIgnoreInvalidFields(false);

        factory.setIgnoreUnknownFields(true);

        factory.setIgnoreNestedProperties(false);

        factory.setTargetName(prefix);

        try {
            factory.bindPropertiesToTarget();
        } catch (Exception ex) {
            String targetClass = ClassUtils.getShortName(instance.getClass());
            throw new BeanCreationException("rabbitProperties", "Could not bind properties to " + targetClass, ex);
        }

    }
}
