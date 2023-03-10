package com.iker.tinyrpc.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

public class SpringContextUtil {

    @Getter
    @Setter
    private static ApplicationContext applicationContext;

    public static <T> T getBean(String name, Class<T> type) {
        return applicationContext.getBean(name, type);
    }
}
