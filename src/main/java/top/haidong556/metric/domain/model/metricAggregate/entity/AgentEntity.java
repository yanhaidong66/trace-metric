package top.haidong556.metric.domain.model.metricAggregate.entity;

import lombok.Builder;
import lombok.Getter;

/**
 * AgentEntity类用于封装有关Agent的信息，这些数据是由Metricbeat收集的。
 */
@Builder
@Getter
public class AgentEntity {
    public String type; // Agent的类型（例如 Metricbeat, Filebeat）
    public String version; // Agent的版本（例如 7.10.0）
    public String ephemeralId; // Agent的临时ID，通常用于容器化环境
    public String id; // Agent的唯一标识符
    public String name; // Agent的名称

    @Override
    public String toString() {
        return "{\n" +
                "        \"type\": \"" + type + "\",\n" +
                "        \"version\": \"" + version + "\",\n" +
                "        \"ephemeral_id\": \"" + ephemeralId + "\",\n" +
                "        \"id\": \"" + id + "\",\n" +
                "        \"name\": \"" + name + "\"\n" +
                "    }";
    }
}

