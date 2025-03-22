package top.haidong556.metric.infrastructure.persistence.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import top.haidong556.metric.domain.model.metricAggregate.MetricAggregateRoot;

@Slf4j
@Service
public class MetricKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public MetricKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 发送 MetricEvent 到 Kafka
     * @param metricEvent 业务对象
     */
    public void sendMetricEvent(MetricAggregateRoot metricEvent) {
        try {
            String metricJson = objectMapper.writeValueAsString(metricEvent);
            kafkaTemplate.send(new ProducerRecord<>("metric", metricJson));
            log.info("成功发送 metric 消息: {}", metricJson);
        } catch (Exception e) {
            log.error("发送 Kafka metric 消息失败", e);
        }
    }
}
