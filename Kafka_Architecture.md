# Internals of Apache kafka

## Topic

- **Topic** in kafka is an Entity and there is a name attached to it.
  - Think of this as a database table
  - Producers send the message to the Broker using the topic name.
  - Consumers read the message from it using the topic name.
- Kafka maintains a one or more physical log behind the scenes.
- The messages are stored in the topic as a time ordered sequence.
- The messages in the topic are immutable.

### Kafka Message
- The message in Kafka topic has the following fields.
  - timestamp
  - Unique Identifier
  - payload(binary)
  - Offset
- Kafka messages are retained in the machine where the Kakfa is installed and its persisted in a file system.
  - **Messsages** are kept in the file system based on the configurable retention period.
  - **Retention period** is based on the per topic basis.  
  - Physical Storage constraints can also constrain message retention.

### Kafka as a Distributed Commit log
- Any message that gets sent to a topic is persisted in the system as a commit log.
- A Kafka topic thats created in a broker can span to multiple brokers based on the number of partitions.
    - Each topic in general has more than one partition so each partition has a log file.

### Kafka Producer
- A producer in Kafka writes the message to the topic.
- A message that gets sent to a specific partition is decided by the partition logic thats present at the producer end.
  - By default the **Paritioning** scheme is **round-robin**

### How Kafka Create Command Works?

- When a create command is issued the following steps will happen before the Topic is created.
  - The call first goes to the **Zookeeper**. Because the zookeeper has the complete information about the Kafka brokers information.34                                          
  -
