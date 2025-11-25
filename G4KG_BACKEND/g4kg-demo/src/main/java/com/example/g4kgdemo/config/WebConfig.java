package com.example.g4kgdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径
                .allowedOrigins("http://10.201.1.80") // 需要改成前端运行的ip地址，之前是实验室ip http://10.201.12.44:8080/，
                .allowedMethods("GET", "POST") // 允许的方法
                .allowedHeaders("*") // 允许所有头部
                .allowCredentials(true); // 允许发送 cookies
    }
}
