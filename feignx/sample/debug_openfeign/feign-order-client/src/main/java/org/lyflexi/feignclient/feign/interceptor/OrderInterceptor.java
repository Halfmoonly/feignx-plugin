package org.lyflexi.feignclient.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @Author: lyflexi
 * @project: debuginfo_jdkToFramework
 * @Date: 2024/7/27 22:06
 */
//@Component
public class OrderInterceptor implements RequestInterceptor {
    public OrderInterceptor() {
        System.out.println("OrderInterceptor...");
    }
    @Override
    public void apply(RequestTemplate template) {
        System.out.println("OrderInterceptor...");
    }
}
