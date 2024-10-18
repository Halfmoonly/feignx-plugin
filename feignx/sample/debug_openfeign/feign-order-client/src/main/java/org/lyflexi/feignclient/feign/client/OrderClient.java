package org.lyflexi.feignclient.feign.client;

import org.lyflexi.cloudfeignapi.Order;
import org.lyflexi.feignclient.feign.config.OrderConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: lyflexi
 * @project: debuginfo_jdkToFramework
 * @Date: 2024/7/27 14:12
 */
// http://localhost:9000/consumer/feign/order/get/1
@FeignClient(value = "cloud-feign-server", contextId = "order", configuration = OrderConfiguration.class)
public interface OrderClient {

    @GetMapping(value = "/order/get/{id}")
    Order getOrderById(@PathVariable("id") Long id);
}