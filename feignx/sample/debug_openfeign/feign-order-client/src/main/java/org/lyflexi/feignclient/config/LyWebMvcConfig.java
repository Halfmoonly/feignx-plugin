package org.lyflexi.feignclient.config;

import org.lyflexi.feignclient.feign.client.OrderClient;
import org.lyflexi.feignclient.feign.client.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: lyflexi
 * @project: debuginfo_jdkToFramework
 * @Date: 2024/7/27 22:55
 */
@Configuration
public class LyWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    UserClient  userClient;
    @Autowired
    OrderClient orderClient;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LyInterceptor());
    }

}
