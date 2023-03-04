package com.iker.tinyrpcjava;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class TinyRpcJavaApplication {

    public static void main(String[] args) {
        log.debug("test TinyRpcJavaApplication run");
        SpringApplication.run(TinyRpcJavaApplication.class, args);
        log.debug("test TinyRpcJavaApplication run");
    }

}
