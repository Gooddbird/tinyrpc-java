package com.iker.tinyrpc.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringContextUtil {

    @Getter
    @Setter
    private static AnnotationConfigApplicationContext applicationContext;

    public static <T> T getBean(Class<T> type) {
        return applicationContext.getBean(type);
    }

    public static <T> T getBean(String name, Class<T> type) {
        return applicationContext.getBean(name, type);
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static DefaultListableBeanFactory getBeanFactory() {
        return (DefaultListableBeanFactory) applicationContext.getBeanFactory();
    }
}
