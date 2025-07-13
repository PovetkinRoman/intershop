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