package org.lyflexi.feignclient.feign.client;

import org.lyflexi.cloudfeignapi.User;

/**
 * @Author: liuyanoutsee@outlook.com
 * @Date: 2025/4/5 21:37
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description:
 */
public class MyTestFallBackFactory implements org.springframework.cloud.openfeign.FallbackFactory<UserClient3> {
    @Override
    public UserClient3 create(Throwable cause) {
        return new UserClient3() {
            @Override
            public User parallelScan8(Long id) {
                return null;
            }
        };
    }
}
