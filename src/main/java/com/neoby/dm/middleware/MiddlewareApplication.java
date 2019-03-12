package com.neoby.dm.middleware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MiddlewareApplication {


    public static Logger logger = LoggerFactory.getLogger(MiddlewareApplication.class);


    public static void main(String[] args) {

        SpringApplication.run(MiddlewareApplication.class, args);
        logger.info("程序启动完毕了");
    }

}
