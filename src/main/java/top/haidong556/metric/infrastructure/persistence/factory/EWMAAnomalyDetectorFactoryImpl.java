package top.haidong556.metric.infrastructure.persistence.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import top.haidong556.metric.domain.common.EWMAAnomalyDetectorFactory;
import top.haidong556.metric.domain.model.machineMonitorAggregate.detector.EWMAAnomalyDetector;
import top.haidong556.metric.infrastructure.persistence.redis.RedisEWMAAnomalyDetectorImpl;

@Component
public class EWMAAnomalyDetectorFactoryImpl implements EWMAAnomalyDetectorFactory {

    RedisTemplate<String, String> redisTemplate;
    @Autowired
    public EWMAAnomalyDetectorFactoryImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @Override
    public EWMAAnomalyDetector<Double> createDoubleEWMADetector(String ewmaKey, double alpha, double k ) {
        return new RedisEWMAAnomalyDetectorImpl<Double>(ewmaKey, alpha, k,redisTemplate);
    }
    @Override
    public EWMAAnomalyDetector<Long> createLongEWMADetector(String ewmaKey, double alpha, double k) {
        return new RedisEWMAAnomalyDetectorImpl<Long>(ewmaKey, alpha, k,redisTemplate);
    }


}
