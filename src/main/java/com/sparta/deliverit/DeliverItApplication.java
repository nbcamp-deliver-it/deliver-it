package com.sparta.deliverit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class DeliverItApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliverItApplication.class, args);
    }
}
