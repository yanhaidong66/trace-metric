package top.haidong556.metric.domain.common;

import top.haidong556.metric.domain.model.machineMonitorAggregate.detector.SlidingWindow;

public interface SlidingWindowFactory {
    public  SlidingWindow<Double> createDoubleSlidingWindow(int windowSize,String windowKey);
    public  SlidingWindow<Long> createLongSlidingWindow(int windowSize,String windowKey);

}
