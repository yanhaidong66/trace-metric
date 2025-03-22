package top.haidong556.metric.infrastructure.persistence.kafka;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.haidong556.metric.MetricApplication;

import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest(classes = MetricApplication.class)
class MetricKafkaConsumerTest {

    @Autowired
    private MetricKafkaConsumer metricKafkaConsumer;

    @Test
    void  consumeMetricRecords() throws InterruptedException {
        log.info("Kafka 消费者测试启动，开始监听 metric 主题...");

        // 保持测试线程运行，不然 @KafkaListener 会立刻结束
        TimeUnit.MINUTES.sleep(10);  // 监听 10 分钟，可根据需求调整时间

        log.info("Kafka 消费者测试结束");
    }
}
