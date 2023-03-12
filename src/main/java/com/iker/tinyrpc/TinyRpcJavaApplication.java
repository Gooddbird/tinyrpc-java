package com.iker.tinyrpc;

import com.iker.tinyrpc.net.TcpServer;
import com.iker.tinyrpc.util.SpringContextUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.Resource;
import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class TinyRpcJavaApplication {

    public static void main(String[] args) {
        log.info("TinyRpcJavaApplication run begin");
        SpringApplication.run(TinyRpcJavaApplication.class, args);
        log.info("TinyRpcJavaApplication run end");


    }

}
