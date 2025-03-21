package top.haidong556.metric.domain.model.metricAggregate;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.haidong556.metric.domain.model.metricAggregate.entity.AgentEntity;
import top.haidong556.metric.domain.model.metricAggregate.entity.HostEntity;
import top.haidong556.metric.domain.model.metricAggregate.entity.ProcessEntity;
import top.haidong556.metric.domain.model.metricAggregate.entity.SystemEntity;
import top.haidong556.metric.domain.model.metricAggregate.vo.*;

import static org.junit.jupiter.api.Assertions.*;

class MetricIndexAggregateRootTest {
    private MetricAggregateRoot metricAggregateRoot;
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

    @BeforeEach
    void setUp() throws JsonProcessingException {
        metricAggregateRoot = MetricAggregateRoot.Builder.build(json);
    }


    @Test
    void getMetricAggregateId() {
        assertNull(metricAggregateRoot.getMetricAggregateId());
    }

    @Test
    void getMetricJson() {
        assertEquals(json, metricAggregateRoot.getMetricJson());
    }

    @Test
    void getTimestamp() {
        assertEquals("2025-03-13T16:46:29.861Z", metricAggregateRoot.getTimestamp());
    }

    @Test
    void getMetadataVo() {
        MetadataVo metadata = metricAggregateRoot.getMetadataVo();
        assertNotNull(metadata);
        assertEquals("metricbeat", metadata.getBeat());
        assertEquals("_doc", metadata.getType());
        assertEquals("8.17.3", metadata.getVersion());
    }

    @Test
    void getServiceVo() {
        ServiceVo service = metricAggregateRoot.getServiceVo();
        assertNotNull(service);
        assertEquals("system", service.getType());
    }

    @Test
    void getProcessEntity() {
        ProcessEntity process = metricAggregateRoot.getProcessEntity();
        assertNotNull(process);
        assertEquals("/usr/libexec/polkitd --no-debug", process.getCommandLine());
        assertEquals("/usr/libexec/polkitd", process.getExecutable());
        assertEquals("polkitd", process.getName());
        assertEquals(4793, process.getPid());
        assertEquals("sleeping", process.getState());
        assertEquals("/", process.getWorkingDirectory());
        assertArrayEquals(new String[]{"/usr/libexec/polkitd", "--no-debug"}, process.getArgs().toArray());
        assertEquals(4793, process.getPgId());

        ProcessEntity.Parent parent = process.getParent();
        assertNotNull(parent);
        assertEquals(1, parent.getPid());

        ProcessEntity.Cpu cpu = process.getCpu();
        assertNotNull(cpu);
        assertEquals("2025-03-13T16:45:22.000Z", cpu.getStartTime());
        assertEquals(0, cpu.getPct());

        ProcessEntity.Memory memory = process.getMemory();
        assertNotNull(memory);
        assertEquals(0.0007, memory.getPct());
    }

    @Test
    void getSystemEntity() {
        SystemEntity system = metricAggregateRoot.getSystemEntity();
        assertNotNull(system);
        assertEquals("sleeping", system.getState());

        SystemEntity.Process systemProcess = system.getProcess();
        assertNotNull(systemProcess);
        assertEquals(3, systemProcess.getNumThreads());
        assertEquals("/usr/libexec/polkitd --no-debug", systemProcess.getCmdLine());

        SystemEntity.Process.Memory systemMemory = systemProcess.getMemory();
        assertNotNull(systemMemory);
        assertEquals(241164288L, systemMemory.getSize());
        assertEquals(7180288L, systemMemory.getShare());

        SystemEntity.Process.Memory.Rss rss = systemMemory.getRss();
        assertNotNull(rss);
        assertEquals(8503296L, rss.getBytes());
        assertEquals(0.0007, rss.getPct());

        SystemEntity.Process.Cpu systemCpu = systemProcess.getCpu();
        assertNotNull(systemCpu);
        assertEquals("2025-03-13T16:45:22.000Z", systemCpu.getStartTime());

        SystemEntity.Process.Cpu.Total totalCpu = systemCpu.getTotal();
        assertNotNull(totalCpu);
        assertEquals(10, totalCpu.getValue());
        assertEquals(0, totalCpu.getPct());
        assertEquals(0, totalCpu.getNorm().getPct());

        SystemEntity.Process.Fd fd = systemProcess.getFd();
        assertNotNull(fd);
        assertEquals(11, fd.getOpen());

        SystemEntity.Process.Fd.Limit limit = fd.getLimit();
        assertNotNull(limit);
        assertEquals(1048576, limit.getHard());
        assertEquals(1024, limit.getSoft());
    }

    @Test
    void getElasticCommonSchemaVo() {
        EcsVo ecs = metricAggregateRoot.getElasticCommonSchemaVo();
        assertNotNull(ecs);
        assertEquals("8.0.0", ecs.getVersion());
    }

    @Test
    void getHostEntity() {
        HostEntity host = metricAggregateRoot.getHostEntity();
        assertNotNull(host);
        assertEquals("lxyten", host.getName());
        assertEquals("Lxyten", host.getHostname());
        assertEquals("x86_64", host.getArchitecture());
        assertEquals("ddd8185a42e34d71a32110784f8a2913", host.getId());
        assertFalse(host.isContainerized());

        HostEntity.Os os = host.getOs();
        assertNotNull(os);
        assertEquals("debian", os.getFamily());
        assertEquals("Ubuntu", os.getName());
        assertEquals("5.15.153.1-microsoft-standard-WSL2", os.getKernel());
        assertEquals("jammy", os.getCodename());
        assertEquals("linux", os.getType());
        assertEquals("ubuntu", os.getPlatform());
        assertEquals("22.04.5 LTS (Jammy Jellyfish)", os.getVersion());

        String[] expectedIps = {
                "198.18.0.1", "2.0.0.1", "fe80::93cd:8203:9e4d:68d3",
                "10.129.87.223", "2001:da8:215:3c0a:a9f1:a00:e677:4413"
        };
        assertArrayEquals(expectedIps, host.getIp().toArray());

        String[] expectedMacs = {"00-15-5D-07-1E-13", "00-15-5D-A0-69-E7", "00-15-5D-BF-33-15"};
        assertArrayEquals(expectedMacs, host.getMac().toArray());
    }

    @Test
    void getAgentEntity() {
        AgentEntity agent = metricAggregateRoot.getAgentEntity();
        assertNotNull(agent);
        assertEquals("metricbeat", agent.getType());
        assertEquals("8.17.3", agent.getVersion());
        assertEquals("32936e4b-c241-4369-82e2-794a7847c367", agent.getEphemeralId());
        assertEquals("cab8659a-5c83-46c6-a73f-4af6cbfc88c9", agent.getId());
        assertEquals("Lxyten", agent.getName());
    }

    @Test
    void getEventVo() {
        EventVo event = metricAggregateRoot.getEventVo();
        assertNotNull(event);
        assertEquals("system", event.getModule());
        assertEquals(50252243L, event.getDuration());
        assertEquals("system.process", event.getDataset());
    }

    @Test
    void getMetricSetVo() {
        MetricSetVo metricSet = metricAggregateRoot.getMetricSetVo();
        assertNotNull(metricSet);
        assertEquals("process", metricSet.getName());
        assertEquals(10000, metricSet.getPeriod());
    }
}