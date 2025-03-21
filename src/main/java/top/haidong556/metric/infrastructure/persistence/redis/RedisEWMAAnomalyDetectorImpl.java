package top.haidong556.metric.infrastructure.persistence.redis;

import top.haidong556.metric.domain.common.AnomalyResult;
import top.haidong556.metric.domain.model.machineMonitorAggregate.detector.EWMAAnomalyDetector;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

public class RedisEWMAAnomalyDetectorImpl<T extends Number> extends EWMAAnomalyDetector<T> {

    // RedisTemplate 用于与 Redis 交互
    private final RedisTemplate<String, String> redisTemplate;

    // 构造器注入，初始化 RedisKey前缀、α、k 和 RedisTemplate
    public RedisEWMAAnomalyDetectorImpl(String ewmaKey, double alpha, double k, RedisTemplate<String, String> redisTemplate) {
        this.ewmaKey = ewmaKey;
        this.alpha = alpha;
        this.k = k;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public AnomalyResult detectAnomaly(T data) {
        // 将数据点转换为 double 类型
        double value = data.doubleValue();

        // 从 Redis 获取上次的平滑值 (sPrev) 和残差列表
        String sPrevStr = redisTemplate.opsForValue().get(ewmaKey + ":sPrev");
        String residualJson = redisTemplate.opsForValue().get(ewmaKey + ":residuals");

        double sPrev;
        List<Double> residuals;

        // 如果这是第一次运行，初始化状态并返回没有异常
        if (sPrevStr == null) {
            sPrev = value;  // 第一个数据点作为初始平滑值
            residuals = new ArrayList<>();
            residuals.add(0.0); // 初始残差为 0.0
            // 将初始化的 sPrev 和 residuals 存入 Redis
            redisTemplate.opsForValue().set(ewmaKey + ":sPrev", String.valueOf(sPrev));
            redisTemplate.opsForValue().set(ewmaKey + ":residuals", serializeList(residuals));

            // 返回没有异常
            AnomalyResult anomalyResult = new AnomalyResult("initialMachineId"); // 假设 machineId 为 "initialMachineId"
            anomalyResult.setHasAnomaly(false);
            return anomalyResult;
        } else {
            // 如果不是第一次运行，读取 Redis 中的状态
            sPrev = Double.parseDouble(sPrevStr);
            residuals = deserializeList(residualJson);
        }

        // 使用 EWMA 公式更新平滑值
        double sCurr = alpha * value + (1 - alpha) * sPrev;
        // 计算当前残差，残差为当前值与上一个平滑值之间的绝对差
        double residual = Math.abs(value - sPrev);
        residuals.add(residual); // 将当前残差添加到历史残差列表中

        // 计算历史残差的标准差
        double std = calculateStd(residuals);
        // 使用 k 倍标准差作为动态阈值
        double threshold = k * std;

        // 如果残差大于阈值，则认为是异常
        boolean isAnomaly = residual > threshold;

        // 更新 Redis 中的状态（保存最新的平滑值和残差列表）
        redisTemplate.opsForValue().set(ewmaKey + ":sPrev", String.valueOf(sCurr));
        redisTemplate.opsForValue().set(ewmaKey + ":residuals", serializeList(residuals));

        // 返回 AnomalyResult 对象，包含异常结果信息
        AnomalyResult anomalyResult = new AnomalyResult("machineId");  // 假设 machineId 为 "machineId"
        anomalyResult.setHasAnomaly(isAnomaly);
        if (isAnomaly) {
            anomalyResult.addAnomalyDetail("Anomaly detected for value: " + value);
        }

        return anomalyResult;
    }

    /**
     * 计算给定数值列表的标准差。
     * @param values 数值列表
     * @return 标准差
     */
    private double calculateStd(List<Double> values) {
        // 检查 residuals 列表是否为空，防止除以 0 错误
        if (values.isEmpty()) {
            return 0.0;
        }

        // 计算列表的均值
        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        // 计算方差
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2)) // 每个值与均值的差值的平方
                .sum() / values.size();  // 方差 = 平均差值的平方
        // 返回标准差
        return Math.sqrt(variance); // 标准差是方差的平方根
    }

    /**
     * 将 Double 列表序列化为一个字符串，列表元素以逗号分隔。
     * @param list Double 列表
     * @return 逗号分隔的字符串
     */
    private String serializeList(List<Double> list) {
        // 使用 Java 8 Stream API 将列表元素转换为字符串并连接成一个字符串
        return list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * 将一个以逗号分隔的字符串反序列化为 Double 列表。
     * @param str 逗号分隔的字符串
     * @return Double 列表
     */
    private List<Double> deserializeList(String str) {
        if (str == null || str.isEmpty()) return new ArrayList<>();
        // 按逗号分割字符串并转换为 Double 列表
        String[] parts = str.split(",");
        List<Double> list = new ArrayList<>();
        for (String part : parts) {
            list.add(Double.parseDouble(part)); // 将字符串转换为 Double
        }
        return list;
    }
}
