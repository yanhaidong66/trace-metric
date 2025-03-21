package top.haidong556.metric.application.metricEventApplicationService.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.haidong556.metric.application.common.filterChainTemplate.AbstractFilter;

/**
 * 格式验证过滤器（Format Validation Filter）。
 * <p>
 * 该过滤器用于确保输入数据的格式符合预期。例如：
 * <ul>
 *     <li>验证日期、时间、邮箱、电话号码等字段是否符合规定格式。</li>
 *     <li>确保字符串长度、数字范围等字段符合系统要求。</li>
 * </ul>
 * 该过滤器通常在数据进入系统的业务处理之前执行，以确保数据的合法性。
 * </p>
 *
 * @author [haidong]
 * @version 1.0
 */
public class FormatValidationFilter implements AbstractFilter<String> {
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void apply(String input) throws Exception {
        if(input == null || input.isEmpty() || !isValidJson(input)){
            throw new Exception();
        }
    }

    public boolean isValidJson(String jsonStr) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
