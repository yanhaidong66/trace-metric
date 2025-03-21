package top.haidong556.metric.domain.model.metricAggregate;

import top.haidong556.metric.domain.common.SnowflakeIdGenerator;
import top.haidong556.metric.domain.model.machineMonitorAggregate.MachineIdentification;
import top.haidong556.metric.domain.model.metricAggregate.entity.MetricAggregateId;

public class MetricAggregateFactory {
    // 单例实例，volatile 关键字防止指令重排
    private static volatile MetricAggregateFactory instance;

    // 私有构造方法，防止外部实例化
    private MetricAggregateFactory() {}

    // 获取单例实例（双重检查锁）
    public static MetricAggregateFactory getInstance() {
        if (instance == null) {
            synchronized (MetricAggregateFactory.class) {
                if (instance == null) {
                    instance = new MetricAggregateFactory();
                }
            }
        }
        return instance;
    }

    public MetricAggregateRoot createByJson(String json) throws Exception {
        MetricAggregateRoot root =MetricAggregateRoot.Builder.build(json);
        root.setMetricAggregateId(new MetricAggregateId(generateMetricAggregateId(root)));
        root.setMachineIdentification(new MachineIdentification(generateMachineIdentification(root)));
        return root;
    }

    public String generateMetricAggregateId(MetricAggregateRoot root) {
        return SnowflakeIdGenerator.generateMetricAggregateId(root);
    }

    public String generateMachineIdentification(MetricAggregateRoot root) {
        StringBuilder idBuilder = new StringBuilder();
        return root.getHostEntity().getMachineIdentification();
    }

}
