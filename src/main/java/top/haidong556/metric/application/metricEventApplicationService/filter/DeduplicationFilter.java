package top.haidong556.metric.application.metricEventApplicationService.filter;

import top.haidong556.metric.application.common.filterChainTemplate.AbstractFilter;
import top.haidong556.metric.domain.model.metricAggregate.MetricAggregateRoot;
import top.haidong556.metric.domain.model.metricAggregate.MetricRepo;

/**
 * 数据去重过滤器（Deduplication Filter）。
 * <p>
 * 该过滤器用于检查输入数据是否重复，并去除重复项。例如：
 * <ul>
 *     <li>对列表或集合中的数据进行去重。</li>
 *     <li>确保处理的业务数据唯一，避免冗余或重复处理。</li>
 * </ul>
 * 持久化之前执行，以保证数据的唯一性。
 * </p>
 *
 * @author [haidong]
 * @version 1.0
 */
public class DeduplicationFilter implements AbstractFilter<MetricAggregateRoot> {
    MetricRepo metricRepo;
    public DeduplicationFilter(MetricRepo metricRepo) {
        this.metricRepo = metricRepo;
    }

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public void apply(MetricAggregateRoot input) throws Exception {
        if(metricRepo.findById(input.getMetricAggregateId().getMetricAggregateRootId())!=null){
            throw new Exception("metricAggregateId is exist");
        }
    }


}
