# 🧩 Kafka + Debezium + Spring Boot Integration

This `README.md` provides documentation for setting up a **Kafka + Debezium + Spring Boot** environment locally using Docker. This setup enables streaming data from a MySQL database using Debezium and consuming it in a Spring Boot application.

---

## 📦 Features

* 🧠 Apache Kafka (no Zookeeper, using KRaft mode)
* 🔌 Debezium Kafka Connect for CDC
* 📊 AKHQ for Kafka UI
* ⚙️ Spring Boot Kafka configuration

---

## 🛠️ Prerequisites

* Java 17+
* Maven
* Docker Desktop
* MySQL with binary logging enabled

### ✅ MySQL Configuration (Important)

Update your MySQL `my.ini` or `my.cnf` to enable binary logging and GTID:

```ini
[mysqld]
server-id=223344
log_bin=mysql-bin
binlog_format=row
binlog_row_image=full
gtid_mode=ON
enforce_gtid_consistency=ON
binlog_do_db=sincdev
bind-address = 0.0.0.0
```

---

## 🐳 Docker Compose File

Here is the `docker-compose.yml` configuration to set up Kafka, Kafka Connect (Debezium), and AKHQ:

```yaml
version: '3.8'
services:

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: kafka
    ports:
      - "9092:9092"
      - "9093:9093"
    environment:
      KAFKA_KRAFT_MODE: "true"
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka:9093
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: BROKER:PLAINTEXT,CONTROLLER:PLAINTEXT
      KAFKA_LISTENERS: BROKER://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093
      KAFKA_ADVERTISED_LISTENERS: BROKER://host.docker.internal:9092
      KAFKA_INTER_BROKER_LISTENER_NAME: BROKER
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_LOG_DIRS: /tmp/kraft-combined-logs
      CLUSTER_ID: "_6gy4M1CSDemWf1YOHfmcA"
    networks:
      - kafka-net

  connect:
    image: debezium/connect:2.5
    container_name: connect
    ports:
      - "8083:8083"
    environment:
      BOOTSTRAP_SERVERS: kafka:9092
      GROUP_ID: 1
      CONFIG_STORAGE_TOPIC: connect-configs
      OFFSET_STORAGE_TOPIC: connect-offsets
      STATUS_STORAGE_TOPIC: connect-status
      KEY_CONVERTER: org.apache.kafka.connect.json.JsonConverter
      VALUE_CONVERTER: org.apache.kafka.connect.json.JsonConverter
      CONNECT_REST_ADVERTISED_HOST_NAME: connect
      CONNECT_PLUGIN_PATH: /kafka/connect,/usr/share/java
    depends_on:
      - kafka
    networks:
      - kafka-net

  akhq:
    image: tchiotludo/akhq:latest
    container_name: akhq
    ports:
      - "8081:8080"
    depends_on:
      - kafka
    restart: on-failure
    environment:
      AKHQ_CONFIGURATION: |
        akhq:
          connections:
            kafka-cluster:
              properties:
                bootstrap.servers: "kafka:9092"
    networks:
      - kafka-net

networks:
  kafka-net:
    name: my-kafka-network
```

---

## 🧪 Spring Boot Kafka Application

### `application.yml`

```yaml
server:
  port: 8087

spring:
  application:
    name: spring-boot-kafka-example

  kafka:
    bootstrap-servers: localhost:9092

    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      acks: all
      retries: 3
      properties:
        enable.idempotence: true
        max.block.ms: 60000

    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      group-id: my-spring-boot-group
      auto-offset-reset: earliest
      enable-auto-commit: true
      auto-commit-interval: 1000
      properties:
        max.poll.records: 500
        session.timeout.ms: 45000
        heartbeat.interval.ms: 10000

    listener:
      type: single
      concurrency: 3
      ack-mode: record
```

---

## 🔌 Debezium Connector Configuration

Once Kafka and Debezium are up, create a Debezium MySQL connector using the below JSON payload:

```json
{
  "name": "tcs_sincdev",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "database.hostname": "host.docker.internal",
    "database.port": "3306",
    "database.user": "debezium",
    "database.password": "dbz",
    "database.server.id": "184054",
    "topic.prefix": "aosdev",
    "database.include.list": "sincdev",
    "table.include.list": "sincdev.users",
    "schema.history.internal.kafka.bootstrap.servers": "kafka:9092",
    "schema.history.internal.kafka.topic": "schema-changes.tcs_sincdev",
    "snapshot.mode": "initial"
  }
}
```

Use any HTTP tool (like Postman or curl) to send this request to:

```
POST http://localhost:8083/connectors
Content-Type: application/json
```

---

## 🔍 Steps to Run and Verify

1. **Update MySQL**:

   * Add the config block shown above to `my.ini`
   * Restart your MySQL server

2. **Start Docker Services**:

   ```bash
   docker-compose up -d
   ```

3. **Open AKHQ** UI:

   * Visit: [http://localhost:8081](http://localhost:8081)
   * You should see the `aosdev` topic after connector is created.

4. **Create Debezium Connector**:

   * Use Postman or curl to send the above connector payload to `http://localhost:8083/connectors`

5. **Start Spring Boot App**:

   * Run your application with the provided `application.yml`
   * Add Kafka consumer to listen to topic `aosdev.sincdev.users`

6. **Insert Data into MySQL**:

   * Insert records into `sincdev.users`
   * Observe records flow to Kafka topic and then to Spring Boot logs

---

## ✅ Summary

* 🚀 Kafka with KRaft mode
* 🔄 CDC with Debezium
* 🖥️ Kafka UI with AKHQ
* ☕ Spring Boot Kafka consumer/producer setup
* 🛠️ Connector payload included for easy MySQL CDC setup
* 🐳 Full Docker Compose for Kafka ecosystem
* 🧪 Verification steps to test your pipeline

Let me know if you'd like to:

* Add sample consumers
* Auto-create topics or connectors
* Use dockerized MySQL with init data
