package com.iker.tinyrpc;

import com.iker.tinyrpc.util.SpringContextUtil;
import lombok.Getter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

@Component
@Order(value = 1)
public class TinyRpcInitializer implements CommandLineRunner {

    @Getter
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        SpringContextUtil.setApplicationContext(applicationContext);
        String[] beans = applicationContext.getBeanDefinitionNames();
        Arrays.sort(beans);
        for (String bean : beans)
        {
            System.out.println(bean + " of Type :: " + applicationContext.getBean(bean).getClass());
        }
    }
}
