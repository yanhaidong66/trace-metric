package top.haidong556.metric.infrastructure.persistence.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import top.haidong556.metric.domain.common.SlidingWindowFactory;
import top.haidong556.metric.domain.model.machineMonitorAggregate.detector.SlidingWindow;
import top.haidong556.metric.infrastructure.persistence.redis.RedisSlidingWindowImpl;

@Component
public class SlidingWindowFactoryImpl implements SlidingWindowFactory {
    StringRedisTemplate redisTemplate;
    @Autowired
    public SlidingWindowFactoryImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public SlidingWindow<Double> createDoubleSlidingWindow(int windowSize, String windowKey) {
        return new RedisSlidingWindowImpl<Double>(windowKey,windowSize,redisTemplate);
    }

    @Override
    public SlidingWindow<Long> createLongSlidingWindow(int windowSize, String windowKey) {
        return new RedisSlidingWindowImpl<Long>(windowKey,windowSize,redisTemplate);
    }
}
