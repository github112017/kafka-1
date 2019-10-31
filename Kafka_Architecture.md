# Internals of Apache kafka

# Why Kafka ?

# What is Kafka ?
- Kafka is a **distributed stream processing** system. Read this link - https://kafka.apache.org/intro .
  - Publish and subscribe to streams of records, similar to a message queue or enterprise messaging system
  - Store streams of records in a fault-tolerant durable way
  - Process streams of records as they occur.
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
  - Offset - The records in the partitions are each assigned a sequential id number called the **offset** that uniquely identifies each record within the partition.
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

## Kafka Producer
- A producer in Kafka writes the message to the topic.
- When the send() method is invoked, below are the steps that are performed.
  - Producer will reach out to the cluster and fetches the **metadata**. This data is always upto date as the producer invokes updates the metadata everytime a call is made to the producer.
  - Serializer -> Partitioner -> RecordAccumulator
  - Serializer : Serializes the message.
#### Partitioning Scheme
- A message that gets sent to a specific partition is decided by the partition logic thats present at the producer end.
  - By default the **Paritioning** scheme is **round-robin**
- Partitioner : Determines the partition to which the message needs to be posted
  - direct  : Partition number is mentioned as part of the **send** call itself.
  - roundrobin  : If no key or partition is mentioned, then the **round-robin** will be used.
  - key-mod hash  : Applies murmur hash of the key and applies the modulus function of the number of partitions.
    ```
    murmurhash/3(no of paritions)
    ```
  - CustomPartitioner  : Own partitioning scheme to determine the partition of the topic.

#### RecordAccumulator : InMemory Queue like data-structure.      
- Each send invocation in general will require a resource overhead.
- Kafka uses the concept of a **microbatch** for :
  - Producer
  - Writing
  - Consumer
- There is a **RecordBatch** for each TopicParition in the **RecordAccumulator**.
-  **RecordBatch**
  - Each RecordBatch has a limit on how many records it can buffer and is determined by the **batch.size** property.
  - There is a global **buffer.memory** property value which is a threshold value of how much memory can be used to buffer across all RecordBath that are waiting to be sent to the Kafka broker. This also represented as number of bytes.

#### When Records are sent to the server?
**batch.size:**  
  - When the **batch.size** is met then the records are immediately sent to the server.
**linger.ms:**  
  - If the **batch.size** is not met then there is another configuration setting called **linger.ms**
  - It represents the number of milli seconds the unfilled buffer should wait before writing to the server.
**max.block.ms**
  - If records are sent faster than they can be delivered to the server, then the producer will block for **max.block.ms** after which it will throw an exception.

#### Delivery Guarantees

- **acks** - Broker Acknowledgment
  - 0 -> Fire and Forget
  - 1 -> Leader Acknowledged
  - 2 -> replication quorum acknowledged.
    - This method assures the data is replicated in the all the clusters but there is a performance impact to this option.

- **retries** - No of times that you would like to retry.
**delivery.timeout.ms** - The time to report the failure or successful after the send call returns.  
- **retry.backoff.ms**  - wait for the retry before every attempt.

#### Ordering Guarantees
- The ordering is guaranteed only at the partition level.
- If an error occurred for a given message and the following messages that were published were successful. In this case there is a possibility that the order can go our of sync.  
- **max.in.flight.request.per.connection** - If the value of this property is set as **1** then its possible to achieve ordering. But this is the not the recommended option as it will cause other performance related issues.

#### Message serialization:
- All the messages are encoded as binary inside the Kafka Broker. So the key and value takes care of generating the binary(encoding technique) using the specified serialization techniques.  
- This is not just for the network transport. This helps to achieve storage and compression.
- The whole lifecycle of the message starts with the producer.

#### Things to Do (Kafka Producer)

- Write CustomPartitioner
- Write CustomSerializer
- Compression of messages

## Kafka Consumer
- The consumer is used in general to read the messages from the Kafka topic.
- The simple KafkaConsumer example is given below.

### Consumer APIs
- subscribe():
  - The subscribe api is used to subscribe to a list of topics.
  - There are no limits on how many topics that a particular consumer can subscribe to.

```
    Map<String,String> propsMap = new HashMap<>();
    propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093");
    propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    KafkaConsumer consumer = new KafkaConsumer(propsMap);
    consumer.subscribe(Arrays.asList("test-topic"));
```  

- unSubscribe()
  - The **unSubscribe** method in consumer is mainly used to unsubscribe the consumer from the broker.
- assign()
  - The **assign** method in consumer is mainly used to read a message from a specific Partition.

```
  public  void assign(){
      TopicPartition topicPartition  = new TopicPartition(TOPIC, 1);
      KafkaConsumer assignConsumer = new KafkaConsumer(propsMap());
      assignConsumer.assign(Arrays.asList(topicPartition));

  }
```

### Consumer Partition Management

- The consumer partition management is completely managed to you by the Kafka Broker itself.
- The consumer will poll and look for new messages from the subscribed topics.  
**subscribe**:  
  - The benefit of using subscribe method is that the partition management is entirely managed for you.
  - Any change to the **kafka** topic is notified to the consumer by the broker.
  - Consumer maintains the **subscriptions** in an internal object called subscription state.
  - This automatic subscription management is available only in subscribe() method.
- The Consumer api has something called **Subscription** state which the maintaines the state of the
