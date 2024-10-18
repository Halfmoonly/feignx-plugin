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
@FeignClient(value = "cloud-feign-server", contextId = "user", configuration = UserConfiguration.class)
public interface UserClient {

    @GetMapping(value = "/user/get/{id}")
    User getUserById(@PathVariable("id") Long id);
}