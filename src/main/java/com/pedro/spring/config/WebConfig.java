package com.pedro.spring.config;

import com.pedro.spring.interceptors.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.templateparser.text.CSSTemplateParser;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.HashSet;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor
                        (new RequestInterceptor())
                .excludePathPatterns(
                        "/css/**/",
                        "/js/**",
                        "/imgs/**",
                        "/sing-in",
                        "/esqueceu",
                        "/criar",
                        "/create-account",
                        "/remember-password",
                        "/");
    }

}
