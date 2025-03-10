package org.lyflexi.feignclient.feign.client;

import org.lyflexi.cloudfeignapi.User;
import org.lyflexi.feignclient.feign.config.UserConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: lyflexi
 * @project: debuginfo_jdkToFramework
 * @Date: 2024/7/27 14:13
 */
// http://localhost:9000/consumer/feign/user/get/1
@FeignClient(path = "/hello/world/user",value = "cloud-feign-server", contextId = "user", configuration = UserConfiguration.class)
public interface UserClient2 {

    @GetMapping(value = "/get/{id}")
    User getUserById(@PathVariable("id") Long id);

    @GetMapping(value = "/update2/{id}")
    User update(@PathVariable("id") Long id);

    @GetMapping(value = "/del/{id}")
    User del(@PathVariable("id") Long id);

    @GetMapping(value = "/getfather/{id}")
    User getfather(@PathVariable("id") Long id);

    @GetMapping(value = "/getmather/{id}")
    User getmather(@PathVariable("id") Long id);

    @GetMapping(value = "/clipboard/{id}")
    User clipboard(@PathVariable("id") Long id);

    @GetMapping(value = "/clipboard2/{id}")
    User clipboard2(@PathVariable("id") Long id);
}