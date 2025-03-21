package top.haidong556.metric.domain.model.machineMonitorAggregate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.haidong556.metric.MetricApplication;
import top.haidong556.metric.domain.common.EWMAAnomalyDetectorFactory;
import top.haidong556.metric.domain.common.SlidingWindowFactory;
import top.haidong556.metric.domain.model.machineMonitorAggregate.detector.EWMAAnomalyDetector;
import top.haidong556.metric.domain.model.machineMonitorAggregate.detector.SlidingWindow;
import top.haidong556.metric.domain.service.anomalyDetectionService.AnomalyDetectionServiceImpl;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(classes = MetricApplication.class)
class MachineMonitorAggregateRootFactoryTest {

    @Autowired
    private SlidingWindowFactory slidingWindowFactory;

    @Autowired
    private EWMAAnomalyDetectorFactory ewmaAnomalyDetectorFactory;

    @Test
    void createMachineMonitorAggregateRootTest() {
        MonitorConfig monitorConfig = MonitorConfig.builder()
                .addMetric(MonitorConfig.MetricIndex.CPU, 50, 0.3, 2.0)
                .addMetric(MonitorConfig.MetricIndex.MEMORY_RSS, 50, 0.3, 2.0)
                .addMetric(MonitorConfig.MetricIndex.THREAD_NUM, 50, 0.3, 2.0)
                .addMetric(MonitorConfig.MetricIndex.FD_OPEN, 50, 0.3, 2.0)
                .addMetric(MonitorConfig.MetricIndex.IO_READ_BYTES, 50, 0.3, 2.0)
                .addMetric(MonitorConfig.MetricIndex.IO_WRITE_BYTES, 50, 0.3, 2.0)
                .addMetric(MonitorConfig.MetricIndex.NETWORK_IN_BYTES, 50, 0.3, 2.0)
                .addMetric(MonitorConfig.MetricIndex.NETWORK_OUT_BYTES, 50, 0.3, 2.0)
                .addMetric(MonitorConfig.MetricIndex.NETWORK_IN_ERRORS, 50, 0.3, 2.0)
                .addMetric(MonitorConfig.MetricIndex.NETWORK_OUT_ERRORS, 50, 0.3, 2.0)
                .addMetric(MonitorConfig.MetricIndex.NETWORK_IN_DROPPED, 50, 0.3, 2.0)
                .addMetric(MonitorConfig.MetricIndex.NETWORK_OUT_DROPPED, 50, 0.3, 2.0)
                .build();

        MachineIdentification machineId = new MachineIdentification("test-machine-001");

        MachineMonitorAggregateRoot monitorAggregateRoot = MachineMonitorAggregateRootFactory.createMachineMonitorAggregateRoot(
                machineId,
                slidingWindowFactory,
                ewmaAnomalyDetectorFactory,
                monitorConfig
        );

        // 1️⃣ 校验聚合根不为 null
        Assertions.assertNotNull(monitorAggregateRoot, "MachineMonitorAggregateRoot should not be null");

        // 2️⃣ 校验 machineId 正确
        Assertions.assertEquals(machineId, monitorAggregateRoot.getMachineId(), "MachineId should match");

        // 3️⃣ 校验内部 slidingWindowMap 是否全量生成
        Map<MonitorConfig.MetricIndex, SlidingWindow> slidingWindowMap = monitorAggregateRoot.getSlidingWindowMap();
        Assertions.assertEquals(monitorConfig.getAllMonitoredMetrics().size(), slidingWindowMap.size(),
                "SlidingWindowMap size should match configured metrics");

        // 4️⃣ 校验 EWMA 检测器 Map 是否正确生成
        Map<MonitorConfig.MetricIndex, EWMAAnomalyDetector> ewmaAnomalyDetectorMap = monitorAggregateRoot.getEwmaAnomalyDetectorMap();
        Assertions.assertEquals(monitorConfig.getAllMonitoredMetrics().size(), ewmaAnomalyDetectorMap.size(),
                "EWMAAnomalyDetectorMap size should match configured metrics");

        // 5️⃣ 随机 spot check 某个metric的 SlidingWindow和EWMA是否存在
        Assertions.assertTrue(slidingWindowMap.containsKey(MonitorConfig.MetricIndex.CPU), "SlidingWindow for CPU should exist");
        Assertions.assertTrue(ewmaAnomalyDetectorMap.containsKey(MonitorConfig.MetricIndex.CPU), "EWMA for CPU should exist");

        System.out.println("✅ MonitorAggregateRoot 创建成功，包含指标数: " + slidingWindowMap.size());
    }
}
