package com.javaproject.javatask.app;

import com.javaproject.javatask.rest.JavaTaskController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.javaproject.javatask"})
public class JavaTaskApplication {

    private static final Logger logger = LoggerFactory.getLogger(JavaTaskController.class);

    public static void main(String[] args) {
        logger.info("Starting SpringApplication");
        SpringApplication.run(JavaTaskApplication.class, args);
        logger.info("SpringApplication is running");
    }

}
