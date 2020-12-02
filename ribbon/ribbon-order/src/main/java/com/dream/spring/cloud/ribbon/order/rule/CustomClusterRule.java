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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lv.QingYu
 * @description 同集群优先权重负载均衡策略
 */
@Slf4j
@Component
public class CustomClusterRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        // TODO Auto-generated method stub 读取配置文件并且初始化,ribbon内部的,几乎用不上
    }

    @Override
    public Server choose(Object key) {
        // 获得服务集群名称
        try {
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            log.info("[同集群优先权重负载均衡策略]-loadBalancer:{}", loadBalancer);
            String serverName = loadBalancer.getName();
            log.info("[同集群优先权重负载均衡策略]-serverName:{}", serverName);
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
            String currentClusterName = nacosDiscoveryProperties.getClusterName();
            log.info("[同集群优先权重负载均衡策略]-currentClusterName:{}", currentClusterName);

            List<Instance> instanceList = namingService.getAllInstances(serverName);
            List<Instance> hitInstanceList = new ArrayList<>();
            for (Instance instance : instanceList) {
                String clusterName = instance.getClusterName();
                if (StringUtils.equalsIgnoreCase(clusterName, currentClusterName)) {
                    hitInstanceList.add(instance);
                }
            }
            if (hitInstanceList.isEmpty()) {
                Instance instance = WeightedBalancer.chooseInstanceByRandomWeight(instanceList);
                log.info("[同集群优先权重负载均衡策略]-跨集群调用,clusterName:{},port:{}", instance.getClusterName(), instance.getPort());
                return new NacosServer(instance);
            }
            Instance instance = WeightedBalancer.chooseInstanceByRandomWeight(hitInstanceList);
            log.info("[同集群优先权重负载均衡策略]-同集群调用,clusterName:{},port:{}", instance.getClusterName(), instance.getPort());
            return new NacosServer(instance);
        } catch (Exception e) {
            log.error("[同集群优先权重负载均衡策略]-error！msg:{}", Throwables.getStackTraceAsString(e));
        }
        return null;
    }
}
