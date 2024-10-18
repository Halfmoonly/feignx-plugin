package org.lyflexi.feignclient;

import org.lyflexi.feignclient.feign.config.DefaultConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@EnableFeignClients(defaultConfiguration = {DefaultConfiguration.class}) // 开启feign
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,pattern = "org.lyflexi.feignclient.feign.config.*"))
@EnableDiscoveryClient
public class FeignClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeignClientApplication.class, args);
    }

}
