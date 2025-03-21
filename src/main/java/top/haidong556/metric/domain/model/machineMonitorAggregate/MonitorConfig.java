package top.haidong556.metric.domain.model.machineMonitorAggregate;

import lombok.Builder;
import lombok.Getter;
import top.haidong556.metric.domain.model.metricAggregate.MetricAggregateRoot;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 监控配置类，包含：
 * 1. 需要监听的监控项
 * 2. 每个监控项的滑动窗口大小
 * 3. 每个监控项的 EWMA 检测器参数（alpha、k）
 */
public class MonitorConfig {
    // 保存所有配置好的监听指标
    private final Map<MetricIndex, MetricConfig> metricConfigMap = new HashMap<>();

    private MonitorConfig(){}

    /**
     * 添加一个监控指标配置
     *
     * @param metricName 监控项名称
     * @param windowSize 滑动窗口大小
     * @param alpha      EWMA 平滑系数
     * @param k          EWMA 阈值系数
     */
    public void addMetricConfig(MetricIndex metricName, int windowSize, double alpha, double k) {
        metricConfigMap.put(metricName, new MetricConfig(windowSize, alpha, k));
    }

    /**
     * 获取某个监控项的配置
     */
    public MetricConfig getMetricConfig(MetricIndex metricName) {
        return metricConfigMap.get(metricName);
    }

    /**
     * 返回所有需要监听的监控项
     */
    public Map<MetricIndex, MetricConfig> getAllMonitoredMetrics() {
        return metricConfigMap;
    }


    public enum MetricClassType {
        LONG, DOUBLE
    }
    // 直接开始建造
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final MonitorConfig config = new MonitorConfig();

        public Builder addMetric(MetricIndex metricName, int windowSize, double alpha, double k) {
            config.addMetricConfig(metricName, windowSize, alpha, k);
            return this;
        }

        public MonitorConfig build() {
            return config;
        }
    }

    @Getter
    public enum MetricIndex {
        // ---------------------- 基础监控指标 ----------------------
        CPU("CPU", MetricClassType.DOUBLE) {
            @Override
            public Double extract(MetricAggregateRoot root) {
                // 提取 CPU 使用率，优先从 system.process.cpu.total.pct 里拿
                if (root.getSystemEntity() != null
                        && root.getSystemEntity().getProcess() != null
                        && root.getSystemEntity().getProcess().getCpu() != null
                        && root.getSystemEntity().getProcess().getCpu().getTotal() != null) {
                    return root.getSystemEntity().getProcess().getCpu().getTotal().getPct();
                }
                return null;
            }
        },
        MEMORY_RSS("MEMORY_RSS", MetricClassType.LONG) {
            @Override
            public Long extract(MetricAggregateRoot root) {
                // 从 system.process.memory.rss.bytes 提取 RSS
                if (root.getSystemEntity() != null
                        && root.getSystemEntity().getProcess() != null
                        && root.getSystemEntity().getProcess().getMemory() != null
                        && root.getSystemEntity().getProcess().getMemory().getRss() != null) {
                    return root.getSystemEntity().getProcess().getMemory().getRss().getBytes();
                }
                return null;
            }
        },
        THREAD_NUM("THREAD_NUM", MetricClassType.LONG) {
            @Override
            public Long extract(MetricAggregateRoot root) {
                // 提取线程数 system.process.numThreads
                if (root.getSystemEntity() != null
                        && root.getSystemEntity().getProcess() != null) {
                    return root.getSystemEntity().getProcess().getNumThreads();
                }
                return null;
            }
        },
        FD_OPEN("FD_OPEN", MetricClassType.LONG) {
            @Override
            public Long extract(MetricAggregateRoot root) {
                // 提取文件描述符 system.process.fd.open
                if (root.getSystemEntity() != null
                        && root.getSystemEntity().getProcess() != null
                        && root.getSystemEntity().getProcess().getFd() != null) {
                    return root.getSystemEntity().getProcess().getFd().getOpen();
                }
                return null;
            }
        },

        // ---------------------- IO 监控指标 ----------------------
        IO_READ_BYTES("IO_READ_BYTES", MetricClassType.LONG) {
            @Override
            public Long extract(MetricAggregateRoot root) {
                // io 相关字段示例，你可根据实际的 json 或 entity 加到 root 里
                return null; // 没找到对应字段，待你给具体IO模型
            }
        },
        IO_WRITE_BYTES("IO_WRITE_BYTES", MetricClassType.LONG) {
            @Override
            public Long extract(MetricAggregateRoot root) {
                return null; // 同上，IO相关字段待定
            }
        },

        // ---------------------- 网络监控指标 ----------------------
        NETWORK_IN_BYTES("NETWORK_IN_BYTES", MetricClassType.LONG) {
            @Override
            public Long extract(MetricAggregateRoot root) {
                // 提取网络入流量 system.network.in.bytes
                if (root.getSystemEntity() != null
                        && root.getSystemEntity().getNetwork() != null
                        && root.getSystemEntity().getNetwork().getIn() != null) {
                    return root.getSystemEntity().getNetwork().getIn().getBytes();
                }
                return null;
            }
        },
        NETWORK_OUT_BYTES("NETWORK_OUT_BYTES", MetricClassType.LONG) {
            @Override
            public Long extract(MetricAggregateRoot root) {
                // 提取网络出流量 system.network.out.bytes
                if (root.getSystemEntity() != null
                        && root.getSystemEntity().getNetwork() != null
                        && root.getSystemEntity().getNetwork().getOut() != null) {
                    return root.getSystemEntity().getNetwork().getOut().getBytes();
                }
                return null;
            }
        },
        NETWORK_IN_ERRORS("NETWORK_IN_ERRORS", MetricClassType.LONG) {
            @Override
            public Long extract(MetricAggregateRoot root) {
                // 网络入错误 system.network.in.errors
                if (root.getSystemEntity() != null
                        && root.getSystemEntity().getNetwork() != null
                        && root.getSystemEntity().getNetwork().getIn() != null) {
                    return root.getSystemEntity().getNetwork().getIn().getErrors();
                }
                return null;
            }
        },
        NETWORK_OUT_ERRORS("NETWORK_OUT_ERRORS", MetricClassType.LONG) {
            @Override
            public Long extract(MetricAggregateRoot root) {
                // 网络出错误 system.network.out.errors
                if (root.getSystemEntity() != null
                        && root.getSystemEntity().getNetwork() != null
                        && root.getSystemEntity().getNetwork().getOut() != null) {
                    return root.getSystemEntity().getNetwork().getOut().getErrors();
                }
                return null;
            }
        },
        NETWORK_IN_DROPPED("NETWORK_IN_DROPPED", MetricClassType.LONG) {
            @Override
            public Long extract(MetricAggregateRoot root) {
                // 网络入丢包 system.network.in.dropped
                if (root.getSystemEntity() != null
                        && root.getSystemEntity().getNetwork() != null
                        && root.getSystemEntity().getNetwork().getIn() != null) {
                    return root.getSystemEntity().getNetwork().getIn().getDropped();
                }
                return null;
            }
        },
        NETWORK_OUT_DROPPED("NETWORK_OUT_DROPPED", MetricClassType.LONG) {
            @Override
            public Long extract(MetricAggregateRoot root) {
                // 网络出丢包 system.network.out.dropped
                if (root.getSystemEntity() != null
                        && root.getSystemEntity().getNetwork() != null
                        && root.getSystemEntity().getNetwork().getOut() != null) {
                    return root.getSystemEntity().getNetwork().getOut().getDropped();
                }
                return null;
            }
        };


        private final String code;
        private final MetricClassType type;

        public abstract Number extract(MetricAggregateRoot root);

        MetricIndex(String code, MetricClassType type) {
            this.code = code;
            this.type = type;
        }

        // 根据 code 查找 Metric
        public static MetricIndex fromCode(String code) {
            for (MetricIndex metricIndex : MetricIndex.values()) {
                if (metricIndex.code.equals(code)) {
                    return metricIndex;
                }
            }
            throw new IllegalArgumentException("Unknown metric code: " + code);
        }
    }

    // 内部类：封装每个指标的详细配置
    public static class MetricConfig {
        public final int windowSize;   // 滑动窗口大小
        public final double alpha;     // EWMA 平滑系数
        public final double k;         // EWMA 阈值系数

        public MetricConfig(int windowSize, double alpha, double k) {
            this.windowSize = windowSize;
            this.alpha = alpha;
            this.k = k;
        }
    }
}
