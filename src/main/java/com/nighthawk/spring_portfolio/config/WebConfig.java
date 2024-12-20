package com.nighthawk.spring_portfolio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
    .allowedOrigins(
    "http://localhost:4100", 
    "http://127.0.0.1:4100",
    "http://localhost:8088", "http://127.0.0.1:8088"
    )
    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    .allowedHeaders("*")
    .allowCredentials(true);
    }}