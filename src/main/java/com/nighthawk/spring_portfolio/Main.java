package com.nighthawk.spring_portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;  // Add this import

// @SpringBootApplication annotation is the key to building web applications with Java https://spring.io/projects/spring-boot
@SpringBootApplication
@EnableScheduling  // Add this annotation to enable scheduled tasks
public class Main {

    // Starts a spring application as a stand-alone application from the main method
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}