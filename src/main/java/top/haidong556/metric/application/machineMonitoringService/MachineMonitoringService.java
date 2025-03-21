package top.haidong556.metric.application.machineMonitoringService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.haidong556.metric.domain.common.AnomalyResult;
import top.haidong556.metric.domain.common.EWMAAnomalyDetectorFactory;
import top.haidong556.metric.domain.common.SlidingWindowFactory;
import top.haidong556.metric.domain.model.machineMonitorAggregate.MonitorConfig;
import top.haidong556.metric.domain.model.metricAggregate.MetricAggregateRoot;
import top.haidong556.metric.domain.service.anomalyDetectionService.AnomalyDetectionService;
import top.haidong556.metric.domain.service.anomalyDetectionService.AnomalyDetectionServiceImpl;

@Service
public class MachineMonitoringService {

    private AnomalyDetectionService anomalyDetectionService;

    @Autowired
    public MachineMonitoringService(EWMAAnomalyDetectorFactory ewmaAnomalyDetectorFactory, SlidingWindowFactory slidingWindowFactory) {
        MonitorConfig monitorConfig = MonitorConfig.builder()
                .addMetric(MonitorConfig.MetricIndex.CPU, 30, 2, 2)
                .addMetric(MonitorConfig.MetricIndex.MEMORY_RSS,30, 2, 2)
//                .addMetric(MonitorConfig.MetricIndex.THREAD_NUM, /* windowSize */, /* alpha */, /* k */)
//                .addMetric(MonitorConfig.MetricIndex.FD_OPEN, /* windowSize */, /* alpha */, /* k */)
//                .addMetric(MonitorConfig.MetricIndex.IO_READ_BYTES, /* windowSize */, /* alpha */, /* k */)
//                .addMetric(MonitorConfig.MetricIndex.IO_WRITE_BYTES, /* windowSize */, /* alpha */, /* k */)
//                .addMetric(MonitorConfig.MetricIndex.NETWORK_IN_BYTES, /* windowSize */, /* alpha */, /* k */)
//                .addMetric(MonitorConfig.MetricIndex.NETWORK_OUT_BYTES, /* windowSize */, /* alpha */, /* k */)
//                .addMetric(MonitorConfig.MetricIndex.NETWORK_IN_ERRORS, /* windowSize */, /* alpha */, /* k */)
//                .addMetric(MonitorConfig.MetricIndex.NETWORK_OUT_ERRORS, /* windowSize */, /* alpha */, /* k */)
//                .addMetric(MonitorConfig.MetricIndex.NETWORK_IN_DROPPED, /* windowSize */, /* alpha */, /* k */)
//                .addMetric(MonitorConfig.MetricIndex.NETWORK_OUT_DROPPED, /* windowSize */, /* alpha */, /* k */)
                .build();

        anomalyDetectionService = AnomalyDetectionServiceImpl.getInstance(slidingWindowFactory, ewmaAnomalyDetectorFactory, monitorConfig);
    }

    // 运行所有检测方法
    public AnomalyResult monitorMachine(MetricAggregateRoot metricValues) {
        return anomalyDetectionService.detectAnomaly(metricValues);
    }


}


