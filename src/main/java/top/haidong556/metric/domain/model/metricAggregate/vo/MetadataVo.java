package top.haidong556.metric.domain.model.metricAggregate.vo;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class MetadataVo {
    private final String beat;
    private final String type;
    private final String version;

    @Override
    public String toString() {
        return "{\n" +
                "        \"beat\": \"" + beat + "\",\n" +
                "        \"type\": \"" + type + "\",\n" +
                "        \"version\": \"" + version + "\"\n" +
                "    }";
    }
}

