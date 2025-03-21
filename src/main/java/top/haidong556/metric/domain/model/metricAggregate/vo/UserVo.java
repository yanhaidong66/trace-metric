package top.haidong556.metric.domain.model.metricAggregate.vo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserVo {
    String name;
    @Override
    public String toString() {
        return "{\n" +
                "    \"name\": \"" + (name != null ? name : "") + "\"\n" +
                "}";
    }
}
