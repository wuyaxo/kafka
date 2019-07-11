package com.neoby.dm.middleware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class MiddlewareApplication {


    public static void main(String[] args) {
        SpringApplication.run(MiddlewareApplication.class, args);
        log.info("程序启动完毕了");
    }

}
