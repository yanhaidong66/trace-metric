package top.haidong556.metric.domain.model.metricAggregate.vo;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class EventVo {
    private final long duration;
    private final String dataset;
    private final String module;

    @Override
    public String toString() {
        return "{\n" +
                "        \"duration\": " + duration + ",\n" +
                "        \"dataset\": \"" + dataset + "\",\n" +
                "        \"module\": \"" + module + "\"\n" +
                "    }";
    }
}

