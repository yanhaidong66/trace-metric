package top.haidong556.metric.domain.model.metricAggregate.vo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ServiceVo {
    String type;
    public ServiceVo() {}
    public ServiceVo(String type) {
        this.type = type;
    }
    @Override
    public String toString() {
        return "{\n" +
                "        \"type\": \"" + type + "\"\n" +
                "    }";
    }
}
