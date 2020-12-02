package com.dream.spring.cloud.ribbon.order.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Lv.QingYu
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("create")
    public String create() {
        String orderNo = UUID.randomUUID().toString();
        String url = "http://ribbon-product/product/getDetails";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String productId = responseEntity.getBody();
        Map<String, String> resultMap = new HashMap<>(2);
        resultMap.put("orderNo", orderNo);
        resultMap.put("productId", productId);
        String result = JSON.toJSONString(resultMap);
        return result;
    }

}
