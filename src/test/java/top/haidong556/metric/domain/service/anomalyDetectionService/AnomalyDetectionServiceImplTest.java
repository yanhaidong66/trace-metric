package top.haidong556.metric.domain.service.anomalyDetectionService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.haidong556.metric.MetricApplication;
import top.haidong556.metric.domain.common.AnomalyResult;
import top.haidong556.metric.domain.common.EWMAAnomalyDetectorFactory;
import top.haidong556.metric.domain.common.SlidingWindowFactory;
import top.haidong556.metric.domain.model.machineMonitorAggregate.MonitorConfig;
import top.haidong556.metric.domain.model.metricAggregate.MetricAggregateRoot;

@SpringBootTest(classes = MetricApplication.class)
class AnomalyDetectionServiceImplTest {
    private final String json = """
            {
                "@timestamp": "2025-03-13T16:46:29.861Z",
                "metadata": {
                    "beat": "metricbeat",
                    "type": "_doc",
                    "version": "8.17.3"
                },
                "process": {
                    "command_line": "/usr/libexec/polkitd --no-debug",
                    "executable": "/usr/libexec/polkitd",
                    "name": "polkitd",
                    "pid": 4793,
                    "state": "sleeping",
                    "parent": {
                        "pid": 1
                    },
                    "working_directory": "/",
                    "args": ["/usr/libexec/polkitd", "--no-debug"],
                    "pgid": 4793,
                    "cpu": {
                        "start_time": "2025-03-13T16:45:22.000Z",
                        "pct": 0
                    },
                    "memory": {
                        "pct": 0.0007
                    }
                },
                "user": {
                    "name": "root"
                },
                "service": {
                    "type": "system"
                },
                "system": {
                    "process": {
                        "num_threads": 3,
                        "cmdline": "/usr/libexec/polkitd --no-debug",
                        "memory": {
                            "size": 241164288,
                            "share": 7180288,
                            "rss": {
                                "bytes": 8503296,
                                "pct": 0.0007
                            }
                        },
                        "cpu": {
                            "start_time": "2025-03-13T16:45:22.000Z",
                            "total": {
                                "norm": {
                                    "pct": 0
                                },
                                "value": 10,
                                "pct": 0
                            }
                        },
                        "fd": {
                            "open": 11,
                            "limit": {
                                "hard": 1048576,
                                "soft": 1024
                            }
                        }
                    },
                    "state": "sleeping"
                },
                "event": {
                    "module": "system",
                    "duration": 50252243,
                    "dataset": "system.process"
                },
                "metricset": {
                    "name": "process",
                    "period": 10000
                },
                "host": {
                    "name": "lxyten",
                    "hostname": "Lxyten",
                    "architecture": "x86_64",
                    "os": {
                        "family": "debian",
                        "name": "Ubuntu",
                        "kernel": "5.15.153.1-microsoft-standard-WSL2",
                        "codename": "jammy",
                        "type": "linux",
                        "platform": "ubuntu",
                        "version": "22.04.5 LTS (Jammy Jellyfish)"
                    },
                    "id": "ddd8185a42e34d71a32110784f8a2913",
                    "containerized": false,
                    "ip": [
                        "198.18.0.1",
                        "2.0.0.1",
                        "fe80::93cd:8203:9e4d:68d3",
                        "10.129.87.223",
                        "2001:da8:215:3c0a:a9f1:a00:e677:4413"
                    ],
                    "mac": [
                        "00-15-5D-07-1E-13",
                        "00-15-5D-A0-69-E7",
                        "00-15-5D-BF-33-15"
                    ]
                },
                "agent": {
                    "version": "8.17.3",
                    "ephemeral_id": "32936e4b-c241-4369-82e2-794a7847c367",
                    "id": "cab8659a-5c83-46c6-a73f-4af6cbfc88c9",
                    "name": "Lxyten",
                    "type": "metricbeat"
                },
                "ecs": {
                    "version": "8.0.0"
                }
            }
            """;

    @Autowired
    private EWMAAnomalyDetectorFactory ewmaAnomalyDetectorFactory;

    @Autowired
    private SlidingWindowFactory slidingWindowFactory;

    private AnomalyDetectionServiceImpl anomalyDetectionService;

    @BeforeEach
    void setUp() {
        MonitorConfig monitorConfig = MonitorConfig.builder()
                .addMetric(MonitorConfig.MetricIndex.CPU, 50, 0.3, 2.0)
                .addMetric(MonitorConfig.MetricIndex.MEMORY_RSS, 50, 0.3, 2.0)
                .addMetric(MonitorConfig.MetricIndex.THREAD_NUM, 50, 0.3, 2.0)
                .addMetric(MonitorConfig.MetricIndex.FD_OPEN, 50, 0.3, 2.0)
                .build();

        anomalyDetectionService = AnomalyDetectionServiceImpl.getInstance(
                slidingWindowFactory,
                ewmaAnomalyDetectorFactory,
                monitorConfig
        );
    }

    @Test
    void getInstanceTest() {
        Assertions.assertNotNull(anomalyDetectionService, "AnomalyDetectionServiceImpl instance should not be null");
    }

    @Test
    void detectAnomalyTest() throws Exception {
        // 构造模拟的 MetricAggregateRoot
        MetricAggregateRoot metricAggregateRoot = MetricAggregateRoot.Builder.buildByJson(json);

        // 执行异常检测
        AnomalyResult result = anomalyDetectionService.detectAnomaly(metricAggregateRoot);

        // 校验结果不为空
        Assertions.assertNotNull(result, "AnomalyResult should not be null");
        System.out.println("异常检测结果: " + result);
    }

}
