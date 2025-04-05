package org.lyflexi.feignclient.feign.client;

import org.lyflexi.cloudfeignapi.User;
import org.lyflexi.cloudfeignapi.UserApiConst;
import org.lyflexi.feignclient.feign.config.UserConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.concurrent.FutureAdapter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: wdhcr
 * @project: debuginfo_jdkToFramework
 * @Date: 2025/03/20 15:08
 */
@FeignClient(path = UserApiConst.USER_CLIENT_BASE, value = "cloud-feign-server", contextId = "user", configuration = UserConfiguration.class,fallbackFactory = MyTestFallBackFactory.class)
public interface UserClient3 {


    @GetMapping(UserApiConst.USER_CLIENT_PARALLEL_SCAN9_ID)
    User parallelScan8(@PathVariable("id") Long id);



}