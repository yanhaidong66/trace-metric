package top.haidong556.metric.domain.service.anomalyDetectionService;

import top.haidong556.metric.domain.common.AnomalyResult;
import top.haidong556.metric.domain.model.machineMonitorAggregate.MachineIdentification;
import top.haidong556.metric.domain.model.metricAggregate.MetricAggregateRoot;

//异常检测
public interface AnomalyDetectionService{
    AnomalyResult detectAnomaly(MetricAggregateRoot metricAggregateRoot);
}
