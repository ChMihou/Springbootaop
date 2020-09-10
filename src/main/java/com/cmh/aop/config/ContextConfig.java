package com.cmh.aop.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

//错误重定向或者页面不存在指定跳转
@Configuration
public class ContextConfig {
    @Bean
    public ErrorPageRegistrar errorPageRegistrar() {
        return registry -> {
            ErrorPage page404 = new ErrorPage(HttpStatus.NOT_FOUND, "/errorPage");
            registry.addErrorPages(page404);
        };
    }
}
