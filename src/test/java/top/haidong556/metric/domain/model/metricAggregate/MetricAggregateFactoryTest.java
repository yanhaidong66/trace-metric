package top.haidong556.metric.domain.model.metricAggregate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import top.haidong556.metric.domain.model.metricAggregate.entity.HostEntity;

import java.util.List;

class MetricAggregateFactoryTest {
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


    private MetricAggregateFactory factory ;
    private MetricAggregateRoot root ;
    @BeforeEach
    void setUp() throws Exception {
        factory = MetricAggregateFactory.getInstance();
        root = factory.createByJson(json);;
    }
    @Test
    void getInstance() {
        MetricAggregateFactory instance1 = MetricAggregateFactory.getInstance();
        MetricAggregateFactory instance2 = MetricAggregateFactory.getInstance();
        Assertions.assertNotNull(instance1);
        Assertions.assertSame(instance1, instance2, "Factory instance should be singleton");
    }

    @Test
    void createByJson() throws Exception {


        // 基本校验
        Assertions.assertNotNull(root);
        Assertions.assertNotNull(root.getMetricAggregateId());
        Assertions.assertNotNull(root.getMachineIdentification());

        // 校验时间戳，假设解析时映射 @timestamp -> root.timestamp
        Assertions.assertEquals("2025-03-13T16:46:29.861Z", root.getTimestamp());

        // 校验 HostEntity
        Assertions.assertNotNull(root.getHostEntity());
        Assertions.assertEquals("Lxyten", root.getHostEntity().getHostname());  // host.hostname
        Assertions.assertEquals("lxyten", root.getHostEntity().getName());      // host.name
        Assertions.assertEquals("x86_64", root.getHostEntity().getArchitecture());

        // 校验 IP 是否包含特定地址
        Assertions.assertTrue(root.getHostEntity().getIp().contains("198.18.0.1"));
        Assertions.assertTrue(root.getHostEntity().getIp().contains("10.129.87.223"));

        // 校验 MAC 地址是否包含某个值
        Assertions.assertTrue(root.getHostEntity().getMac().contains("00-15-5D-07-1E-13"));

        // 校验 host ID
        Assertions.assertEquals("ddd8185a42e34d71a32110784f8a2913", root.getHostEntity().getId());

        // 校验是否 containerized
        Assertions.assertFalse(root.getHostEntity().isContainerized());

        // 校验系统状态（从 system.state 解析）
        Assertions.assertNotNull(root.getSystemEntity());
        Assertions.assertEquals("sleeping", root.getSystemEntity().getState());

        // 打印结果
        System.out.println("MetricAggregateId: " + root.getMetricAggregateId().getMetricAggregateId());
        System.out.println("MachineIdentification: " + root.getMachineIdentification().getMachineIdentification());
    }

    @Test
    void generateMetricAggregateId() throws Exception {
        MetricAggregateFactory factory = MetricAggregateFactory.getInstance();
        String aggregateId = factory.generateMetricAggregateId(factory.createByJson(json));
        Assertions.assertNotNull(aggregateId);
        System.out.println("Generated MetricAggregateId: " + aggregateId);
    }

    @Test
    void generateMachineIdentification() {
        // 生成机器标识符
        String machineId = factory.generateMachineIdentification(root);

        // 校验生成的机器标识符
        Assertions.assertNotNull(machineId);
        Assertions.assertTrue(machineId.contains("ddd8185a42e34d71a32110784f8a2913"));
        Assertions.assertTrue(machineId.contains("lxyten"));  // 注意，这里是 "lxyten" 而不是 "test-host"
        Assertions.assertTrue(machineId.contains("198.18.0.1"));  // 假设第一个 IP 地址是 "198.18.0.1"

        // 打印生成的机器标识符
        System.out.println("Generated MachineIdentification: " + machineId);
    }
}
