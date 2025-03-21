package top.haidong556.metric.domain.model.machineMonitorAggregate;

import lombok.Getter;
import top.haidong556.metric.domain.model.machineMonitorAggregate.MonitorConfig.MetricIndex;
import top.haidong556.metric.domain.model.machineMonitorAggregate.detector.EWMAAnomalyDetector;
import top.haidong556.metric.domain.model.machineMonitorAggregate.detector.SlidingWindow;

import java.util.HashMap;
import java.util.Map;
@Getter
public class MachineMonitorAggregateRoot {
    MachineIdentification machineId;
    Map<MetricIndex, SlidingWindow> slidingWindowMap = new HashMap<>();
    Map<MetricIndex, EWMAAnomalyDetector> ewmaAnomalyDetectorMap = new HashMap<>();
}