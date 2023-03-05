package com.iker.tinyrpc;

import com.iker.tinyrpc.net.TcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class TinyRpcJavaApplication implements CommandLineRunner {

    public static void main(String[] args) {
        log.debug("test TinyRpcJavaApplication run");
        SpringApplication.run(TinyRpcJavaApplication.class, args);
        log.debug("test TinyRpcJavaApplication run");
    }

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private TcpServer tcpServer;

    /**
     * @param args incoming main method arguments
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
//        String[] beans = applicationContext.getBeanDefinitionNames();
//        Arrays.sort(beans);
//        for (String bean : beans)
//        {
//            System.out.println(bean + " of Type :: " + applicationContext.getBean(bean).getClass());
//        }
//        tcpServer.bind(12345);
    }
}
