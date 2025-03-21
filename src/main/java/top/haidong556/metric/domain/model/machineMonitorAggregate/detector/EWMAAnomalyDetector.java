package top.haidong556.metric.domain.model.machineMonitorAggregate.detector;

import top.haidong556.metric.domain.common.AnomalyResult;

public abstract class EWMAAnomalyDetector<T extends Number>{
    // 用于区分不同机器的状态数据
    protected String ewmaKey;

    // EWMA 平滑系数 (α)，决定数据的平滑程度
    protected  double alpha;

    // 阈值系数 (k)，决定异常值的敏感度
    protected  double k;
    /**
     * 核心异常检测
     * @param data 当前采样值
     */
    public abstract AnomalyResult detectAnomaly(T data);
}
