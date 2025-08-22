# Intershop

Коротко о проекте: витрина интернет‑магазина на Spring WebFlux с Redis‑кешем и сервисом платежей. Запуск и работа — через Docker Compose. Межсервисная авторизация — OAuth2 Client Credentials (Keycloak).

## Состав
- online-store — веб‑приложение (http://localhost:8080)
- payment-service — платежный сервис (ресурсный сервер JWT)
- db — PostgreSQL
- redis — Redis
- keycloak — сервер авторизации (realm импортируется автоматически)

## Быстрый старт
```bash
# сборка артефактов
./mvnw -DskipTests package

# запуск контейнеров
docker compose up -d --build

# проверка
docker compose ps
```

Полезно:
- перезапустить платежи (сброс баланса): `docker compose restart payment-service-app`
- логи: `docker compose logs -f online-store-app` / `payment-service-app`

## Безопасность и роли
- Анонимные пользователи имеют роль ROLE_ANONYMOUS и видят витрину/карточки.
- USER/ADMIN доступны корзина, заказы, действия (+/−/в корзину).
- В шаблонах скрытие элементов по флагу `isUser`.
- В online-store методовая защита `@PreAuthorize` и правила в `SecurityConfig`.
- Межсервисные вызовы online-store -> payment-service подписываются access‑token из Keycloak (Client Credentials). `payment-service` — OAuth2 Resource Server (JWT).

## Конфигурация
- URL и креды платежного сервиса задаются через `payment.service.*` (`PaymentServiceProperties`).
- Профили `application.yml` настроены для Docker (Keycloak, Redis, DB).

## Redis‑кеш (кратко)
- Кешируются: список товаров и карточки. TTL — 1 час. Инвалидация при изменениях корзины/товара.

## Тесты
```bash
# все модули
./mvnw test

# только online-store
./mvnw -pl online-store -am test

# только payment-service
./mvnw -pl payment-service -am test
```

## Частые операции
```bash
# остановка
docker compose down

# полная пересборка
./mvnw -DskipTests package && docker compose up -d --build
```

## Доступ
- Витрина: http://localhost:8080/main/items
- Карточка: http://localhost:8080/main/items/{id}
- Корзина: http://localhost:8080/cart/items (только USER/ADMIN)
- Платежный сервис: http://localhost:8081/payment

Если что‑то не поднимается — посмотрите логи соответствующего контейнера.