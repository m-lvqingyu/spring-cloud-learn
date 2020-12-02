package com.dream.spring.cloud.ribbon.order.rule;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;

import java.util.List;

/**
 * @author Lv.QingYu
 */
public class WeightedBalancer extends Balancer {

    public static Instance chooseInstanceByRandomWeight(List<Instance> host) {
        return getHostByRandomWeight(host);
    }

}
