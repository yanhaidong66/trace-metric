package top.haidong556.metric.domain.model.metricAggregate.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EcsVo {
    String version;
    @Override
    public String toString() {
        return "{\n" +
                "        \"version\": \"" + version + "\"\n" +
                "    }";
    }
}
