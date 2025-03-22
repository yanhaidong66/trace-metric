package top.haidong556.metric.domain.model.metricAggregate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetricAggregateRootTest {
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

    @Test
    void testToString() throws Exception {
        System.out.println(MetricAggregateRoot.Builder.buildByJson(json));
    }

    @Test
    void testBuilder() throws Exception {
        // 构建对象
        MetricAggregateRoot metricAggregateRoot = MetricAggregateRoot.Builder.buildByJson(json);
        System.out.println(metricAggregateRoot);

        // 顶层字段校验
        assertNotNull(metricAggregateRoot);
        assertEquals("2025-03-13T16:46:29.861Z", metricAggregateRoot.getTimestamp());

        // metadata 校验
        assertNotNull(metricAggregateRoot.getMetadataVo());
        assertEquals("metricbeat", metricAggregateRoot.getMetadataVo().getBeat());
        assertEquals("8.17.3", metricAggregateRoot.getMetadataVo().getVersion());

        // process 校验
        assertNotNull(metricAggregateRoot.getProcessEntity());
        assertEquals(4793, metricAggregateRoot.getProcessEntity().getPid());
        assertEquals("polkitd", metricAggregateRoot.getProcessEntity().getName());
        assertEquals("/usr/libexec/polkitd", metricAggregateRoot.getProcessEntity().getExecutable());

        // nested parent 校验
        assertNotNull(metricAggregateRoot.getProcessEntity().getParent());
        assertEquals(1, metricAggregateRoot.getProcessEntity().getParent().getPid());

        // user 校验
        assertNotNull(metricAggregateRoot.getUserVo());
        assertEquals("root", metricAggregateRoot.getUserVo().getName());

        // host 校验
        assertNotNull(metricAggregateRoot.getHostEntity());
        assertEquals("lxyten", metricAggregateRoot.getHostEntity().getName());
        assertEquals("Ubuntu", metricAggregateRoot.getHostEntity().getOs().getName());

        // system.process.memory.rss.pct 校验
        assertNotNull(metricAggregateRoot.getSystemEntity());
        assertNotNull(metricAggregateRoot.getSystemEntity().getProcess());
        assertNotNull(metricAggregateRoot.getSystemEntity().getProcess().getMemory());
        assertNotNull(metricAggregateRoot.getSystemEntity().getProcess().getMemory().getRss());
        assertEquals(0.0007, metricAggregateRoot.getSystemEntity().getProcess().getMemory().getRss().getPct());

        // ip List 校验
        assertNotNull(metricAggregateRoot.getHostEntity().getIp());
        assertTrue(metricAggregateRoot.getHostEntity().getIp().contains("10.129.87.223"));

        // agent 校验
        assertNotNull(metricAggregateRoot.getAgentEntity());
        assertEquals("8.17.3", metricAggregateRoot.getAgentEntity().getVersion());
        assertEquals("metricbeat", metricAggregateRoot.getAgentEntity().getType());

        // ecs 校验
        assertNotNull(metricAggregateRoot.getElasticCommonSchemaVo());
        assertEquals("8.0.0", metricAggregateRoot.getElasticCommonSchemaVo().getVersion());

        // 打印 JSON 反射后的对象内容
        System.out.println(metricAggregateRoot);
    }
}