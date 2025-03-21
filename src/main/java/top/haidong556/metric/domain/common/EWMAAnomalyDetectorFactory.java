package top.haidong556.metric.domain.common;

import top.haidong556.metric.domain.model.machineMonitorAggregate.detector.EWMAAnomalyDetector;

public interface EWMAAnomalyDetectorFactory {
    public EWMAAnomalyDetector<Double> createDoubleEWMADetector(String ewmaKey, double alpha, double k);
    public EWMAAnomalyDetector<Long> createLongEWMADetector(String ewmaKey, double alpha, double k);
}
