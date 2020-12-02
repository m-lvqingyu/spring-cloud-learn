package com.dream.spring.cloud.ribbon.order.rule;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.base.Throwables;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Lv.QingYu
 * @Description 权重负载均衡策略
 */
@Slf4j
//@Component
public class CustomWeightRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        // TODO Auto-generated method stub 读取配置文件并且初始化,ribbon内部的,几乎用不上
    }

    @Override
    public Server choose(Object key) {
        try {
            log.info("[权重负载均衡策略]-key:{}", key);
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            log.info("[权重负载均衡策略]-loadBalancer:{}", loadBalancer);
            // 获取服务名称
            String serverName = loadBalancer.getName();
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
            //获取一个基于nacos client 实现权重的负载均衡算法
            Instance instance = namingService.selectOneHealthyInstance(serverName);
            return new NacosServer(instance);
        } catch (Exception e) {
            log.error("[权重负载均衡策略]-error！msg:{}", Throwables.getStackTraceAsString(e));
        }
        return null;
    }
}
