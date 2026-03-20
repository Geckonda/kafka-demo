# kafka-demo

Проект шпаргалка для Apache Kafka

## Идея проекта

Проект моделирует упрощённую систему обработки заказов из трёх независимых микросервисов:

- **producer-service** — принимает HTTP запрос на создание заказа и публикует сообщение в Kafka
- **consumer-inventory** — слушает топики `orders.created` и `orders.processed`, имитирует обновление склада
- **consumer-notification** — слушает топик `notifications`, имитирует отправку уведомления клиенту

```
            POST /api/orders
                   ↓
          producer-service (8080)
                   ↓
                 Kafka
                ↙     ↘
consumer-inventory   consumer-notification
    (8081)                  (8082)
```

Каждый сервис работает независимо и общается только через Kafka — никаких прямых HTTP вызовов между сервисами.

## Топики

| Топик              | Партиции | Кто пишет        | Кто читает          |
|--------------------|----------|------------------|---------------------|
| orders.created     | 3        | producer-service | consumer-inventory  |
| orders.processed   | 3        | producer-service | consumer-inventory  |
| notifications      | 2        | producer-service | consumer-notification |

## Стек

- Java 21
- Spring Boot 4.0.x
- Spring Kafka 4.0.x
- Apache Kafka 7.5.0 (Confluent)
- Docker / Docker Compose

## Структура проекта

```
kafka-demo/
├── docker-compose.yml
├── pom.xml                        ← parent pom
├── producer-service/              ← Order Service, порт 8080
│   └── src/main/
│       ├── config/KafkaTopicConfig.java
│       ├── controller/OrderController.java
│       ├── dto/OrderDto.java
│       └── service/OrderProducerService.java
├── consumer-inventory/            ← Inventory Service, порт 8081
│   └── src/main/
│       ├── consumer/InventoryConsumer.java
│       └── dto/OrderDto.java
└── consumer-notification/         ← Notification Service, порт 8082
    └── src/main/
        ├── consumer/NotificationConsumer.java
        └── dto/OrderDto.java
```

## Быстрый старт

### 1. Требования

- Java 21+
- Maven 3.9+
- Docker Desktop

### 2. Запуск Kafka

```bash
docker compose up -d
```

### 3. Сбор проекта

```bash
mvn clean install -DskipTests
```

### 4. Запуск сервисов

Открыть три отдельных терминала:

```bash
# Терминал 1 — Producer
cd producer-service
mvn spring-boot:run

# Терминал 2 — Consumer Inventory
cd consumer-inventory
mvn spring-boot:run

# Терминал 3 — Consumer Notification
cd consumer-notification
mvn spring-boot:run
```

### 5. Отправить тестовый заказ

Через curl:

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName": "Александр", "product": "Laptop", "amount": 999.99}'
```

Или через Postman:

```
Method:  POST
URL:     http://localhost:8080/api/orders
Body:    raw → JSON

{
  "customerName": "Александр",
  "product": "Laptop",
  "amount": 999.99
}
```

### 6. Результат

В терминале **consumer-inventory**:
```
=== INVENTORY SERVICE ===
Topic: orders.created | Partition: 0 | Offset: 0
Processing order → id: xxx, customer: Александр, product: Laptop, amount: 999.99
Inventory updated for order: xxx
```

В терминале **consumer-notification**:
```
=== NOTIFICATION SERVICE ===
Topic: notifications | Partition: 0 | Offset: 0
Sending notification → customer: Александр, product: Laptop, amount: 999.99
Notification sent to customer: Александр
```

## Kafka UI

Веб-интерфейс для мониторинга топиков, сообщений и consumer groups:

```
http://localhost:8090
```

Полезные вкладки:
- **Topics** → выбрать топик → **Messages** — просмотр всех сообщений с payload
- **Consumer Groups** — текущие offsets для каждой группы

## Остановка

```bash
# Останови все Docker контейнеры
docker compose down
```
