package top.haidong556.metric.domain.model.machineMonitorAggregate.detector;

import lombok.Getter;
import top.haidong556.metric.domain.common.AnomalyResult;

import java.util.*;

public abstract class SlidingWindow<T extends Number> {
    protected long WINDOW_SIZE = 60; // 滑动窗口大小
    @Getter
    protected String windowKey;      // Redis 键

    public abstract long addEvent(T event);

    public abstract long getEventCount();

    public abstract List<T> getEvents();

    public abstract Double averageEvent();

    public abstract void clearWindow();

    public abstract long getWindowSize();

    /**
     * 核心异常检测
     * @param data 当前采样值
     */
    public AnomalyResult detectAnomaly(T data) {
        AnomalyResult anomalyResult = new AnomalyResult(windowKey);
        anomalyResult.setHasAnomaly(false);

        // 直接添加新数据
        addEvent(data);

        // 获取历史事件
        List<T> historicalEvents = getEvents();

        // 执行异常检测
        String indicator = this.windowKey;

        // 检测尖刺
        if (detectSpike(historicalEvents)) {
            anomalyResult.setHasAnomaly(true);
            anomalyResult.addAnomalyDetail("Spike detected on indicator: " + indicator);
        }

        // 检测离群值
        if (detectOutliers(historicalEvents)) {
            anomalyResult.setHasAnomaly(true);
            anomalyResult.addAnomalyDetail("Outlier detected on indicator: " + indicator);
        }

        // 检测趋势变化
        if (detectTrendShift(historicalEvents)) {
            anomalyResult.setHasAnomaly(true);
            anomalyResult.addAnomalyDetail("Trend shift detected on indicator: " + indicator);
        }

        return anomalyResult;
    }

    /**
     * Z-Score 尖刺检测
     */
    protected boolean detectSpike(List<T> events) {
        double mean = calculateMean(events);
        double stdDev = calculateStandardDeviation(events, mean);
        if (stdDev == 0) return false; // 避免除以0

        for (T event : events) {
            double zScore = Math.abs((event.doubleValue() - mean) / stdDev);
            if (zScore > 3) {
                return true;
            }
        }
        return false;
    }

    /**
     * IQR 离群值检测
     */
    protected boolean detectOutliers(List<T> events) {
        List<T> sortedEvents = new ArrayList<>(events);
        sortedEvents.sort(Comparator.comparingDouble(Number::doubleValue));

        int size = sortedEvents.size();
        if (size < 4) return false;

        double q1 = sortedEvents.get(size / 4).doubleValue();
        double q3 = sortedEvents.get(size * 3 / 4).doubleValue();
        double iqr = q3 - q1;

        double lowerLimit = q1 - 1.5 * iqr;
        double upperLimit = q3 + 1.5 * iqr;

        for (T event : events) {
            double value = event.doubleValue();
            if (value < lowerLimit || value > upperLimit) {
                return true;
            }
        }
        return false;
    }

    /**
     * 趋势变化检测
     */
    protected boolean detectTrendShift(List<T> events) {
        double movingAverage = calculateMean(events);
        for (T event : events) {
            if (Math.abs(event.doubleValue() - movingAverage) > movingAverage * 0.2) {
                return true;
            }
        }
        return false;
    }

    protected double calculateMean(List<T> events) {
        return events.stream().mapToDouble(Number::doubleValue).average().orElse(0.0);
    }

    protected double calculateStandardDeviation(List<T> events, double mean) {
        double variance = events.stream()
                .mapToDouble(event -> Math.pow(event.doubleValue() - mean, 2))
                .average().orElse(0.0);
        return Math.sqrt(variance);
    }
}
