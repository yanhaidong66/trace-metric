package top.haidong556.metric.domain.common;

import top.haidong556.metric.domain.model.metricAggregate.MetricAggregateRoot;

/**
 * Snowflake ID 生成器，基于 Twitter 的 Snowflake 算法
 * 生成 64 位唯一 ID，格式如下：
 *
 * <pre>
 * 0 - 41 位时间戳 - 5 位数据中心 ID - 5 位工作节点 ID - 12 位序列号
 * </pre>
 *
 * 其中：
 * - 1 位符号位：始终为 0，表示正数
 * - 41 位时间戳：表示从设定的 `epoch` 时间起的毫秒数，最多可使用 69 年
 * - 5 位数据中心 ID：支持最多 32 个数据中心
 * - 5 位工作节点 ID：支持最多 32 台机器
 * - 12 位序列号：同一毫秒最多生成 4096 个 ID
 *
 * **优点：**
 * - **全局唯一**：不同机器生成的 ID 不会重复
 * - **有序性**：基于时间戳，ID 按时间递增
 * - **高性能**：单机每秒可生成 **百万级** ID
 */
public class SnowflakeIdGenerator {
    /** 自定义起始时间戳（2023-11-14 00:00:00 UTC） */
    private final long epoch = 1700000000000L;

    /** 机器 ID 占用 5 位（最多支持 32 台机器） */
    private final long workerIdBits = 5L;
    /** 数据中心 ID 占用 5 位（最多支持 32 个数据中心） */
    private final long datacenterIdBits = 5L;
    /** 序列号占用 12 位（同一毫秒内最多生成 4096 个 ID） */
    private final long sequenceBits = 12L;

    /** 最大值（用于防止 ID 超出范围） */
    private final long maxWorkerId = ~(-1L << workerIdBits);
    private final long maxDatacenterId = ~(-1L << datacenterIdBits);
    private final long sequenceMask = ~(-1L << sequenceBits);

    /** 位移运算（用于拼接 ID 各部分） */
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /** 机器 ID */
    private long workerId;
    /** 数据中心 ID */
    private long datacenterId;
    /** 毫秒内序列号 */
    private long sequence = 0L;
    /** 记录上次生成 ID 的时间戳 */
    private long lastTimestamp = -1L;

    /**
     * 构造方法，初始化 Snowflake ID 生成器
     *
     * @param workerId     机器 ID（0~31）
     * @param datacenterId 数据中心 ID（0~31）
     */
    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("Worker ID 超出范围");
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID 超出范围");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 生成下一个唯一 ID
     *
     * @return 唯一的 Snowflake ID
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("系统时钟回退异常");
        }

        if (timestamp == lastTimestamp) {
            // 同一毫秒内增加序列号
            sequence = (sequence + 1) & sequenceMask;
            // 如果序列号溢出，等待下一毫秒
            if (sequence == 0) {
                while (timestamp <= lastTimestamp) {
                    timestamp = System.currentTimeMillis();
                }
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    /**
     * 根据 MetricAggregateRoot 计算 Worker ID
     *
     * @param root 统计数据根对象
     * @return 计算出的 Worker ID（0~31）
     */
    public static long generateWorkerId(MetricAggregateRoot root) {
        if (root.getHostEntity() == null || root.getHostEntity().getHostname() == null) {
            return 0;
        }
        return Math.abs(root.getHostEntity().getHostname().hashCode()) % 32;
    }

    /**
     * 根据 MetricAggregateRoot 计算 Datacenter ID
     *
     * @param root 统计数据根对象
     * @return 计算出的 Datacenter ID（0~31）
     */
    public static long generateDatacenterId(MetricAggregateRoot root) {
        if (root.getHostEntity() == null || root.getHostEntity().getIp() == null || root.getHostEntity().getIp().isEmpty()) {
            return 0;
        }
        return Math.abs(root.getHostEntity().getIp().get(0).hashCode()) % 32;
    }

    /**
     * 生成基于 MetricAggregateRoot 的唯一 ID
     *
     * @param root 统计数据根对象
     * @return 生成的唯一 ID
     */
    public static String generateMetricAggregateId(MetricAggregateRoot root) {
        long workerId = generateWorkerId(root);
        long datacenterId = generateDatacenterId(root);
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(workerId, datacenterId);
        return String.valueOf(generator.nextId());
    }
}
