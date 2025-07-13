# Реализация Redis кеширования в модуле online-store

## Обзор

В модуль `online-store` успешно добавлено Redis кеширование для оптимизации производительности при работе с данными о товарах. Реализация включает в себя кеширование данных для карточек товаров и списков товаров с автоматической очисткой кеша при изменениях.

## Что было реализовано

### 1. Зависимости и конфигурация

#### Добавленные зависимости в `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

#### Конфигурация Redis в `docker-compose.yaml`:
```yaml
redis:
  image: redis:7-alpine
  ports:
    - "6379:6379"
  volumes:
    - redis_data:/data
  command: redis-server --appendonly yes
```

#### Настройки в `application.yml`:
```yaml
spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
  cache:
    type: redis
    redis:
      time-to-live: 3600000 # 1 час
      cache-null-values: false
```

### 2. DTO классы для кеширования

#### ItemCardDto - для карточки товара:
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCardDto implements Serializable {
    private Long id;
    private String imgPath;
    private String title;
    private BigDecimal price;
    private String description;
}
```

#### ItemListDto - для списка товаров:
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemListDto implements Serializable {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
}
```

### 3. Конфигурация Redis

#### RedisConfig.java:
- Настройка RedisTemplate с JSON сериализацией
- Конфигурация RedisCacheManager
- Включение кеширования с аннотацией `@EnableCaching`

### 4. Сервис кеширования

#### CacheService.java:
- `cacheItemCard(Item)` - кеширует карточку товара
- `cacheItemList(Item)` - кеширует данные для списка
- `cacheAllItemsList(List<Item>)` - кеширует все товары
- `evictItemCard(Long)` - очищает кеш карточки
- `evictItemList(Long)` - очищает кеш списка
- `evictAllItemsList()` - очищает кеш всех товаров
- `evictAllItemCaches(Long)` - очищает все кеши товара

### 5. Интеграция с ItemService

#### Обновленный ItemService:
- Добавлены методы для работы с кешированными данными
- Автоматическая очистка кеша при изменениях товаров
- Интеграция с CacheService

### 6. Обновленные контроллеры

#### ItemController и MainItemController:
- Используют кешированные данные вместо прямых запросов к БД
- Автоматическое кеширование при первом обращении

### 7. REST API для кешированных данных

#### ItemApiController:
- `GET /api/items/{id}/card` - получить карточку товара
- `GET /api/items/{id}/list` - получить данные товара для списка
- `GET /api/items/list` - получить все товары для списка
- `GET /api/items/search` - поиск товаров с пагинацией
- `GET /api/items` - получить товары с пагинацией

## Кешируемые данные

### Для карточки товара:
- **Идентификатор** (id)
- **Картинка** (imgPath)
- **Название** (title)
- **Цена** (price)
- **Описание** (description)

### Для списка товаров:
- **Идентификатор** (id)
- **Название** (title)
- **Описание** (description)
- **Цена** (price)

## Архитектура кеширования

### Кеши:
- `item-card` - кеш карточек товаров (ключ: ID товара)
- `item-list` - кеш данных для списков товаров (ключ: ID товара)
- `item-list-all` - кеш всех товаров для списка (ключ: 'all')

### Автоматическая очистка кеша:
1. При изменении количества товара в корзине
2. При очистке корзины
3. При обновлении данных товара

## Запуск и тестирование

### 1. Запуск всех сервисов:
```bash
docker-compose up -d
```

### 2. Проверка работы:
- Приложение доступно на порту 8080
- Redis доступен на порту 6379
- API endpoints для кешированных данных доступны по адресу `/api/items/*`

### 3. Тестирование:
```bash
./mvnw test -pl online-store -Dtest=CacheServiceTest
```

## Мониторинг и логирование

### Логирование Redis включено:
```yaml
logging:
  level:
    org.springframework.data.redis: DEBUG
```

### Производительность:
- Время жизни кеша: 1 час
- Максимальное количество соединений: 8
- Кеширование null значений отключено
- Используется JSON сериализация для объектов

## Преимущества реализации

1. **Производительность**: Значительное ускорение доступа к часто запрашиваемым данным
2. **Масштабируемость**: Redis поддерживает высокие нагрузки
3. **Автоматическое управление**: Кеш автоматически очищается при изменениях
4. **Гибкость**: Разделение данных для карточек и списков товаров
5. **API**: REST endpoints для работы с кешированными данными
6. **Мониторинг**: Подробное логирование для отладки

## Структура файлов

```
online-store/
├── src/main/java/ru/rpovetkin/intershop/
│   ├── conf/
│   │   └── RedisConfig.java
│   ├── model/
│   │   ├── ItemCardDto.java
│   │   └── ItemListDto.java
│   ├── service/
│   │   └── CacheService.java
│   └── web/
│       └── ItemApiController.java
├── src/test/java/ru/rpovetkin/intershop/
│   └── CacheServiceTest.java
├── pom.xml (обновлен)
├── application.yml (обновлен)
└── README_REDIS.md
```

## Заключение

Redis кеширование успешно интегрировано в модуль `online-store`. Реализация обеспечивает:
- Кеширование данных о товарах для карточек и списков
- Автоматическое управление кешем
- REST API для работы с кешированными данными
- Высокую производительность и масштабируемость
- Подробное логирование и мониторинг

Система готова к использованию и может быть легко расширена для кеширования других типов данных. 