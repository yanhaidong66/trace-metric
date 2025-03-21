package top.haidong556.metric.infrastructure.persistence.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import top.haidong556.metric.application.metricEventApplicationService.MetricEventApplicationService;

@Slf4j
@Service
public class MetricKafkaConsumer {

    private final MetricEventApplicationService metricEventApplicationService;

    @Autowired
    public MetricKafkaConsumer(MetricEventApplicationService metricEventApplicationService) {
        this.metricEventApplicationService = metricEventApplicationService;
    }

    // 使用 @KafkaListener 自动消费 Kafka 消息
    @KafkaListener(topics = "metric", groupId = "metric-group1", concurrency = "3")
    public void consumeMetricRecords(String metricJson) {
        try {
            // 直接调用应用服务处理消息
            metricEventApplicationService.processMetricEvent(metricJson);
        } catch (Exception e) {
            log.error("处理 Kafka 消息失败", e);
        }
    }
}

