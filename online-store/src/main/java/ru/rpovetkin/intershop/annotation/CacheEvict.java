package ru.rpovetkin.intershop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для очистки кеша товаров
 * Используется для автоматической очистки кеша после изменения товаров
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheEvict {
    
    /**
     * Тип очистки кеша
     */
    CacheEvictType value() default CacheEvictType.ITEM;
    
    /**
     * ID товара (если применимо)
     * Поддерживает SpEL выражения
     */
    String itemId() default "";
    
    /**
     * Очищать ли все кеши товаров
     */
    boolean evictAll() default false;
    
    enum CacheEvictType {
        /**
         * Очистить кеш конкретного товара
         */
        ITEM,
        
        /**
         * Очистить кеш всех товаров
         */
        ALL_ITEMS,
        
        /**
         * Очистить все кеши
         */
        ALL
    }
} 