package top.haidong556.metric.domain.model.metricAggregate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import top.haidong556.metric.domain.common.JsonReflectiveBuilder;
import top.haidong556.metric.domain.model.machineMonitorAggregate.MachineIdentification;
import top.haidong556.metric.domain.model.metricAggregate.entity.*;
import top.haidong556.metric.domain.model.metricAggregate.vo.*;

import java.util.List;
import java.util.Map;

@Getter
public class MetricAggregateRoot {
    @Setter
    private MetricAggregateId metricAggregateId;//具有时间顺序的Metric唯一ID
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
        public static MetricAggregateRoot build1(String json) throws Exception {
            if (json == null || json.isEmpty()) {
                throw new IllegalArgumentException("Json cannot be null or empty");
            }

            MetricAggregateRoot root = new MetricAggregateRoot();
            root.metricJson = json;

            // 使用 JsonReflectiveBuilder 解析对象
            root.serviceVo = JsonReflectiveBuilder.build(json, ServiceVo.class);
            root.elasticCommonSchemaVo = JsonReflectiveBuilder.build(json, EcsVo.class);
            root.metadataVo = JsonReflectiveBuilder.build(json, MetadataVo.class);
            root.processEntity = JsonReflectiveBuilder.build(json, ProcessEntity.class);
            root.hostEntity = JsonReflectiveBuilder.build(json, HostEntity.class);
            root.agentEntity = JsonReflectiveBuilder.build(json, AgentEntity.class);
            root.eventVo = JsonReflectiveBuilder.build(json, EventVo.class);
            root.metricSetVo = JsonReflectiveBuilder.build(json, MetricSetVo.class);
            root.userVo = JsonReflectiveBuilder.build(json, UserVo.class);

            // 解析 systemEntity，支持复杂嵌套
            root.systemEntity = JsonReflectiveBuilder.build(json, SystemEntity.class);

            // 解析 timestamp
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);
            if (rootNode.has("@timestamp")) {
                root.timestamp = rootNode.get("@timestamp").asText();
            }

            return root;
        }

        public static MetricAggregateRoot build(String json) throws JsonProcessingException {
            if (json == null || json.isEmpty()) {
                throw new IllegalArgumentException("Json cannot be null or empty");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            // 让 Jackson 把所有整数解析成 Long
            objectMapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);
            Map<String, Object> properties = objectMapper.readValue(json, Map.class);
            MetricAggregateRoot root = new MetricAggregateRoot();

            root.metricJson = json;
            root.timestamp = properties.containsKey("@timestamp") ? properties.get("@timestamp").toString() : null;
            root.serviceVo = getServiceValue(properties);
            root.elasticCommonSchemaVo = getElasticCommonSchemaValue(properties);
            root.metadataVo = getMetadataValue(properties);
            root.processEntity = getProcessValue(properties);
            root.systemEntity = getSystemValue(properties);
            root.hostEntity = getHostValue(properties);
            root.agentEntity = getAgentValue(properties);
            root.eventVo = getEventValue(properties);
            root.metricSetVo = getMetricSetValue(properties);
            root.userVo=getUserValue(properties);
            return root;
        }

        private static UserVo getUserValue(Map<String, Object> properties) {
            UserVo userValue=null;
            if (properties != null && properties.containsKey("user")){
                Map<String, Object> userMap = (Map<String, Object>) properties.get("user");
                userValue = UserVo.builder()
                        .name(userMap.containsKey("name") ? (String) userMap.get("pid") : null)
                        .build();
            }
            return userValue;
        }

        private static ProcessEntity getProcessValue(Map<String, Object> properties) {
            ProcessEntity processValue = null;

            if (properties != null && properties.containsKey("process")) {
                Map<String, Object> processMap = (Map<String, Object>) properties.get("process");

                // 处理 parent 对象
                ProcessEntity.Parent parent = null;
                if (processMap.containsKey("parent")) {
                    Map<String, Object> parentMap = (Map<String, Object>) processMap.get("parent");
                    parent = ProcessEntity.Parent.builder()
                            .pid(parentMap.containsKey("pid") ? (Long) parentMap.get("pid") : 0)
                            .build();
                }

                // 处理 CPU 对象
                ProcessEntity.Cpu cpu = null;
                if (processMap.containsKey("cpu")) {
                    Map<String, Object> cpuMap = (Map<String, Object>) processMap.get("cpu");
                    cpu = ProcessEntity.Cpu.builder()
                            .startTime((String) cpuMap.get("start_time"))
                            .pct(cpuMap.containsKey("pct") ? ((Number) cpuMap.get("pct")).doubleValue() : 0)
                            .build();
                }

                // 处理 Memory 对象
                ProcessEntity.Memory memory = null;
                if (processMap.containsKey("memory")) {
                    Map<String, Object> memoryMap = (Map<String, Object>) processMap.get("memory");
                    memory = ProcessEntity.Memory.builder()
                            .pct(memoryMap.containsKey("pct") ? ((Number) memoryMap.get("pct")).doubleValue() : 0.0)
                            .build();
                }

                processValue = ProcessEntity.builder()
                        .commandLine(processMap.containsKey("command_line") ? (String) processMap.get("command_line") : null)
                        .executable(processMap.containsKey("executable") ? (String) processMap.get("executable") : null)
                        .name(processMap.containsKey("name") ? (String) processMap.get("name") : null)
                        .pid(processMap.containsKey("pid") ? (long) processMap.get("pid") : 0)
                        .state(processMap.containsKey("state") ? (String) processMap.get("state") : null)
                        .workingDirectory(processMap.containsKey("working_directory") ? (String) processMap.get("working_directory") : null)
                        .args(processMap.containsKey("args") ? (List<String>) processMap.get("args") : null)
                        .pgId(processMap.containsKey("pgid") ? (long) processMap.get("pgid") : 0)
                        .parent(parent)
                        .cpu(cpu)
                        .memory(memory)
                        .build();
            }

            return processValue;
        }

        private static EcsVo getElasticCommonSchemaValue(Map<String, Object> properties) {
            EcsVo ecsVo = null;
            if (properties != null && properties.containsKey("ecs")) {
                Map<String, Object> ecsMap = (Map<String, Object>) properties.get("ecs");
                ecsVo = EcsVo.builder()
                        .version(ecsMap.containsKey("version") ? (String) ecsMap.get("version") : null)
                        .build();
            }
            return ecsVo;
        }

        private static ServiceVo getServiceValue(Map<String, Object> properties) {
            ServiceVo serviceValue = null;
            if (properties.containsKey("service")) {
                Map<String, Object> serviceMap = (Map<String, Object>) properties.get("service");
                serviceValue = ServiceVo.builder()
                        .type(serviceMap.containsKey("type") ? (String) serviceMap.get("type") : null)
                        .build();

            }
            return serviceValue;
        }

        private static MetricSetVo getMetricSetValue(Map<String, Object> properties) {
            MetricSetVo metricSetValue = null;
            if (properties.containsKey("metricset")) {
                Map<String, Object> metricSetMap = (Map<String, Object>) properties.get("metricset");
                metricSetValue = MetricSetVo.builder()
                        .name(metricSetMap.containsKey("name") ? (String) metricSetMap.get("name") : null)
                        .period(metricSetMap.containsKey("period") ? (long) metricSetMap.get("period") : 0L)
                        .build();
            }
            return metricSetValue;
        }

        private static EventVo getEventValue(Map<String, Object> properties) {
            EventVo eventValue = null;
            if (properties.containsKey("event")) {
                Map<String, Object> eventMap = (Map<String, Object>) properties.get("event");
                eventValue = EventVo.builder()
                        .duration(eventMap.containsKey("duration") ? (long) eventMap.get("duration") : 0L)
                        .dataset(eventMap.containsKey("dataset") ? (String) eventMap.get("dataset") : null)
                        .module(eventMap.containsKey("module") ? (String) eventMap.get("module") : null)
                        .build();
            }
            return eventValue;
        }

        private static AgentEntity getAgentValue(Map<String, Object> properties) {
            AgentEntity agentValue = null;
            if (properties.containsKey("agent")) {
                Map<String, Object> agentMap = (Map<String, Object>) properties.get("agent");
                agentValue = AgentEntity.builder()
                        .type(agentMap.containsKey("type") ? (String) agentMap.get("type") : null)
                        .version(agentMap.containsKey("version") ? (String) agentMap.get("version") : null)
                        .ephemeralId(agentMap.containsKey("ephemeral_id") ? (String) agentMap.get("ephemeral_id") : null)
                        .id(agentMap.containsKey("id") ? (String) agentMap.get("id") : null)
                        .name(agentMap.containsKey("name") ? (String) agentMap.get("name") : null)
                        .build();

            }
            return agentValue;
        }

        private static HostEntity getHostValue(Map<String, Object> properties) {
            HostEntity hostValue = null;
            if (properties.containsKey("host")) {
                Map<String, Object> hostMap = (Map<String, Object>) properties.get("host");
                HostEntity.Os osValue = null;
                if (hostMap.containsKey("os")) {
                    Map<String, Object> osMap = (Map<String, Object>) hostMap.get("os");
                    osValue = HostEntity.Os.builder()
                            .codename(osMap.containsKey("codename") ? (String) osMap.get("codename") : null)
                            .type(osMap.containsKey("type") ? (String) osMap.get("type") : null)
                            .platform(osMap.containsKey("platform") ? (String) osMap.get("platform") : null)
                            .version(osMap.containsKey("version") ? (String) osMap.get("version") : null)
                            .family(osMap.containsKey("family") ? (String) osMap.get("family") : null)
                            .name(osMap.containsKey("name") ? (String) osMap.get("name") : null)
                            .kernel(osMap.containsKey("kernel") ? (String) osMap.get("kernel") : null)
                            .build();
                }

                hostValue = HostEntity.builder()
                        .ip(hostMap.containsKey("ip") ? (List<String>) hostMap.get("ip") : null)
                        .mac(hostMap.containsKey("mac") ? (List<String>) hostMap.get("mac") : null)
                        .name(hostMap.containsKey("name") ? (String) hostMap.get("name") : null)
                        .hostname(hostMap.containsKey("hostname") ? (String) hostMap.get("hostname") : null)
                        .architecture(hostMap.containsKey("architecture") ? (String) hostMap.get("architecture") : null)
                        .id(hostMap.containsKey("id") ? (String) hostMap.get("id") : null)
                        .containerized(hostMap.containsKey("containerized") ? (boolean) hostMap.get("containerized") : false)
                        .os(osValue)
                        .build();
            }
            return hostValue;
        }

        private static SystemEntity getSystemValue(Map<String, Object> properties) {
            SystemEntity systemValue = null;
            if (properties.containsKey("system")) {
                Map<String, Object> systemMap = (Map<String, Object>) properties.get("system");
                SystemEntity.Network networkValue = null;
                SystemEntity.Process processValue = null;
                if (systemMap.containsKey("network")) {
                    Map<String, Object> networkMap = (Map<String, Object>) systemMap.get("network");
                    SystemEntity.Network.In inValue = null;
                    SystemEntity.Network.Out outValue = null;
                    if (networkMap.containsKey("in")) {
                        Map<String, Object> inMap = (Map<String, Object>) networkMap.get("in");
                        SystemEntity.Network.In.builder()
                                .packets(inMap.containsKey("packets") ? (Long) inMap.get("packets") : null)
                                .dropped(inMap.containsKey("dropped") ? (Long) inMap.get("dropped") : null)
                                .bytes(inMap.containsKey("bytes") ? (Long) inMap.get("bytes") : null)
                                .errors(inMap.containsKey("errors") ? (Long) inMap.get("errors") : null)
                                .build();
                    }
                    if (networkMap.containsKey("out")) {
                        Map<String, Object> outMap = (Map<String, Object>) networkMap.get("out");
                        SystemEntity.Network.Out.builder()
                                .packets(outMap.containsKey("packets") ? (Long) outMap.get("packets") : null)
                                .dropped(outMap.containsKey("dropped") ? (Long) outMap.get("dropped") : null)
                                .bytes(outMap.containsKey("bytes") ? (Long) outMap.get("bytes") : null)
                                .errors(outMap.containsKey("errors") ? (Long) outMap.get("errors") : null)
                                .build();

                    }
                    networkValue = SystemEntity.Network.builder()
                            .in(inValue)
                            .out(outValue)
                            .name(networkMap.containsKey("name") ? (String) networkMap.get("name") : null)
                            .build();

                }

                if (systemMap.containsKey("process")) {
                    Map<String, Object> processMap = (Map<String, Object>) systemMap.get("process");
                    SystemEntity.Process.Memory memoryValue = null;
                    SystemEntity.Process.Cgroup cgroupValue = null;
                    SystemEntity.Process.Fd fdValue = null;
                    SystemEntity.Process.Cpu cpuValue = null;

                    if (processMap.containsKey("memory")) {
                        Map<String, Object> memoryMap = (Map<String, Object>) processMap.get("memory");
                        SystemEntity.Process.Memory.Rss rssValue = null;

                        if (memoryMap.containsKey("rss")) {
                            Map<String, Object> rssMap = (Map<String, Object>) memoryMap.get("rss");

                            rssValue = SystemEntity.Process.Memory.Rss.builder()
                                    .pct(rssMap.containsKey("pct") ? (Double) ((Number)rssMap.get("pct")).doubleValue() : null)
                                    .bytes(rssMap.containsKey("bytes") ? (long) rssMap.get("bytes") : null)
                                    .build();
                        }

                        memoryValue = SystemEntity.Process.Memory.builder()
                                .share(memoryMap.containsKey("share") ? (long) memoryMap.get("share") : null)
                                .size(memoryMap.containsKey("size") ? (long) memoryMap.get("size") : null)
                                .rss(rssValue)
                                .build();
                    }

                    if (processMap.containsKey("cgroup")) {
                        Map<String, Object> cgroupMap = (Map<String, Object>) processMap.get("cgroup");

                        // Initialize the Cgroup builder
                        cgroupValue = SystemEntity.Process.Cgroup.builder()
                                .id(cgroupMap.containsKey("id") ? (String) cgroupMap.get("id") : null)
                                .path(cgroupMap.containsKey("path") ? (String) cgroupMap.get("path") : null)
                                .cgroupsVersion(cgroupMap.containsKey("cgroupsVersion") ? (long) cgroupMap.get("cgroupsVersion") : 0)
                                .build();
                    }
                    if (processMap.containsKey("fd")) {
                        Map<String, Object> fdMap = (Map<String, Object>) processMap.get("fd");

                        // Initialize the Fd.Limit builder
                        SystemEntity.Process.Fd.Limit fdLimit = null;
                        if (fdMap.containsKey("limit")) {
                            Map<String, Object> limitMap = (Map<String, Object>) fdMap.get("limit");
                            fdLimit = SystemEntity.Process.Fd.Limit.builder()
                                    .hard(limitMap.containsKey("hard") ? (long) limitMap.get("hard") : 0)
                                    .soft(limitMap.containsKey("soft") ? (long) limitMap.get("soft") : 0)
                                    .build();
                        }

                        // Initialize the Fd builder
                        fdValue = SystemEntity.Process.Fd.builder()
                                .open(fdMap.containsKey("open") ? (long) fdMap.get("open") : 0)
                                .limit(fdLimit)
                                .build();
                    }

                    if (processMap.containsKey("cpu")) {
                        Map<String, Object> cpuMap = (Map<String, Object>) processMap.get("cpu");

                        SystemEntity.Process.Cpu.Total cpuTotalValue = null;
                        if (cpuMap.containsKey("total")) {
                            Map<String, Object> totalMap = (Map<String, Object>) cpuMap.get("total");
                            SystemEntity.Process.Cpu.Total.Norm cpuNormValue = null;
                            if (totalMap.containsKey("norm")) {
                                Map<String, Object> normMap = (Map<String, Object>) totalMap.get("norm");
                                cpuNormValue = SystemEntity.Process.Cpu.Total.Norm.builder()
                                        .pct(normMap.containsKey("pct") ?  ((Number)normMap.get("pct")).doubleValue() : 0.0)
                                        .build();
                            }

                            cpuTotalValue = SystemEntity.Process.Cpu.Total.builder()
                                    .norm(cpuNormValue)
                                    .value(totalMap.containsKey("value") ? (long) totalMap.get("value") : 0)
                                    .pct(totalMap.containsKey("pct") ? ((Number)totalMap.get("pct")).doubleValue() : 0.0)
                                    .build();
                        }

                        // Initialize the Cpu builder
                        cpuValue = SystemEntity.Process.Cpu.builder()
                                .startTime(cpuMap.containsKey("start_time") ? (String) cpuMap.get("start_time") : null)
                                .total(cpuTotalValue)
                                .build();
                    }

                    processValue = SystemEntity.Process.builder()
                            .cgroup(cgroupValue)
                            .memory(memoryValue)
                            .state((String) processMap.getOrDefault("state", null))
                            .numThreads((long) processMap.getOrDefault("num_threads", 0))
                            .cmdLine((String) processMap.getOrDefault("cmdline", null))
                            .fd(fdValue)
                            .cpu(cpuValue)
                            .build();
                }

                systemValue = SystemEntity.builder()
                        .process(processValue)
                        .network(networkValue)
                        .state((String) systemMap.getOrDefault("state", null))
                        .build();
            }
            return systemValue;
        }

        private static MetadataVo getMetadataValue(Map<String, Object> properties) {
            MetadataVo metadataValue = null;
            if (properties.containsKey("metadata")) {
                Map<String, Object> metadataMap = (Map<String, Object>) properties.get("metadata");
                metadataValue = MetadataVo.builder()
                        .beat(metadataMap.containsKey("beat") ? (String) metadataMap.get("beat") : null)
                        .type(metadataMap.containsKey("type") ? (String) metadataMap.get("type") : null)
                        .version(metadataMap.containsKey("version") ? (String) metadataMap.get("version") : null)
                        .build();
            }
            return metadataValue;
        }
    }

    @Override
    public String toString() {
        return "{\n" +
                "    \"@timestamp\": \"" + timestamp + "\",\n" +
                "    \"metadata\": " + (metadataVo != null ? metadataVo.toString() : "null") + ",\n" +
                "    \"process\": " + (processEntity != null ? processEntity.toString() : "null") + ",\n" +
                "    \"user\":"+(userVo!=null?userVo.toString():"null")+",\n" +
                "    \"service\": " + (serviceVo != null ? serviceVo.toString() : "null") + ",\n" +
                "    \"system\": " + (systemEntity != null ? systemEntity.toString() : "null") + ",\n" +
                "    \"event\": " + (eventVo != null ? eventVo.toString() : "null") + ",\n" +
                "    \"metricset\": " + (metricSetVo != null ? metricSetVo.toString() : "null") + ",\n" +
                "    \"host\": " + (hostEntity != null ? hostEntity.toString() : "null") + ",\n" +
                "    \"agent\": " + (agentEntity != null ? agentEntity.toString() : "null") + ",\n" +
                "    \"ecs\": " + (elasticCommonSchemaVo != null ? elasticCommonSchemaVo.toString() : "null") + "\n" +
                "}";
    }

}