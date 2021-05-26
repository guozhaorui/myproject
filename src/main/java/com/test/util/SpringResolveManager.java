package com.test.util;

import org.apache.commons.lang3.StringUtils;

/*
 * Copyright(C) 2017 Hangzhou Differsoft Co., Ltd. All rights reserved.
 */

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring Boot对象反转管理器。
 *
 * @author 洪光旺
 * @since 2017年12月21日 下午2:38:08
 */
@Component
public class SpringResolveManager implements ApplicationContextAware {
    /**
     * ServletContext对象
     */
    private static ApplicationContext servletContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringResolveManager.servletContext == null) {
            SpringResolveManager.servletContext = applicationContext;
        }
    }

    /**
     * Spring Boot对象反转
     *
     * @param type 对象类型
     * @return 反转对象
     */
    public static <T> T resolve(Class<T> type) {
        return resolve(type, null, null);
    }

    /**
     * Spring Boot对象反转
     *
     * @param beanName bean别名
     * @return 反转对象
     */
    public static <T> T resolve(String beanName) {
        return resolve(null, beanName, null);
    }

    /**
     * Spring Boot对象反转
     *
     * @param type     对象类型
     * @param beanName bean别名
     * @return 反转对象
     */
    public static <T> T resolve(Class<T> type, String beanName) {
        return resolve(type, beanName, null);
    }

    /**
     * Spring Boot对象反转
     *
     * @param type 对象类型
     * @param args 构造器参数
     * @return 反转对象
     */
    public static <T> T resolve(Class<T> type, Object... args) {
        return resolve(type, null, args);
    }

    /**
     * Spring Boot对象反转
     *
     * @param type     对象类型
     * @param beanName bean别名
     * @param args     构造器参数
     * @return 反转对象
     */
    public static <T> T resolve(Class<T> type, String beanName, Object... args) {

        if (StringUtils.isNotBlank(beanName)) {
            //通过别名获取。

            if (null != args && args.length > 0) {
                //有参构造。
                return (T) servletContext.getBean(beanName, args);
            } else {
                //无参构造。
                return servletContext.getBean(beanName, type);
            }
        } else {
            //通过类型获取。

            if (null != args && args.length > 0) {
                //有参构造。
                return servletContext.getBean(type, args);
            } else {
                //无参构造。
                return servletContext.getBean(type);
            }
        }
    }

}
