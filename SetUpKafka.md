# Kafka

## Setting Up Kafka

-   Start up the Zookeeper.

```youtrack
./zookeeper-server-start.sh ../config/zookeeper.properties
```

-   Start up the Kafka Broker.

```youtrack
./kafka-server-start.sh ../config/server.properties
```

## How to create a topic ?

**my-first-topic:**
```youtrack
./kafka-topics.sh --create --topic test-topic -zookeeper localhost:2181 --replication-factor 1 --partitions 4
```

## How to instantiate a Console Producer?

```youtrack
./kafka-console-producer.sh --broker-list localhost:9092 --topic test-topic
```

## How to instantiate a Console Consumer?

```youtrack
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic --from-beginning
```

## List the topics in a cluster

```
./kafka-topics.sh --zookeeper localhost:2181 --list
```

## Describe a topic

```
./kafka-topics.sh --zookeeper localhost:2181 --describe
```
