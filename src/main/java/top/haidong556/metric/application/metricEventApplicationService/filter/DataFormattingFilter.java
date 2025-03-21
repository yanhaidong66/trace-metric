package top.haidong556.metric.application.metricEventApplicationService.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.haidong556.metric.application.common.filterChainTemplate.AbstractFilter;

/**
 * 数据格式化过滤器（Data Formatting Filter）。
 * <p>
 * 该过滤器用于对输入数据进行格式化，使其符合领域模型的要求。例如：
 * <ul>
 *     <li>将日期字符串转换为标准的日期格式。</li>
 *     <li>将数字转换为指定的小数位数。</li>
 *     <li>修正数据中的不一致或格式问题。</li>
 * </ul>
 * 该过滤器通常在数据进入领域模型之前执行，以确保数据符合系统的要求。
 * </p>
 * @author [haidong]
 * @version 1.0
 **/
public class DataFormattingFilter implements AbstractFilter<String> {

    @Override
    public int getOrder() {
        return 4;
    }

    @Override
    public void apply(String input) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(input);

        // IP 校验
        JsonNode ipNode = rootNode.at("/host/ip");
        if (ipNode.isMissingNode() || !ipNode.isArray()) {
            throw new IllegalArgumentException("host.ip 字段缺失或格式错误");
        }
        for (JsonNode ip : ipNode) {
            if (!isValidIP(ip.asText())) {
                throw new IllegalArgumentException("IP 格式非法: " + ip.asText());
            }
        }

        // MAC 校验
        JsonNode macNode = rootNode.at("/host/mac");
        if (macNode.isMissingNode() || !macNode.isArray()) {
            throw new IllegalArgumentException("host.mac 字段缺失或格式错误");
        }
        for (JsonNode mac : macNode) {
            if (!isValidMac(mac.asText())) {
                throw new IllegalArgumentException("MAC 格式非法: " + mac.asText());
            }
        }

        // CPU 和 内存百分比校验
        validatePct(rootNode.at("/process/cpu/pct"), "process.cpu.pct");
        validatePct(rootNode.at("/system/process/cpu/total/pct"), "system.process.cpu.total.pct");
        validatePct(rootNode.at("/system/process/cpu/total/norm/pct"), "system.process.cpu.total.norm.pct");
        validatePct(rootNode.at("/process/memory/pct"), "process.memory.pct");
        validatePct(rootNode.at("/system/process/memory/rss/pct"), "system.process.memory.rss.pct");

        // 网络性能百分比校验（可按需扩展字段）
        validatePct(rootNode.at("/network/receive_pct"), "network.receive_pct");
        validatePct(rootNode.at("/network/transmit_pct"), "network.transmit_pct");
        validatePct(rootNode.at("/network/drop_pct"), "network.drop_pct");
        validatePct(rootNode.at("/network/utilization_pct"), "network.utilization_pct"); // 网络利用率示例
    }

    private void validatePct(JsonNode node, String fieldName) {
        if (!node.isMissingNode() && node.isNumber()) {
            double pct = node.asDouble();
            if (pct < 0 || pct > 1) {
                throw new IllegalArgumentException(fieldName + " 百分比超出范围 (0~1): " + pct);
            }
        }
    }

    private boolean isValidIP(String ip) {
        // 正则表达式：IPv4地址
        String ipv4Pattern =
                "^(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}$";

        // 正则表达式：IPv6地址（支持压缩形式）
        String ipv6Pattern =
                "([0-9a-fA-F]{1,4}:){1,7}([0-9a-fA-F]{1,4}|:)|([0-9a-fA-F]{1,4}:){1,6}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,5}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,6}|([0-9a-fA-F]{1,4}:){1}(:[0-9a-fA-F]{1,4}){1,7}|(:([0-9a-fA-F]{1,4}:){0,7}[0-9a-fA-F]{1,4})$";

        // 如果匹配IPv4或IPv6地址
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }


    private boolean isValidMac(String mac) {
        String macPattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
        return mac.matches(macPattern);
    }
}

