package top.haidong556.metric.domain.model.metricAggregate;

import java.util.List;

public interface MetricRepo {
    /**
     * 保存一个 MetricAggregateRoot 实体
     * @param metricAggregateRoot 需要保存的实体
     * @return 保存后的实体
     */
    MetricAggregateRoot save(MetricAggregateRoot metricAggregateRoot);

    /**
     * 根据 ID 查找 MetricAggregateRoot
     * @param id 实体的唯一 ID
     * @return 如果存在返回实体，否则返回 null
     */
    MetricAggregateRoot findById(String id);

    /**
     * 获取所有 MetricAggregateRoot 记录
     * @return 返回所有的 MetricAggregateRoot
     */
    List<MetricAggregateRoot> findAll();

    /**
     * 删除指定 ID 的 MetricAggregateRoot
     * @param id 需要删除的实体 ID
     * @return 是否删除成功
     */
    boolean deleteById(String id);

    /**
     * 统计 MetricAggregateRoot 的总数
     * @return 记录数
     */
    long count();

    /**
     * 根据某个时间戳范围查询 MetricAggregateRoot
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 匹配的实体列表
     */
    List<MetricAggregateRoot> findByTimestampRange(String startTime, String endTime);

    /**
     * 保存一个 MetricAggregateRoot 实体
     * @param metricAggregateRoot 需要保存的实体
     * @return 是否存在，返回false则没找到并且保存了
     */
    boolean saveIfNotFound(MetricAggregateRoot metricAggregateRoot);
}

