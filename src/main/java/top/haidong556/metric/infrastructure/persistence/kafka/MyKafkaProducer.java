package top.haidong556.metric.infrastructure.persistence.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.Properties;

public class MyKafkaProducer {
    public static void main(String[] args) {
        // 设置生产者配置
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 创建 KafkaProducer 实例
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // 创建 ProducerRecord 消息
        ProducerRecord<String, String> record = new ProducerRecord<>("your_topic", "key", "value");

        // 发送消息
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
            } else {
                System.out.println("消息发送成功，主题：" + metadata.topic() + "，分区：" + metadata.partition());
            }
        });

        // 关闭生产者
        producer.close();
    }
}
