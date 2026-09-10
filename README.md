# Kafka Producer/Consumer with Spring Boot

A simple messaging demo built with **Apache Kafka (KRaft mode)** and **Spring Boot**.
It has two independent Spring Boot applications:

- **kafka_producer** — exposes a REST endpoint that publishes a message to a Kafka topic.
- **kafka_consumer** — listens to the same topic and consumes incoming messages.

## Architecture

```
Postman (HTTP GET)
      |
      v
Producer App  --(publishes to topic "JT")-->  Kafka Broker (KRaft mode)
                                                       |
                                                       v
                                                 Consumer App
```

## Prerequisites

- Java 17+ (or whatever JDK the projects target)
- Apache Kafka (downloaded, not necessarily installed as a service)
- Postman (or any HTTP client) for manual testing

## 1. Start Kafka in KRaft mode

Kafka is run **without ZooKeeper**, using KRaft mode. From the Kafka installation folder:

```bash
# 1. Generate a Cluster UUID
KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"

# 2. Format the log directories
bin/kafka-storage.sh format --standalone -t $KAFKA_CLUSTER_ID -c config/server.properties

# 3. Start the Kafka server
bin/kafka-server-start.sh config/server.properties
```

Leave this terminal running — this is the Kafka broker.

## 2. Run the Consumer

In a separate terminal, go to the consumer project folder and run:

```bash
./mvnw spring-boot:run
```

The consumer starts listening on the configured topic (`JT`) and will print every message it receives.

## 3. Run the Producer

In a third terminal, go to the producer project folder and run:

```bash
./mvnw spring-boot:run
```

By default it starts on port `9191` (as configured in `application.properties`).

## 4. Send a test message (via Postman)

With both apps running, send a **GET** request to:

```
http://localhost:9191/producer-app/publish/{message}
```

Example:

```
http://localhost:9191/producer-app/publish/HelloKafka
```

Expected response:

```
Message published successfully...
```

You should immediately see the message logged in the **consumer** terminal, and the producer terminal will log the topic offset it was written to.

## Notes

- Topic used: `orders`
- Endpoint is a `GET` for quick manual testing; a real-world API should use `POST` with a request body instead of a path variable.
- Order of startup matters: **Kafka broker → Consumer → Producer**, so the consumer is already subscribed before any message is sent.
