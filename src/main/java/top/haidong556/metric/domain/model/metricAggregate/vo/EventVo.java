package top.haidong556.metric.domain.model.metricAggregate.vo;

import lombok.*;

@Getter

@AllArgsConstructor
@NoArgsConstructor
public class EventVo {
    private long duration;
    private String dataset;
    private String module;

    @Override
    public String toString() {
        return "{\n" +
                "        \"duration\": " + duration + ",\n" +
                "        \"dataset\": \"" + dataset + "\",\n" +
                "        \"module\": \"" + module + "\"\n" +
                "    }";
    }
}

