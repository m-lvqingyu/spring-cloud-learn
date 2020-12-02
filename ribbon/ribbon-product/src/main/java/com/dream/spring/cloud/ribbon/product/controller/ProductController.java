package com.dream.spring.cloud.ribbon.product.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author Lv.QingYu
 */
@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    @Value("${server.port}")
    private int port;

    @GetMapping("getDetails")
    public String getProductDetails() {
        String productId = UUID.randomUUID().toString();
        log.info("[ribbon-learn]-product server被调用了,port:{}", port);
        return productId;
    }
}
