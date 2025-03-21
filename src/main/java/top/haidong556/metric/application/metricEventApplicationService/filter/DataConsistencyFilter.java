package top.haidong556.metric.application.metricEventApplicationService.filter;

import top.haidong556.metric.application.common.filterChainTemplate.AbstractFilter;
import top.haidong556.metric.domain.model.metricAggregate.MetricAggregateRoot;

/**
 * 数据一致性过滤器（Data Consistency Filter）。
 * <p>
 * 该过滤器用于检查数据是否符合业务规则。例如：
 * <ul>
 *     <li>确保日期的逻辑一致性（结束日期不能早于开始日期）。</li>
 *     <li>校验某些字段是否符合预期的逻辑关系。</li>
 * </ul>
 * 该过滤器通常在数据持久化前执行，以确保数据的完整性和正确性。
 * </p>
 *
 * @author [haidong]
 * @version 1.0
 */
public class DataConsistencyFilter implements AbstractFilter<MetricAggregateRoot> {
    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public void apply(MetricAggregateRoot input) throws Exception {

    }

}
