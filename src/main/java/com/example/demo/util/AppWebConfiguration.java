package com.example.demo.util;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
public class AppWebConfiguration implements WebMvcConfigurer {
	@Resource
    LoginInterceptor loginInterceptor;
 
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	System.out.println("=== AppWebConfiguration -> addInterceptors ===");
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
    }
}
