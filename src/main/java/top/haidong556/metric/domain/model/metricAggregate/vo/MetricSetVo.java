package top.haidong556.metric.domain.model.metricAggregate.vo;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class MetricSetVo {
    private final String name;
    private final long period;
    @Override
    public String toString() {
        return "{\n" +
                "        \"name\": \"" + name + "\",\n" +
                "        \"period\": " + period + "\n" +
                "    }";
    }
}
