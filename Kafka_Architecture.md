# Internals of Apache kafka

# What is Kafka ?
- Kafka is a **distributed stream processing** system.
- What is stream processing ?
  - Application's working on the data as it arrives to the system.
  - It is the ability of the application to act on the infinite streams of data with continuous computation as it arrives to the system.
  .
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

### How Kafka Create Command Works?

- When a create command is issued the following steps will happen before the Topic is created.
  - The call first goes to the **Zookeeper**. Because the zookeeper has the complete information about the Kafka brokers in the cluster.
  - If the topic request is for multiple paritions then Zookeeper elects the leader gor each partition from the available brokers and assign  the patitions of the specific topic.

### Fault Tolerance - Replication Factor
- Fault Tolerance is achieved by configuring the replication factor.
- Replication factor decides how many number of replications of the same data is available.
  - This is to make sure that the data is not lost in the event of the broker crash or failure.
  - Minimum of 2 or 3 is recommended.
  - This is configured on a topic basis.


#### ISR (In Sync Replica)
- When the data is replicated in the available brokers as per value of the replication factor then this state is called **Quorum**.
- This provides resiliency to the Kafka topic in the event of a failure.

### Kafka Producer
- A producer in Kafka writes the message to the topic.
- A message that gets sent to a specific partition is decided by the partition logic thats present at the producer end.
  - By default the **Paritioning** scheme is **round-robin**

#### Message serialization:
- All the messages are encoded as binary inside the Kafka Broker. So the key and value takes care of generating the binary(encoding technique) using the specified serialization techniques.  
- This is not just for the network transport. This helps to achieve storage and compression.
- The whole lifecycle of the message starts with the producer.
