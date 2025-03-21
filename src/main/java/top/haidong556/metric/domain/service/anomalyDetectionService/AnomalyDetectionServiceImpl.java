package top.haidong556.metric.domain.service.anomalyDetectionService;

import top.haidong556.metric.domain.common.AnomalyResult;
import top.haidong556.metric.domain.common.EWMAAnomalyDetectorFactory;
import top.haidong556.metric.domain.common.SlidingWindowFactory;
import top.haidong556.metric.domain.model.machineMonitorAggregate.MachineIdentification;
import top.haidong556.metric.domain.model.machineMonitorAggregate.MachineMonitorAggregateRoot;
import top.haidong556.metric.domain.model.machineMonitorAggregate.MachineMonitorAggregateRootFactory;
import top.haidong556.metric.domain.model.machineMonitorAggregate.MonitorConfig;
import top.haidong556.metric.domain.model.machineMonitorAggregate.detector.EWMAAnomalyDetector;
import top.haidong556.metric.domain.model.machineMonitorAggregate.detector.SlidingWindow;
import top.haidong556.metric.domain.model.metricAggregate.MetricAggregateRoot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnomalyDetectionServiceImpl implements AnomalyDetectionService {
    private static AnomalyDetectionServiceImpl instance;
    private final Map<MachineIdentification, MachineMonitorAggregateRoot> machineMonitorAggregateRootMap;
    private final SlidingWindowFactory slidingWindowFactory;
    private final EWMAAnomalyDetectorFactory ewmaAnomalyDetectorFactory;
    private final MonitorConfig config;

    // 私有构造函数
    private AnomalyDetectionServiceImpl(SlidingWindowFactory slidingWindowFactory, EWMAAnomalyDetectorFactory ewmaAnomalyDetectorFactory, MonitorConfig config) {
        this.machineMonitorAggregateRootMap = new HashMap<>();
        this.slidingWindowFactory = slidingWindowFactory;
        this.config = config;
        this.ewmaAnomalyDetectorFactory = ewmaAnomalyDetectorFactory;
    }

    // 懒汉式单例获取方法，线程安全
    public static AnomalyDetectionServiceImpl getInstance(SlidingWindowFactory slidingWindowFactory, EWMAAnomalyDetectorFactory ewmaAnomalyDetectorFactory, MonitorConfig config) {
        if (instance == null) {
            synchronized (AnomalyDetectionServiceImpl.class) {
                if (instance == null) {
                    instance = new AnomalyDetectionServiceImpl(slidingWindowFactory, ewmaAnomalyDetectorFactory, config);
                }
            }
        }
        return instance;
    }

    @Override
    public AnomalyResult detectAnomaly(MetricAggregateRoot metricAggregateRoot) {
        MachineIdentification machineId = metricAggregateRoot.getMachineIdentification();

        // 获取或初始化 MachineMonitorAggregateRoot
        MachineMonitorAggregateRoot monitorAggregateRoot = machineMonitorAggregateRootMap.computeIfAbsent(
                machineId,
                id -> MachineMonitorAggregateRootFactory.createMachineMonitorAggregateRoot(id, slidingWindowFactory, ewmaAnomalyDetectorFactory, config)
        );

        AnomalyResult result = new AnomalyResult(machineId.getMachineIdentification());

        // 滑动窗口检测
        monitorAggregateRoot.getSlidingWindowMap().forEach((metricIndex, slidingWindow) -> {
            Number value = metricIndex.extract(metricAggregateRoot);
            if (value != null) {
                if (slidingWindow.detectAnomaly(value).isHasAnomaly()) {
                    result.setHasAnomaly(true);
                    result.addAnomalyDetail("SlidingWindow anomaly detected for metric: " + metricIndex.getCode() + ", value: " + value);
                }
            }
        });

        // EWMA 检测
        monitorAggregateRoot.getEwmaAnomalyDetectorMap().forEach((metricIndex, ewmaDetector) -> {
            Number value = metricIndex.extract(metricAggregateRoot);
            if (value != null) {
                if (ewmaDetector.detectAnomaly(value).isHasAnomaly()) {
                    result.setHasAnomaly(true);
                    result.addAnomalyDetail("EWMA anomaly detected for metric: " + metricIndex.getCode() + ", value: " + value);
                }
            }
        });

        return result;
    }
}

