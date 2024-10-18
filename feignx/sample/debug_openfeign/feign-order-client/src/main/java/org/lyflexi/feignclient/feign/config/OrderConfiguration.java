package org.lyflexi.feignclient.feign.config;

import org.lyflexi.feignclient.feign.interceptor.OrderInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: lyflexi
 * @project: debuginfo_jdkToFramework
 * @Date: 2024/7/27 14:11
 */
@Configuration
public class OrderConfiguration {

    @Bean
    public OrderInterceptor orderInterceptor(){
        return new OrderInterceptor();
    }
}
