package com.dream.spring.cloud.ribbon.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Lv.QingYu
 */
@EnableDiscoveryClient
@SpringBootApplication
public class RibbonOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(RibbonOrderApplication.class, args);
    }

}
