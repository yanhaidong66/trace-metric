package top.haidong556.metric.infrastructure.persistence.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisClient {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisClient(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 设置字符串类型的值
    public void set(String key, String value) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, value);
    }

    // 设置字符串类型的值，并设置过期时间
    public void set(String key, String value, long timeout, TimeUnit timeUnit) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, value, timeout, timeUnit);
    }

    // 获取指定 key 的值
    public String get(String key) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.get(key);
    }

    // 删除指定 key
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // 判断 key 是否存在
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}

