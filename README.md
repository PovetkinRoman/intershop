# Intershop - Online Store

Веб-приложение интернет-магазина с Redis кешированием, построенное на Spring Boot WebFlux и PostgreSQL.

## Архитектура

Проект состоит из следующих сервисов:
- **online-store** - основной веб-сервис (порт 8080)
- **payment-service** - сервис платежей (порт 8081)
- **PostgreSQL** - база данных
- **Redis** - кеширование данных

## Технологии

- **Backend**: Spring Boot 3.5.0, Spring WebFlux, Spring Data R2DBC
- **Database**: PostgreSQL 13
- **Cache**: Redis 7
- **Build**: Maven
- **Containerization**: Docker & Docker Compose

## Кеширование

Проект использует Redis для кеширования данных товаров:

### API Endpoints с кешированием:
- `GET /api/items` - список всех товаров (кешируется)
- `GET /api/items/{id}/card` - карточка товара (кешируется)

### Обычные страницы (без кеширования):
- `GET /items/{id}` - страница товара
- `GET /main/items` - главная страница
- `GET /cart/items` - корзина

### Кеш-ключи:
- `item-card::{id}` - карточки товаров
- `item-list::{id}` - данные для списка товаров

TTL кеша: 1 час

## Запуск проекта

### Предварительные требования

1. **Docker** и **Docker Compose** должны быть установлены
2. **Java 21** (для локальной разработки)
3. **Maven** (для локальной разработки)

### Быстрый запуск

```bash
# Клонировать репозиторий
git clone <repository-url>
cd intershop

# Запустить все сервисы
docker compose up -d

# Проверить статус
docker compose ps
```

### Полная пересборка

```bash
# Остановить все контейнеры
docker compose down

# Очистить Docker кеш
docker system prune -f

# Пересобрать проект
./mvnw clean package -DskipTests

# Пересобрать Docker образы без кеша
docker compose build --no-cache

# Запустить заново
docker compose up -d
```

### Просмотр логов

```bash
# Все сервисы
docker compose logs -f

# Конкретный сервис
docker compose logs -f online-store-app
docker compose logs -f payment-service-app
docker compose logs -f db
docker compose logs -f redis
```

## Доступные endpoints

### API (с кешированием)
- `http://localhost:8080/api/items` - список товаров
- `http://localhost:8080/api/items/{id}/card` - карточка товара

### Веб-страницы
- `http://localhost:8080/` - главная страница
- `http://localhost:8080/items/{id}` - страница товара
- `http://localhost:8080/cart/items` - корзина
- `http://localhost:8080/orders` - заказы

### Платежный сервис
- `http://localhost:8081/` - платежный сервис

## Управление кешем

### Просмотр кеша Redis
```bash
# Подключиться к Redis
docker compose exec redis redis-cli

# Просмотреть все ключи
keys *

# Просмотреть значение ключа
get "item-card::1"

# Очистить весь кеш
flushall
```

### Очистка кеша
Кеш автоматически очищается при:
- Изменении товара (добавление в корзину, изменение количества)
- Очистке корзины
- Истечении TTL (1 час)

## Разработка

### Локальная разработка

```bash
# Запустить только базу данных и Redis
docker compose up -d db redis

# Запустить приложение локально
./mvnw spring-boot:run -pl online-store
```

### Структура проекта

```
intershop/
├── online-store/           # Основной веб-сервис
│   ├── src/main/java/
│   │   └── ru/rpovetkin/intershop/
│   │       ├── conf/       # Конфигурации (Redis, etc.)
│   │       ├── model/      # Модели данных и DTO
│   │       ├── repository/ # Репозитории
│   │       ├── service/    # Бизнес-логика и кеширование
│   │       └── web/        # Контроллеры
│   └── src/main/resources/
│       ├── application.yml # Конфигурация приложения
│       └── templates/      # Thymeleaf шаблоны
├── payment-service/        # Сервис платежей
├── docker-compose.yaml     # Docker Compose конфигурация
└── README.md
```

## Мониторинг

### Проверка здоровья сервисов
```bash
# Статус контейнеров
docker compose ps

# Использование ресурсов
docker stats

# Проверка подключения к базе данных
docker compose exec db psql -U root -d intershop -c "SELECT 1;"

# Проверка подключения к Redis
docker compose exec redis redis-cli ping
```

## Остановка проекта

```bash
# Остановить все сервисы
docker compose down

# Остановить и удалить volumes (данные БД)
docker compose down -v
```

## Troubleshooting

### Проблемы с подключением к Redis
```bash
# Проверить логи Redis
docker compose logs redis

# Проверить сеть
docker network ls
docker network inspect intershop_default
```

### Проблемы с базой данных
```bash
# Проверить логи БД
docker compose logs db

# Подключиться к БД
docker compose exec db psql -U root -d intershop
```

### Очистка и перезапуск
```bash
# Полная очистка
docker compose down -v
docker system prune -f
docker volume prune -f

# Перезапуск
docker compose up -d
```

# Генерация OpenAPI клиента и серверного кода

## Как работает интеграция

- В проекте используется OpenAPI схема (`openapi.yaml`) для описания взаимодействия между основным приложением (online-store) и сервисом платежей (payment-service).
- Генерация клиента и серверных интерфейсов происходит автоматически при сборке через Maven.

## Генерация клиента (online-store)

- Используется плагин `openapi-generator-maven-plugin` с параметрами:
  - `generatorName=java`
  - `library=webclient`
  - API и модели генерируются в пакеты `ru.rpovetkin.intershop.payment.api` и `ru.rpovetkin.intershop.payment.model`.
- Необходимые зависимости для компиляции сгенерированного кода:
  - `org.openapitools:jackson-databind-nullable:0.2.6`
  - `com.google.code.findbugs:jsr305:3.0.2`

**Пример использования:**
```java
import ru.rpovetkin.intershop.payment.api.DefaultApi;
import ru.rpovetkin.intershop.payment.model.PaymentPostRequest;

DefaultApi paymentApi = new DefaultApi();
Boolean canPay = paymentApi.paymentGet(1000.0);
PaymentPostRequest req = new PaymentPostRequest();
req.setAmountForPay(1000.0);
Boolean paid = paymentApi.paymentPost(req);
```

## Генерация серверного кода (payment-service)

- Используется тот же плагин, но с параметрами:
  - `generatorName=spring`
  - `library=spring-boot`
  - `interfaceOnly=true`
- Необходимые зависимости для компиляции сгенерированного кода:
  - `org.openapitools:jackson-databind-nullable:0.2.6`
  - `com.google.code.findbugs:jsr305:3.0.2`
  - `io.swagger.core.v3:swagger-annotations:2.2.20`
  - `io.swagger.core.v3:swagger-models:2.2.20`
  - `javax.validation:validation-api:2.0.1.Final`
  - `javax.servlet:javax.servlet-api:4.0.1`

**Пример использования:**
- Реализуйте сгенерированный интерфейс `PaymentApi` в своём контроллере.

## Как сгенерировать код вручную

- Для генерации кода выполните:
  ```sh
  ./mvnw clean package -DskipTests
  ```
- Сгенерированные классы появятся в `target/generated-sources/openapi`.

## Важно
- Не удаляйте необходимые зависимости — они нужны для компиляции сгенерированного кода.
- Если меняется openapi.yaml, пересоберите проект для обновления клиента/интерфейсов.