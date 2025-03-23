package top.haidong556.metric.domain.model.metricAggregate.vo;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MetadataVo {
    private String beat;
    private String type;
    private String version;

    @Override
    public String toString() {
        return "{\n" +
                "        \"beat\": \"" + beat + "\",\n" +
                "        \"type\": \"" + type + "\",\n" +
                "        \"version\": \"" + version + "\"\n" +
                "    }";
    }
}

