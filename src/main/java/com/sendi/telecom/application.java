package com.sendi.telecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author HQX
 * 2021/2/28 21:07
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class application {
    public static void main(String[] args) {
        SpringApplication.run(application.class, args);
    }
}
