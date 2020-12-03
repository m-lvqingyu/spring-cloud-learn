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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lv.QingYu
 * @description 同集群同版本优先权重，不同版本不能调用负载均衡策略
 */
@Slf4j
@Component
public class CustomSameClusterVersionRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object key) {
        try {
            // 获取服务名称
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            String serverName = loadBalancer.getName();
            // 获取调用方的集群名称和版本编号
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
            String clusterName = nacosDiscoveryProperties.getClusterName();
            String currentVersion = nacosDiscoveryProperties.getMetadata().get("current-version");

            List<Instance> allInstances = namingService.getAllInstances(serverName);
            List<Instance> hitInstanceList = findSameClusterVersionInstance(clusterName, currentVersion, allInstances);
            if (hitInstanceList.isEmpty()) {
                hitInstanceList = findSameClusterInstance(currentVersion, allInstances);
                if (hitInstanceList == null || hitInstanceList.isEmpty()) {
                    throw new RuntimeException(serverName + "not exist!");
                }
                Instance instance = WeightedBalancer.chooseInstanceByRandomWeight(hitInstanceList);
                return new NacosServer(instance);
            } else {
                // 同集群同版本实例
                Instance instance = WeightedBalancer.chooseInstanceByRandomWeight(hitInstanceList);
                return new NacosServer(instance);
            }
        } catch (Exception e) {
            log.error("[同集群同版本优先权重且不同版本不能调用负载均衡策略]-error!msg:{}", Throwables.getStackTraceAsString(e));
        }
        return null;
    }

    /**
     * 获取同集群同版本的实例列表
     *
     * @param clusterName    集群名称
     * @param currentVersion 版本号
     * @param allInstances   所有实例列表
     * @return
     */
    private List<Instance> findSameClusterVersionInstance(String clusterName, String currentVersion, List<Instance> allInstances) {
        List<Instance> hitInstanceList = new ArrayList<>();
        for (Instance instance : allInstances) {
            String instanceClusterName = instance.getClusterName();
            String instanceVersion = instance.getMetadata().get("current-version");
            if (instanceClusterName.equalsIgnoreCase(clusterName) && instanceVersion.equalsIgnoreCase(currentVersion)) {
                hitInstanceList.add(instance);
            }
        }
        return hitInstanceList;
    }

    /**
     * 获取同版本的实例列表
     *
     * @param currentVersion 版本号
     * @param allInstances   所有实例列表
     * @return
     */
    private List<Instance> findSameClusterInstance(String currentVersion, List<Instance> allInstances) {
        List<Instance> hitInstanceList = new ArrayList<>();
        for (Instance instance : allInstances) {
            String instanceVersion = instance.getMetadata().get("current-version");
            if (instanceVersion.equalsIgnoreCase(currentVersion)) {
                hitInstanceList.add(instance);
            }
        }
        return hitInstanceList;
    }
}
