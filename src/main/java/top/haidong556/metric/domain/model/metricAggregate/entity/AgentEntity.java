package top.haidong556.metric.domain.model.metricAggregate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AgentEntity类用于封装有关Agent的信息，这些数据是由Metricbeat收集的。
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AgentEntity {
    public String type; // Agent的类型（例如 Metricbeat, Filebeat）
    public String version; // Agent的版本（例如 7.10.0）
    public String ephemeral_id; // Agent的临时ID，通常用于容器化环境
    public String id; // Agent的唯一标识符
    public String name; // Agent的名称

    @Override
    public String toString() {
        return "{\n" +
                "        \"type\": \"" + type + "\",\n" +
                "        \"version\": \"" + version + "\",\n" +
                "        \"ephemeral_id\": \"" + ephemeral_id + "\",\n" +
                "        \"id\": \"" + id + "\",\n" +
                "        \"name\": \"" + name + "\"\n" +
                "    }";
    }
}

