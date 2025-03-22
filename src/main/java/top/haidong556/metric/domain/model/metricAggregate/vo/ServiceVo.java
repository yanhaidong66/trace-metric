package top.haidong556.metric.domain.model.metricAggregate.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceVo {
    String type;
    @Override
    public String toString() {
        return "{\n" +
                "        \"type\": \"" + type + "\"\n" +
                "    }";
    }
}
