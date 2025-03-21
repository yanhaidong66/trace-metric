package top.haidong556.metric.infrastructure.persistence.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import top.haidong556.metric.domain.model.machineMonitorAggregate.detector.SlidingWindow;

import java.util.List;
import java.util.stream.Collectors;

public class RedisSlidingWindowImpl<T extends Number> extends SlidingWindow<T> {

    private final String sumKey; // Redis 键（保存总和，保存类型 T 的值）
    private final String countKey; // Redis 键（保存计数，保存 long 类型的值）
    private final StringRedisTemplate redisTemplate;

    public RedisSlidingWindowImpl(String windowKey, long windowSize, StringRedisTemplate redisTemplate) {
        this.windowKey = windowKey;
        this.WINDOW_SIZE=windowSize;
        this.sumKey = windowKey + ":sum"; // 总和，数值类型
        this.countKey = windowKey + ":count"; // 计数，long 类型
        this.redisTemplate = redisTemplate;
    }

    @Override
    public long addEvent(T event) {
        String luaScript = """
            local windowKey = KEYS[1]
            local sumKey = KEYS[2]
            local countKey = KEYS[3]
            local event = tonumber(ARGV[1])
            local windowSize = tonumber(ARGV[2])
            
            -- 1. 插入新事件到左侧
            redis.call('LPUSH', windowKey, event)
            
            -- 2. 更新总和
            redis.call('INCRBYFLOAT', sumKey, event)
            
            -- 3. 更新计数
            redis.call('INCR', countKey)
            
            -- 4. 维护窗口大小
            local size = tonumber(redis.call('GET', countKey)) -- 确保 size 是数值
            if size and size > windowSize then
                local oldestValue = redis.call('RPOP', windowKey)
                if oldestValue then
                    local oldestEvent = tonumber(oldestValue)
                    redis.call('INCRBYFLOAT', sumKey, -oldestEvent)
                    redis.call('DECR', countKey)
                end
            end
            
            return tonumber(redis.call('GET', countKey)) -- 返回最新的 count 值
            """;

        // 执行 Lua 脚本
        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(luaScript, Long.class),
                List.of(windowKey, sumKey, countKey),
                event.toString(),
                String.valueOf(WINDOW_SIZE)
        );

        return result;
    }

    @Override
    public long getEventCount() {
        // 获取当前窗口内的事件数量
        String countStr = redisTemplate.opsForValue().get(countKey);
        return countStr != null ? Long.parseLong(countStr) : 0;
    }

    @Override
    public List<T> getEvents() {
        // 获取当前窗口内所有事件
        List<String> values = redisTemplate.opsForList().range(windowKey, 0, -1);
        return values.stream().map(value -> (T) parseNumber(value)).collect(Collectors.toList());  // 改为 List
    }


    private Number parseNumber(String value) {
        try {
            return Long.parseLong(value); // 尝试转换为 Long
        } catch (NumberFormatException e) {
            return Double.parseDouble(value); // 如果失败则转换为 Double
        }
    }

    @Override
    public Double averageEvent() {
        String luaScript =
                "local sum = redis.call('GET', KEYS[1]) " +
                        "local count = redis.call('GET', KEYS[2]) " +
                        "if not sum then sum = '0' end " +
                        "if not count then count = '0' end " +
                        "sum = tonumber(sum) " +
                        "count = tonumber(count) " +
                        "if count > 0 then " +
                        "   return tostring(sum / count) " +
                        "else " +
                        "   return '0.0' " +
                        "end";

        // 先返回 String，再转换为 Double
        String result = redisTemplate.execute(
                new DefaultRedisScript<>(luaScript, String.class),
                List.of(sumKey, countKey)
        );

        return result != null ? Double.parseDouble(result) : 0.0;
    }

    @Override
    public void clearWindow() {
        // 删除 Redis 中的事件数据、总和和计数
        redisTemplate.delete(windowKey);
        redisTemplate.delete(sumKey);
        redisTemplate.delete(countKey);
    }

    @Override
    public long getWindowSize() {
        return WINDOW_SIZE;
    }

    @Override
    public String getWindowKey() {
        return this.windowKey;
    }
}
