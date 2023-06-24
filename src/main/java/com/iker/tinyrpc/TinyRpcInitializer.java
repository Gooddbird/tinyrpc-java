package com.iker.tinyrpc;

import com.iker.tinyrpc.util.SpringContextUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

@Component
@Order(value = 1)
@Slf4j
public class TinyRpcInitializer implements CommandLineRunner {

    @Getter
    @Resource
    private AnnotationConfigApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        SpringContextUtil.setApplicationContext(applicationContext);
        log.info(String.valueOf(applicationContext.getClass()));
        String[] beans = applicationContext.getBeanDefinitionNames();
        Arrays.sort(beans);
        for (String bean : beans) {
            System.out.println(bean + " of Type :: " + applicationContext.getBean(bean).getClass());
        }
    }
}