package ru.rpovetkin.intershop.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import ru.rpovetkin.intershop.annotation.CacheEvict;
import ru.rpovetkin.intershop.service.CacheService;

import java.lang.reflect.Method;

@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class CacheEvictAspect {

    private final CacheService cacheService;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(cacheEvict)")
    public Object aroundCacheEvict(ProceedingJoinPoint joinPoint, CacheEvict cacheEvict) throws Throwable {
        try {
            // Выполняем метод
            Object result = joinPoint.proceed();
            
            // Очищаем кеш после успешного выполнения
            evictCache(joinPoint, cacheEvict);
            
            return result;
        } catch (Exception e) {
            log.error("Error during cache eviction for method: {}", joinPoint.getSignature().getName(), e);
            throw e;
        }
    }

    private void evictCache(ProceedingJoinPoint joinPoint, CacheEvict cacheEvict) {
        try {
            switch (cacheEvict.value()) {
                case ITEM:
                    evictItemCache(joinPoint, cacheEvict);
                    break;
                case ALL_ITEMS:
                    evictAllItemsCache();
                    break;
                case ALL:
                    evictAllCaches();
                    break;
            }
        } catch (Exception e) {
            log.error("Error during cache eviction", e);
        }
    }

    private void evictItemCache(ProceedingJoinPoint joinPoint, CacheEvict cacheEvict) {
        if (!cacheEvict.itemId().isEmpty()) {
            Long itemId = evaluateItemId(joinPoint, cacheEvict.itemId());
            if (itemId != null) {
                log.debug("Evicting cache for item ID: {}", itemId);
                cacheService.evictAllItemCaches(itemId);
            }
        } else {
            // Если itemId не указан, пытаемся извлечь из первого параметра
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof Long) {
                Long itemId = (Long) args[0];
                log.debug("Evicting cache for item ID from first parameter: {}", itemId);
                cacheService.evictAllItemCaches(itemId);
            }
        }
    }

    private void evictAllItemsCache() {
        log.debug("Evicting all items cache");
        cacheService.evictAllItemsList();
    }

    private void evictAllCaches() {
        log.debug("Evicting all caches");
        cacheService.evictAllItemsList();
    }

    private Long evaluateItemId(ProceedingJoinPoint joinPoint, String itemIdExpression) {
        try {
            // Создаем контекст для SpEL
            EvaluationContext context = new StandardEvaluationContext();
            
            // Добавляем параметры метода в контекст
            Object[] args = joinPoint.getArgs();
            String[] paramNames = getParameterNames(joinPoint);
            
            for (int i = 0; i < args.length && i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            
            // Выполняем выражение
            Expression expression = parser.parseExpression(itemIdExpression);
            Object result = expression.getValue(context);
            
            if (result instanceof Long) {
                return (Long) result;
            } else if (result instanceof Number) {
                return ((Number) result).longValue();
            }
            
        } catch (Exception e) {
            log.warn("Failed to evaluate itemId expression: {}", itemIdExpression, e);
        }
        
        return null;
    }

    private String[] getParameterNames(ProceedingJoinPoint joinPoint) {
        try {
            Method method = getMethod(joinPoint);
            if (method != null) {
                return java.util.Arrays.stream(method.getParameters())
                        .map(java.lang.reflect.Parameter::getName)
                        .toArray(String[]::new);
            }
        } catch (Exception e) {
            log.warn("Failed to get parameter names", e);
        }
        
        // Возвращаем дефолтные имена параметров
        Object[] args = joinPoint.getArgs();
        String[] paramNames = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            paramNames[i] = "arg" + i;
        }
        return paramNames;
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.getTarget().getClass()
                    .getMethod(joinPoint.getSignature().getName(),
                            ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterTypes());
        } catch (Exception e) {
            return null;
        }
    }
} 