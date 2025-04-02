package org.lyflexi.feignclient.feign.client;

import org.lyflexi.cloudfeignapi.User;
import org.lyflexi.feignclient.feign.config.UserConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: hmly
 * @Date: 2025/3/20 21:56
 * @Project: debug_openfeign
 * @Version: 1.0.0
 * @Description:
 */
@FeignClient(path = "/hello/world/user",value = "cloud-feign-server", contextId = "user", configuration = UserConfiguration.class)
public interface UserClient6 {

    @GetMapping(value = "/updateeee/{id}")
    User updateeeeee(@PathVariable("id") Long id);

}
