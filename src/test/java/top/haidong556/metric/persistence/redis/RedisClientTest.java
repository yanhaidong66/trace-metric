package top.haidong556.metric.persistence.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import top.haidong556.metric.MetricApplication;
import top.haidong556.metric.infrastructure.persistence.redis.RedisClient;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MetricApplication.class)
class RedisClientTest {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        valueOperations = redisTemplate.opsForValue();
    }

    @Test
    void set() {
        // Arrange
        String key = "myKey";
        String value = "myValue";

        // Act
        redisClient.set(key, value);

        // Assert
        assertEquals(value, valueOperations.get(key));
    }

    @Test
    void testSetWithTimeout() {
        // Arrange
        String key = "myKey";
        String value = "myValue";
        long timeout = 5L;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        // Act
        redisClient.set(key, value, timeout, timeUnit);

        // Assert
        assertEquals(value, valueOperations.get(key));

        // Verify that the key will expire after the specified timeout
        try {
            TimeUnit.SECONDS.sleep(timeout + 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNull(valueOperations.get(key)); // The key should expire after the timeout
    }

    @Test
    void get() {
        // Arrange
        String key = "myKey";
        String expectedValue = "myValue";
        redisClient.set(key, expectedValue);

        // Act
        String actualValue = redisClient.get(key);

        // Assert
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void delete() {
        // Arrange
        String key = "myKey";
        String value = "myValue";
        redisClient.set(key, value);

        // Act
        redisClient.delete(key);

        // Assert
        assertNull(redisClient.get(key));
    }

    @Test
    void exists() {
        // Arrange
        String key = "myKey";
        redisClient.set(key, "myValue");

        // Act & Assert
        assertTrue(redisClient.exists(key));

        redisClient.delete(key);
        assertFalse(redisClient.exists(key));
    }
}
