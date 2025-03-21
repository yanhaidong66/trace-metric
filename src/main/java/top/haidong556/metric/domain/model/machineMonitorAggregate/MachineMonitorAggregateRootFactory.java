package top.haidong556.metric.domain.model.machineMonitorAggregate;

import top.haidong556.metric.domain.common.EWMAAnomalyDetectorFactory;
import top.haidong556.metric.domain.common.SlidingWindowFactory;
import top.haidong556.metric.domain.model.machineMonitorAggregate.detector.EWMAAnomalyDetector;
import top.haidong556.metric.domain.model.machineMonitorAggregate.detector.SlidingWindow;

/**
 * MachineMonitorAggregateRootFactory 用于创建 MachineMonitorAggregateRoot 实例
 */
public class MachineMonitorAggregateRootFactory {

    /**
     * 创建一个 MachineMonitorAggregateRoot 实例
     *
     * @param machineId            机器唯一标识
     * @param slidingWindowFactory 滑动窗口工厂
     * @param config               监控配置（包含要监控的指标、滑动窗口大小、EWMA配置）
     * @return MachineMonitorAggregateRoot 实例
     */
    public static MachineMonitorAggregateRoot createMachineMonitorAggregateRoot(
            MachineIdentification machineId,
            SlidingWindowFactory slidingWindowFactory,
            EWMAAnomalyDetectorFactory ewmaAnomalyDetectorFactory,
            MonitorConfig config) {

        MachineMonitorAggregateRoot aggregateRoot = new MachineMonitorAggregateRoot();
        aggregateRoot.machineId = machineId;

        // 根据配置动态初始化滑动窗口和EWMA检测器
        config.getAllMonitoredMetrics().forEach((metricIndex, metricConfig) -> {
            String ewmaKey = machineId.getMachineIdentification() + ":" + metricIndex.getCode();

            if (metricIndex.getType() == MonitorConfig.MetricClassType.LONG) {
                // 创建滑动窗口
                SlidingWindow<Long> slidingWindow = slidingWindowFactory.createLongSlidingWindow(
                        metricConfig.windowSize, machineId.getMachineIdentification());
                aggregateRoot.slidingWindowMap.put(metricIndex, slidingWindow);

                // 创建EWMA检测器
                EWMAAnomalyDetector<Long> ewma = ewmaAnomalyDetectorFactory.createLongEWMADetector(
                        ewmaKey, metricConfig.alpha, metricConfig.k);
                aggregateRoot.ewmaAnomalyDetectorMap.put(metricIndex, ewma);

            } else if (metricIndex.getType() == MonitorConfig.MetricClassType.DOUBLE) {
                // 创建滑动窗口
                SlidingWindow<Double> slidingWindow = slidingWindowFactory.createDoubleSlidingWindow(
                        metricConfig.windowSize, machineId.getMachineIdentification());
                aggregateRoot.slidingWindowMap.put(metricIndex, slidingWindow);

                // 创建EWMA检测器
                EWMAAnomalyDetector<Double> ewma = ewmaAnomalyDetectorFactory.createDoubleEWMADetector(
                        ewmaKey, metricConfig.alpha, metricConfig.k);
                aggregateRoot.ewmaAnomalyDetectorMap.put(metricIndex, ewma);
            }
        });

        return aggregateRoot;
    }
}