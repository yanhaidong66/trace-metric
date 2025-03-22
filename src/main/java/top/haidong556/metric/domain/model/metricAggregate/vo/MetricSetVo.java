package top.haidong556.metric.domain.model.metricAggregate.vo;

import lombok.*;

@Getter

@AllArgsConstructor
@NoArgsConstructor
public class MetricSetVo {
    private String name;
    private long period;
    @Override
    public String toString() {
        return "{\n" +
                "        \"name\": \"" + name + "\",\n" +
                "        \"period\": " + period + "\n" +
                "    }";
    }
}
