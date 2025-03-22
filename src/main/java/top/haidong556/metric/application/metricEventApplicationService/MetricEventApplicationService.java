package top.haidong556.metric.application.metricEventApplicationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.haidong556.metric.domain.common.AnomalyResult;
import top.haidong556.metric.application.machineMonitoringService.MachineMonitoringService;
import top.haidong556.metric.application.metricEventApplicationService.filter.*;
import top.haidong556.metric.application.common.filterChainTemplate.FilterChain;
import top.haidong556.metric.domain.model.metricAggregate.MetricAggregateRoot;
import top.haidong556.metric.domain.model.metricAggregate.MetricRepo;


@Service
public class MetricEventApplicationService {

    private FilterChain<String> preCreationFilterChain = new FilterChain<String>();
    private MachineMonitoringService machineMonitoringService;
    private FilterChain<MetricAggregateRoot> postCreationFilterChain = new FilterChain<MetricAggregateRoot>();

    @Autowired
    public MetricEventApplicationService(MachineMonitoringService machineMonitoringService, MetricRepo metricRepo) {
        this.machineMonitoringService = machineMonitoringService;
        preCreationFilterChain
                .addFilter(new FormatValidationFilter())
                .addFilter(new FieldValidationFilter())
                .addFilter(new DataFormattingFilter());
        postCreationFilterChain
                .addFilter(new DeduplicationFilter(metricRepo))
                .addFilter(new DataConsistencyFilter())
                .addFilter(new PersistenceFilter(metricRepo));
    }


    public void processMetricEvent(String metricJson) throws Exception {
        preCreationFilterChain.executeFilters(metricJson);
        MetricAggregateRoot metricAggregateRoot = MetricAggregateRoot.Builder.buildByJson(metricJson);
        postCreationFilterChain.executeFilters(metricAggregateRoot);
        AnomalyResult anomalyResult = machineMonitoringService.monitorMachine(metricAggregateRoot);
    }


}
