# Kafka Producer–Consumer Demo (Spring Boot + KRaft Mode)

A minimal end-to-end messaging demo built with **Apache Kafka** (running in **KRaft mode**, no ZooKeeper) and **Spring Boot**. It consists of two independent Spring Boot applications communicating through a Kafka topic.

| App | Role | Description |
|---|---|---|
| `kafka_producer` | Producer | Exposes a REST endpoint that publishes messages to Kafka |
| `kafka_consumer` | Consumer | Subscribes to the topic and consumes incoming messages |

---

## Table of Contents

- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [1. Start Kafka (KRaft Mode)](#1-start-kafka-kraft-mode)
- [2. Run the Consumer](#2-run-the-consumer)
- [3. Run the Producer](#3-run-the-producer)
- [4. Send a Test Message](#4-send-a-test-message)
- [Consumer Groups & Partition Assignment](#consumer-groups--partition-assignment)
- [Notes](#notes)

---

## Architecture

```
        HTTP GET
Postman ────────► Producer App ────► Topic: "orders" ────► Consumer App
                  (port 9191)          (Kafka Broker)
```

## Prerequisites

- Java 17+
- Apache Kafka distribution downloaded locally (no separate ZooKeeper needed — KRaft mode)
- Postman or any HTTP client, for manual testing

---

## 1. Start Kafka (KRaft Mode)

From the Kafka installation directory:

```bash
# Generate a Cluster UUID
$KAFKA_CLUSTER_ID=(bin/kafka-storage.sh random-uuid)

# Format the log directories
bin/windows/kafka-storage.bat format --standalone -t $KAFKA_CLUSTER_ID -c config/server.properties

# Start the broker
bin/windows/kafka-server-start.bat config/server.properties
```

Keep this terminal open — it is running the Kafka broker itself.

## 2. Run the Consumer

In a new terminal, from the `kafka_consumer` project folder:

```bash
./mvnw spring-boot:run
```

The consumer joins its configured **consumer group** and starts listening on the `orders` topic (see [Consumer Groups](#consumer-groups--partition-assignment) below for how partitions are assigned).

## 3. Run the Producer

In another terminal, from the `kafka_producer` project folder:

```bash
./mvnw spring-boot:run
```

The producer app starts on `localhost:9191` by default (configured in `application.properties`).

## 4. Send a Test Message

With the broker, consumer, and producer all running, send a **GET** request:

```
http://localhost:9191/producer-app/publish/{message}
```

Example:

```
http://localhost:9191/producer-app/publish/Order-1001
```

Expected response:

```
Message published successfully...
```

The message should immediately appear in the consumer's terminal log, and the producer will log the partition offset it was written to.

---

## Consumer Groups & Partition Assignment

Kafka distributes the messages of a topic across **partitions**, and consumers read from those partitions as members of a **Consumer Group** (identified by `group.id`). Understanding this mapping is essential for scaling consumers correctly:

- **Single consumer:** it must be assigned a `group.id` so Kafka knows which offset/topic state to track for it — even a lone consumer belongs to a group.
- **Consumers = Partitions** (e.g. 3 consumers in the same group, topic has 3 partitions): each consumer is assigned exactly **one partition**. This is the ideal setup — maximum parallelism, no consumer is idle.
- **Consumers < Partitions** (e.g. 1 consumer, 3 partitions): that single consumer ends up handling **all 3 partitions itself**, processing messages sequentially — slower throughput, since parallelism is lost.
- **Consumers > Partitions** (e.g. 4 consumers, 3 partitions): only 3 consumers get assigned a partition; the 4th stays **idle**, waiting on standby. If one of the active consumers fails or leaves the group, Kafka triggers a **rebalance**, and the idle consumer is automatically assigned the freed partition.

**Rule of thumb:** the number of consumer instances in a group should not exceed the number of partitions on the topic — extra instances add redundancy for failover, not extra throughput.

> **Rebalancing** is the process where Kafka redistributes partitions among the active members of a consumer group whenever a consumer joins, leaves, or crashes.

---

## Notes

- **Topic name:** `orders`
- The publish endpoint uses `GET` with a path variable for quick manual testing. In a production API this should be a `POST` with a JSON request body instead.
- Startup order matters: **Kafka broker → Consumer → Producer**, so the consumer is already subscribed and ready before any message is sent.
