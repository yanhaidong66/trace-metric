package top.haidong556.metric.application.metricEventApplicationService.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.haidong556.metric.application.common.filterChainTemplate.AbstractFilter;

/**
 * 字段验证过滤器（Field Validation Filter）。
 * <p>
 * 该过滤器用于验证输入数据中的必要字段是否为空或缺失。例如：
 * <ul>
 *     <li>确保必填字段如名称、地址、电话号码等不为空。</li>
 *     <li>验证输入的字段是否符合业务要求，避免缺失关键数据。</li>
 * </ul>
 * 该过滤器通常在数据处理或持久化前执行，以确保输入数据的完整性。
 * </p>
 *
 * @author [haidong]
 * @version 1.0
 */
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 必填字段校验 Filter
 */
public class FieldValidationFilter implements AbstractFilter<String> {

    // 定义必填字段路径（支持简单路径）
    private static final List<String> REQUIRED_FIELDS = Arrays.asList(
            "@timestamp",
            "host.hostname",
            "host.ip",
            "agent.ephemeral_id",
            "agent.id"
    );

    @Override
    public int getOrder() {
        return 1; // 必填字段优先校验
    }

    @Override
    public void apply(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);

        for (String fieldPath : REQUIRED_FIELDS) {
            JsonNode node = getNodeByPath(rootNode, fieldPath);
            if (node == null || node.isNull() || (node.isTextual() && node.asText().isEmpty())) {
                throw new IllegalArgumentException("缺失必填字段或值为空: " + fieldPath);
            }
        }
    }

    /**
     * 支持 . 分隔的路径取值
     */
    private JsonNode getNodeByPath(JsonNode root, String path) {
        String[] parts = path.split("\\.");
        JsonNode currentNode = root;
        for (String part : parts) {
            if (currentNode == null) return null;
            currentNode = currentNode.get(part);
        }
        return currentNode;
    }
}

