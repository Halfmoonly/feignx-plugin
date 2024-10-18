package org.lyflexi.feignclient.controller;

import jakarta.annotation.Resource;
import org.lyflexi.cloudfeignapi.Order;
import org.lyflexi.cloudfeignapi.Result;
import org.lyflexi.feignclient.feign.client.OrderClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: lyflexi
 * @project: debuginfo_jdkToFramework
 * @Date: 2024/7/27 14:13
 */
// curl http://localhost:9000/consumer/feign/order/get/1
@RestController
public class OrderClientController {

    @Resource
    private OrderClient orderClient;

    @GetMapping(value = "/consumer/feign/order/get/{id}")
    public Result<Order> getOrderById(@PathVariable("id") Long id)
    {
        Order order = orderClient.getOrderById(id);
        return new Result<>(order);
    }
}