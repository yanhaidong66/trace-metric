package top.haidong556.metric.domain.model.metricAggregate.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
/**
 * HostEntity类用于封装有关主机的信息。
 */
@Builder
public class HostEntity {
    public List<String> ip; // 主机的IP地址列表
    public List<String> mac; // 主机的MAC地址列表
    public String name; // 主机名称
    public String hostname; // 主机的主机名
    public String architecture; // 主机的体系结构（例如 x86_64）
    public Os os; // 主机的操作系统信息
    public String id; // 主机的唯一标识符
    public boolean containerized; // 标识主机是否为容器化环境

    public String getMachineIdentification(){
        return ip+"-"+name+"-"+hostname+"-"+id;
    }

    @Override
    public String toString() {
        return "{\n" +
                "        \"name\": \"" + name + "\",\n" +
                "        \"hostname\": \"" + hostname + "\",\n" +
                "        \"architecture\": \"" + architecture + "\",\n" +
                "        \"os\": " + (os != null ? os.toString() : "null") + ",\n" +
                "        \"id\": \"" + id + "\",\n" +
                "        \"containerized\": " + containerized + ",\n" +
                "        \"ip\": " + jsonArray(ip) + ",\n" +
                "        \"mac\": " + jsonArray(mac) + "\n" +
                "    }";
    }

    private String jsonArray(List<String> list) {
        return list != null
                ? "[" + String.join(", ", list.stream().map(s -> "\"" + s + "\"").toArray(String[]::new)) + "]"
                : "null";
    }

    /**
     * OS内部类用于封装主机操作系统的相关信息。
     */
    @Getter
    @Builder
    public static class Os {
        public String codename; // 操作系统代号（例如 Ubuntu 20.04 的代号为 Focal Fossa）
        public String type; // 操作系统的类型（如 Linux, Windows）
        public String platform; // 操作系统平台（例如 x86_64, ARM）
        public String version; // 操作系统的版本号
        public String family; // 操作系统的家族（例如 Unix, Windows NT）
        public String name; // 操作系统名称（例如 Ubuntu, CentOS, Windows）
        public String kernel; // 操作系统内核版本

        @Override
        public String toString() {
            return "{\n" +
                    "            \"family\": \"" + family + "\",\n" +
                    "            \"name\": \"" + name + "\",\n" +
                    "            \"kernel\": \"" + kernel + "\",\n" +
                    "            \"codename\": \"" + codename + "\",\n" +
                    "            \"type\": \"" + type + "\",\n" +
                    "            \"platform\": \"" + platform + "\",\n" +
                    "            \"version\": \"" + version + "\"\n" +
                    "        }";
        }

    }
}

