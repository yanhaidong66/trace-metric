package top.haidong556.metric.domain.model.metricAggregate.entity;

import lombok.Getter;

@Getter
public class MetricAggregateId {
    private final String metricAggregateId;
    public MetricAggregateId(String metricAggregateId) {
        this.metricAggregateId = metricAggregateId;
    }
}
