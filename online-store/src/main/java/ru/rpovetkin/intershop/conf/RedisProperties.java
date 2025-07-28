package ru.rpovetkin.intershop.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {
    
    /**
     * Включить ли Redis кеширование
     */
    private boolean enabled = true;
    
    /**
     * Хост Redis сервера
     */
    private String host = "localhost";
    
    /**
     * Порт Redis сервера
     */
    private int port = 6379;
    
    /**
     * Пароль для аутентификации (опционально)
     */
    private String password;
    
    /**
     * Номер базы данных Redis
     */
    private int database = 0;
    
    /**
     * Таймаут подключения
     */
    private Duration timeout = Duration.ofSeconds(2);
    
    /**
     * Настройки пула соединений
     */
    private Pool pool = new Pool();
    
    /**
     * Настройки кеша
     */
    private Cache cache = new Cache();
    
    @Data
    public static class Pool {
        /**
         * Максимальное количество активных соединений
         */
        private int maxActive = 8;
        
        /**
         * Максимальное количество неактивных соединений
         */
        private int maxIdle = 8;
        
        /**
         * Минимальное количество неактивных соединений
         */
        private int minIdle = 0;
        
        /**
         * Максимальное время ожидания соединения
         */
        private Duration maxWait = Duration.ofMillis(-1);
    }
    
    @Data
    public static class Cache {
        /**
         * Время жизни кеша по умолчанию
         */
        private Duration timeToLive = Duration.ofHours(1);
        
        /**
         * Кешировать ли null значения
         */
        private boolean cacheNullValues = false;
        
        /**
         * Использовать ключи с префиксом
         */
        private boolean useKeyPrefix = true;
        
        /**
         * Префикс для ключей кеша
         */
        private String keyPrefix = "";
    }
} 