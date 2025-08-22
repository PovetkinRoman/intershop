# Redis‑кеш (кратко)

- Кешируются карточки товаров и списки товаров (TTL 1 час).
- Инвалидация при изменении корзины/товара.
- Основные классы:
  - `conf/RedisConfig.java` — конфигурация Redis и CacheManager
  - `service/CacheService.java` — операции кеша, инвалидация
  - `service/ItemService.java` — точки инвалидации при изменениях
- API без привязки к кешу находится в контроллерах `ItemApiController`, страницы рендерятся в `MainItemController`/`ItemController`.

Запуск/логи — см. основной `README.md`. 