package top.haidong556.metric.domain.model.metricAggregate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import top.haidong556.metric.domain.common.JsonReflectiveBuilder;
import top.haidong556.metric.domain.common.SnowflakeIdGenerator;
import top.haidong556.metric.domain.model.machineMonitorAggregate.MachineIdentification;
import top.haidong556.metric.domain.model.metricAggregate.entity.*;
import top.haidong556.metric.domain.model.metricAggregate.vo.*;

import java.util.List;

@Getter
public class MetricAggregateRoot {
    @Setter
    private MetricAggregateRootId metricAggregateId;//具有时间顺序的Metric唯一ID
    @Setter
    private MachineIdentification machineIdentification;//这个Metric的来源的机器终端标识
    private String metricJson;
    private String timestamp;
    private MetadataVo metadataVo;
    private ServiceVo serviceVo;
    private ProcessEntity processEntity;
    private SystemEntity systemEntity;
    private EcsVo elasticCommonSchemaVo;
    private HostEntity hostEntity;
    private AgentEntity agentEntity;
    private EventVo eventVo;
    private MetricSetVo metricSetVo;
    private UserVo userVo;

    private MetricAggregateRoot() {
    }


    public static class Builder {
        public static MetricAggregateRoot buildByJson(String json) throws Exception {
            if (json == null || json.isEmpty()) {
                throw new IllegalArgumentException("Json cannot be null or empty");
            }

            // 创建 MetricAggregateRoot 对象
            MetricAggregateRoot root = new MetricAggregateRoot();
            root.metricJson = json;

            // 使用 ObjectMapper 解析根节点
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);

            // 自动解析 @timestamp 字段
            if (rootNode.has("@timestamp")) {
                root.timestamp = rootNode.get("@timestamp").asText();
            }

            // 为每个子对象提取对应的子 JSON，并传递给 JsonReflectiveBuilder
            if (rootNode.has("service")) {
                root.serviceVo = JsonReflectiveBuilder.build(rootNode.get("service").toString(), ServiceVo.class);
            }

            if (rootNode.has("ecs")) {
                root.elasticCommonSchemaVo = JsonReflectiveBuilder.build(rootNode.get("ecs").toString(), EcsVo.class);
            }

            if (rootNode.has("metadata")) {
                root.metadataVo = JsonReflectiveBuilder.build(rootNode.get("metadata").toString(), MetadataVo.class);
            }

            if (rootNode.has("process")) {
                root.processEntity = JsonReflectiveBuilder.build(rootNode.get("process").toString(), ProcessEntity.class);
            }

            if (rootNode.has("host")) {
                root.hostEntity = JsonReflectiveBuilder.build(rootNode.get("host").toString(), HostEntity.class);
            }

            if (rootNode.has("agent")) {
                root.agentEntity = JsonReflectiveBuilder.build(rootNode.get("agent").toString(), AgentEntity.class);
            }

            if (rootNode.has("event")) {
                root.eventVo = JsonReflectiveBuilder.build(rootNode.get("event").toString(), EventVo.class);
            }

            if (rootNode.has("metricset")) {
                root.metricSetVo = JsonReflectiveBuilder.build(rootNode.get("metricset").toString(), MetricSetVo.class);
            }

            if (rootNode.has("user")) {
                root.userVo = JsonReflectiveBuilder.build(rootNode.get("user").toString(), UserVo.class);
            }

            if (rootNode.has("system")) {
                root.systemEntity = JsonReflectiveBuilder.build(rootNode.get("system").toString(), SystemEntity.class);
            }
            root.setMetricAggregateId(new MetricAggregateRootId(generateMetricAggregateId(root)));
            root.setMachineIdentification(new MachineIdentification(generateMachineIdentification(root)));
            return root;
        }

        private static String generateMetricAggregateId(MetricAggregateRoot root) {
            return SnowflakeIdGenerator.generateMetricAggregateId(root);
        }

        private static String generateMachineIdentification(MetricAggregateRoot root) {
            StringBuilder idBuilder = new StringBuilder();

            // 获取 IP 地址列表（如果有的话）
            List<String> ipAddresses = root.getHostEntity().getIp();
            if (ipAddresses != null && !ipAddresses.isEmpty()) {
                idBuilder.append('-').append(String.join(",", ipAddresses));
            }

            // 获取唯一的机器 ID（如果有的话）
            String hostId = root.getHostEntity().getId();
            if (hostId != null && !hostId.isEmpty()) {
                idBuilder.append('-').append(hostId);
            }

            // 最后返回拼接好的 machineIdentification 字符串
            return idBuilder.toString();
        }


    }
}