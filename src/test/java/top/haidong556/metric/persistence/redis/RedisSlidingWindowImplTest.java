package top.haidong556.metric.persistence.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.haidong556.metric.MetricApplication;
import top.haidong556.metric.infrastructure.persistence.redis.RedisSlidingWindowImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MetricApplication.class)
class RedisSlidingWindowImplTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private RedisSlidingWindowImpl<Long> redisLongSlidingWindow;
    private RedisSlidingWindowImpl<Double> redisDoubleSlidingWindow;

    private static final String WINDOW_KEY = "testWindow";

    @BeforeEach
    void setUp() {
        // 初始化 RedisSlidingWindow 实例
        redisLongSlidingWindow = new RedisSlidingWindowImpl<>(WINDOW_KEY,30, redisTemplate);
        redisDoubleSlidingWindow = new RedisSlidingWindowImpl<>(WINDOW_KEY,30, redisTemplate);
    }
    @AfterEach
    void tearDown() {
        // 清理 Redis 数据，以便下一个测试用例运行
        redisDoubleSlidingWindow.clearWindow();
        redisLongSlidingWindow.clearWindow();
    }

    @Test
    void addEvent_Long() {
        // 测试 Long 类型事件的添加
        long eventCount = redisLongSlidingWindow.addEvent(10L);

        // 验证事件计数已更新
        assertEquals(1L, eventCount);
    }

    @Test
    void addEvent_Double() {
        // 测试 Double 类型事件的添加
        long eventCount = redisDoubleSlidingWindow.addEvent(10.5);

        // 验证事件计数已更新
        assertEquals(1L, eventCount);
    }

    @Test
    void getEventCount_Long() {
        // 测试 Long 类型事件计数获取
        redisLongSlidingWindow.addEvent(10L);
        redisLongSlidingWindow.addEvent(20L);

        long count = redisLongSlidingWindow.getEventCount();

        assertEquals(2L, count);
    }

    @Test
    void getEventCount_Double() {
        // 测试 Double 类型事件计数获取
        redisDoubleSlidingWindow.addEvent(10.5);
        redisDoubleSlidingWindow.addEvent(20.5);

        long count = redisDoubleSlidingWindow.getEventCount();

        assertEquals(2L, count);
    }

    @Test
    void getEvents_Long() {
        // 测试 Long 类型事件集合获取
        redisLongSlidingWindow.addEvent(1L);
        redisLongSlidingWindow.addEvent(2L);
        redisLongSlidingWindow.addEvent(3L);

        List<Long> events = redisLongSlidingWindow.getEvents();

        assertEquals(3, events.size());
        assertTrue(events.contains(1L));
        assertTrue(events.contains(2L));
        assertTrue(events.contains(3L));
    }

    @Test
    void getEvents_Double() {
        // 测试 Double 类型事件集合获取
        redisDoubleSlidingWindow.addEvent(1.1);
        redisDoubleSlidingWindow.addEvent(2.2);
        redisDoubleSlidingWindow.addEvent(3.3);

        List<Double> events = redisDoubleSlidingWindow.getEvents();

        assertEquals(3, events.size());
        assertTrue(events.contains(1.1));
        assertTrue(events.contains(2.2));
        assertTrue(events.contains(3.3));
    }

    @Test
    void averageEvent_Long() {
        // 测试 Long 类型事件的平均值计算
        redisLongSlidingWindow.addEvent(10L);
        redisLongSlidingWindow.addEvent(20L);

        Double average = redisLongSlidingWindow.averageEvent();

        assertEquals(15.0, average);
    }

    @Test
    void averageEvent_Double() {
        // 测试 Double 类型事件的平均值计算
        redisDoubleSlidingWindow.addEvent(10.5);
        redisDoubleSlidingWindow.addEvent(20.5);

        Double average = redisDoubleSlidingWindow.averageEvent();

        assertEquals(15.5, average);
    }

    @Test
    void clearWindow_Long() {
        // 测试 Long 类型事件的窗口清除
        redisLongSlidingWindow.addEvent(10L);
        redisLongSlidingWindow.addEvent(20L);

        redisLongSlidingWindow.clearWindow();

        // 检查 Redis 中的事件已被清除
        long count = redisLongSlidingWindow.getEventCount();
        assertEquals(0L, count);
    }

    @Test
    void clearWindow_Double() {
        // 测试 Double 类型事件的窗口清除
        redisDoubleSlidingWindow.addEvent(10.5);
        redisDoubleSlidingWindow.addEvent(20.5);

        redisDoubleSlidingWindow.clearWindow();

        // 检查 Redis 中的事件已被清除
        long count = redisDoubleSlidingWindow.getEventCount();
        assertEquals(0L, count);
    }

    @Test
    void getWindowSize() {
        // 测试获取滑动窗口大小
        assertEquals(60, redisLongSlidingWindow.getWindowSize());
        assertEquals(60, redisDoubleSlidingWindow.getWindowSize());
    }

    @Test
    void getWindowKey() {
        // 测试获取窗口键
        assertEquals(WINDOW_KEY, redisLongSlidingWindow.getWindowKey());
        assertEquals(WINDOW_KEY, redisDoubleSlidingWindow.getWindowKey());
    }
}
