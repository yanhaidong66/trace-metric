package top.haidong556.metric.application.metricEventApplicationService.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.haidong556.metric.application.common.filterChainTemplate.AbstractFilter;
import top.haidong556.metric.domain.model.metricAggregate.MetricAggregateFactory;
import top.haidong556.metric.domain.model.metricAggregate.MetricAggregateRoot;
import top.haidong556.metric.domain.model.metricAggregate.MetricRepo;
public class PersistenceFilter implements AbstractFilter<MetricAggregateRoot> {

    private MetricRepo metricRepo;

    public PersistenceFilter(MetricRepo metricRepo) {
        this.metricRepo = metricRepo;
    }

    @Override
    public int getOrder() {
        return 99; // 最后执行
    }

    @Override
    public void apply(MetricAggregateRoot metric) throws Exception {
        // 持久化
        metricRepo.saveIfNotFound(metric);
    }
}
