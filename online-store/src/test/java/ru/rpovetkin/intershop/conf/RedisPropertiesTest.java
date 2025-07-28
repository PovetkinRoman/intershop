package ru.rpovetkin.intershop.conf;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = RedisProperties.class)
@EnableConfigurationProperties(RedisProperties.class)
@TestPropertySource(properties = {
    "redis.host=test-redis-host",
    "redis.port=6380",
    "redis.database=2",
    "redis.timeout=3000ms",
    "redis.pool.max-active=10",
    "redis.pool.max-idle=5",
    "redis.pool.min-idle=1",
    "redis.pool.max-wait=1500ms",
    "redis.cache.time-to-live=1800000",
    "redis.cache.cache-null-values=true",
    "redis.cache.use-key-prefix=false",
    "redis.cache.key-prefix=test:"
})
class RedisPropertiesTest {

    @Test
    void testRedisProperties() {
        RedisProperties properties = new RedisProperties();
        properties.setHost("test-redis-host");
        properties.setPort(6380);
        properties.setDatabase(2);
        properties.setTimeout(Duration.ofSeconds(3));
        
        // Настройки пула
        RedisProperties.Pool pool = new RedisProperties.Pool();
        pool.setMaxActive(10);
        pool.setMaxIdle(5);
        pool.setMinIdle(1);
        pool.setMaxWait(Duration.ofMillis(1500));
        properties.setPool(pool);
        
        // Настройки кеша
        RedisProperties.Cache cache = new RedisProperties.Cache();
        cache.setTimeToLive(Duration.ofMinutes(30));
        cache.setCacheNullValues(true);
        cache.setUseKeyPrefix(false);
        cache.setKeyPrefix("test:");
        properties.setCache(cache);
        
        // Проверяем значения
        assertEquals("test-redis-host", properties.getHost());
        assertEquals(6380, properties.getPort());
        assertEquals(2, properties.getDatabase());
        assertEquals(Duration.ofSeconds(3), properties.getTimeout());
        
        assertEquals(10, properties.getPool().getMaxActive());
        assertEquals(5, properties.getPool().getMaxIdle());
        assertEquals(1, properties.getPool().getMinIdle());
        assertEquals(Duration.ofMillis(1500), properties.getPool().getMaxWait());
        
        assertEquals(Duration.ofMinutes(30), properties.getCache().getTimeToLive());
        assertTrue(properties.getCache().isCacheNullValues());
        assertFalse(properties.getCache().isUseKeyPrefix());
        assertEquals("test:", properties.getCache().getKeyPrefix());
    }
    
    @Test
    void testDefaultValues() {
        RedisProperties properties = new RedisProperties();
        
        assertEquals("localhost", properties.getHost());
        assertEquals(6379, properties.getPort());
        assertEquals(0, properties.getDatabase());
        assertEquals(Duration.ofSeconds(2), properties.getTimeout());
        
        assertEquals(8, properties.getPool().getMaxActive());
        assertEquals(8, properties.getPool().getMaxIdle());
        assertEquals(0, properties.getPool().getMinIdle());
        assertEquals(Duration.ofMillis(-1), properties.getPool().getMaxWait());
        
        assertEquals(Duration.ofHours(1), properties.getCache().getTimeToLive());
        assertFalse(properties.getCache().isCacheNullValues());
        assertTrue(properties.getCache().isUseKeyPrefix());
        assertEquals("", properties.getCache().getKeyPrefix());
    }
} 