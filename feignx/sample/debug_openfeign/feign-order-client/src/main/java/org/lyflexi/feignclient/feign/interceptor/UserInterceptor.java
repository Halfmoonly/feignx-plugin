package org.lyflexi.feignclient.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @Author: lyflexi
 * @project: debuginfo_jdkToFramework
 * @Date: 2024/7/27 22:06
 */
//@Component
public class UserInterceptor implements RequestInterceptor {
    public UserInterceptor() {
        System.out.println("UserInterceptor...");
    }

    @Override
    public void apply(RequestTemplate template) {
        System.out.println("UserInterceptor...");
    }
}
