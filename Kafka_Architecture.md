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
    - Kafka will hash the key and use the result to map the record to a partition.
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

### Custom Serializers
-

#### Things to Do (Kafka Producer)

- Write CustomPartitioner
- Write CustomSerializer

### Kafka Producer and Partitions

- All messages with the same key will go to the same partition.
-

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


### The Poll Loop

- The **poll** loop is really key because this is the one which kicks off everything and connects to the broker and polls the records and retrieves the records from the broker.
- The poll loop handles all details of coordination, partition rebalances, heartbeats, and data fetching,
- This is a single threaded process.
- The **poll()** returns a list of records. Each record contains the topic and partition the record came from, the offset of the record within the partition, and of course the key and the value of the record. Typically we want to iterate over the list and process the records individually.
- Below are the different objects that are involved in the Kafka Consumer
  - **SubscriptionState** -> Plays as a source of truth for the information about the Topic and Partitions the Kafka consumer instance subscribes or assigned to.
    - This also maintains the last committed offset of a given partition.
    - This helps the new consumer to read from the last committed offset.
  - **Fetcher** -> This plays an important role in communication between the consumer and the broker. This initiates the connection between the consumer and broker through the **Consumer Network Client**. The fetcher gets the information about the topic and paritions to retrieve the record from the **subscriptionstate**.
  - **Consumer Network Client:**
    - Consumer sends heartbeats through this as a way of letting the broker know which consumer is alive and connected.
    - Additionally metadata information from the broker is received.
  - **Metadata** -> The metadata object is instantiated in the consumer using the information received from the network client. This gets upto date with every time the **poll** method is run. This metadata is used by the **ConsumerCoordinator**
  - **ConsumerCoordinator** -> This takes care of assigning automatic and  dynamic partition assignment and updating the **subscriptionstate** object and commiting the offsets.         
          - This also updates the **SubscriptionState** with the commited offset value so that the **Fetcher** will know what is read already and what is not read. Based on this value the fetcher will poll the records from the kafka topic.

- The **poll** method takes in a time value which is a timeout setting. The time the **Consumer Network Client** will poll the broker for new
messages before returning.

```
assignConsumer.poll(Duration.ofMillis(100))
```

  - After the **poll()** method is returned then there are a batch of records available in an in-memory buffer in fetcher where they are parsed, deserialized and grouped as ConsumerRecords by topic and parition.
  - Once the above process is completed then the objects are returned for processing in the consumer.


### Consumer Offset
- last-commited-offset:
  - Represents the bookamrk of the records that the consumer had read already.
- By default the consumer offsets are managed automatically by Kafka Consumer based on the below properties.

```
enable.auto.commit = true
auto.commit.interval=5000(ms)
auto.offset.reset=latest
```
- What are the problems with auto commiting offsets ?
  - A record might be committed before the actual processing of the record is completed.
  - With autocommit enabled, a call to poll will always commit the last offset returned by the previous poll

### Commit and Offset
  - One of Kafka’s unique characteristics is that it does not track acknowledgments from consumers the way many JMS queues do. Instead, it allows consumers to use Kafka to track their position (offset) in each partition.  
  -  All the Consumer Offsets are persisted in the a special topic named **__consumer_offsets**.

#### Storing the Offset

- Kafka stores all the consumer offsets in a topic named **__consumer_offsets**.
  - It has 50 partitions.
  - **ConsumerCoorinator** takes care of producing the data to the **__consumer_offsets**.

### Manual Consumer Offset Management

#### CommitSync()

- This is one of the way of manually committing the offset.
- By using commitSync you can get more control of processing the records and manually **commiting** the offset when the records are successfully processed.
- By manually controlling the offset you will have an impact in the **throughput** and **performance** over **consistency**.
- This can add a latency to the overall polling process.
  - Basically the consumer will be blocked until the response is received from the broker.
  - commitSync() retries committing as long as there is no error that can’t be recovered

```
public void subscribe(){
        Map<String,String> subscribeProps = propsMap();
        subscribeProps.put(ConsumerConfig.GROUP_ID_CONFIG, "subscribeconsumer");
        KafkaConsumer subscribeConsumer = new KafkaConsumer(subscribeProps);
        subscribeConsumer.subscribe(Arrays.asList(TOPIC));
        try{
            while(true){
                ConsumerRecords<String, String> records =  subscribeConsumer.poll(Duration.ofMillis(100));
                records.forEach((record) -> {
                    logger.info("Subscribe Consumer record is : "+ record);
                });
                subscribeConsumer.commitSync();
            }

        }catch (CommitFailedException e){
            logger.info("CommitFailedException Occurred {}", e);
        }catch (Exception e){
            logger.info("Exception Occurred {}", e);
        }
        finally {
            subscribeConsumer.close();
        }
    }
```


#### CommitASync()

- This is non blocking and non deterministic.
- No retries are allowed in **commitAsync()**
- The **commitAsync** has a callback through which you can implement the retry logic.
- With this option you wont be loosing anything from the performance and throughput perspective.
- This is not a recommended option at all.

- Both the **commitSync()** and **commitAsync()** invokes the Consumer-Coordinator which commits the offset to the **consumer_offsets** topic.


#### Combining Synchronous and Asynchronous Commits

- Combining both is one of the better option.
- Sample code below:

```
try {
    while (true) {
        ConsumerRecords<String, String> records = consumer.poll(100);
        for (ConsumerRecord<String, String> record : records) {
            System.out.printf("topic = %s, partition = %s, offset = %d,
                customer = %s, country = %s\n",
                record.topic(), record.partition(),
                record.offset(), record.key(), record.value());
        }
        consumer.commitAsync(); 1
    }
} catch (Exception e) {
    log.error("Unexpected error", e);
} finally {
    try {
        consumer.commitSync(); 2
    } finally {
        consumer.close();
    }
}
```

### When to Manage your own offsets

- When consistency is really mandatory for your application and you want to take control of processing the records one by one.
- Atomicity
  - Exactly once semantics.

### Consumer-Groups

- Consumer instances working together as a team forms consumer group.
- It shares the message consumption and processing the load
  - Parallelism and throughput
  - Perfomance of processing the message are improved.

### Consumer Group Co-Ordinator
- Behind the scenes a broker from the cluster is designated as a **Group-Coordinator**.
  - It maintains and monitor the consumer group membership.
- The group-coordinator also interacts with the zookeeper and consumer coordinator for any new changes to the topic that it needs to act on.
- From the time the consumer group is formed, each consumer in the consumer group sends heartbeats in an interval

```
heartbeat.interval.ms=3000 // heartbeats are sent every 3seconds
session.timeout.ms = 30000 // the group-coordinator will wait until this time to until the heart beat is received from the consumer. If the heart beat is not received then it will mark the consumer as dead.
```  

#### Why/When HeartBeats are sent ?
- Consumers maintain the membership in a consumer group and ownership of the partitions by periodically sending the heartbeats.
- Heartbeats are sent when the consumer polls or when the consumer commits the offset for older versions before (0.10.1).
  - If the heart beats are not sent then the consumer is not considered dead and the group co-ordinator triggers a rebalance.
  - HeartBeat Configuration is available in the folloing [link](https://github.com/code-with-dilip/kafka/blob/master/learn-kafka/README.md)


#### Consumer Rebalance
- Moving partition ownership from one consumer to another is called a **Rebalance**.
- If the consumer is dead then the Group Co-Ordinator takes care of rebalancing the consumers by assigning the partitions to the available consumer instance.
  - This is where offset management is really critical. If the offsets are not managed properly then there is a possibility it might re-process the records.
- If any new partition is added then during that scenario the consumer rebalance will happen.
- During Rebalance the consmer wont be able to process any records. Once the rebalance is successful it will start to process the records from the kafka topic.

#### Adding a new Consumer
- When an extisting parition is assigned to a new consumer because of a rebalance. In this case the consumer has no clue about where to read the data from. The **SubscriptionState** object has the information about the last committed offset and the newly joined consumer leverage this to read the data from the parition.

#### Role of Group CoOrdinator

- Guarantees 1:1 consumer to partition ratio.
- Initiates the Rebalancing Protocol
  - Any topic Changes(Adding or Removing Partitions)
  - Consumer Failure

### Consumer Configuration

- **fetch.min.bytes**  
  - This ensures that the minimum number of bytes are returned to the consumer from the broker.
  - This makes sure the calls between the broker and the consumer is not very frequent.

- **fetch.max.wait.ms**
  - This makes sure the maximum time the the fetch request will wait incase of no data.
  - Default value is **500ms**
- **heartbeat.interval.ms**
  - The interval in which the heartbeats are sent.
- **Max.parition.fetch.bytes**
  - Default value is 1 MB.
- **Session.timeout.ms**
  - The amount of time the consumer can go out of contact with the broker.
  - The **heartbeat.interval.ms** is the time the heartbeats are sent to the broker from the consumer
- **max.poll.interval.ms**  
  - The maximum time the consumer can go without invoking the **poll()** method.
  - The default value is 300000 ms(5 minutes).
- **AUTO.OFFSET.RESET**
  - **latest** is the default
    - Read the new records from the partition
  - **earliest** - read from the beginning of the partition.
- **enable.auto.commit**
    - This parameter controls whether the offsets are committed automatically.
    - **auto.commit.interval.ms** -> This parameter controls how often offsets are committed.
- **PARTITION.ASSIGNMENT.STRATEGY**
  - A class called **PartitionAssignor** takes care of assigning the partitions.
  - There are two strategies available - **Range** and **RoundRobin**
    - Range -> Default Strategy
      - Subset of partitions are assigned.
    - RoundRobin
      - It takes a round robin approach to assign the partitions to the consumer.
- **MAX.POLL.RECORDS**
  - Maximum number of records that a single poll() call will return.  


### Rebalance Listeners
- This is used when you want to implement some custom logic when the consumer leaves a consumer group or a consumer gets added to the consumer group.
- You can implement this functionality by implementing the **ConsumerRebalanceListener** interface.

- Check the code in the **RebalanceHandler** of the learn-java codebase.

- Consumer Position Control
  - seek()
  - seekToBeginning()
  - seekToEnd()
- Flow control
  - pause()
  - resume()
- Rebalance Listeners  

### Message Compression of messages


## Kafka Internals

### Cluster Membership
- Kafka uses **Zookeeper** for maintaining cluster membership.
- Each broker has a unique id.
- Everytime the broker starts it registers itself using this id.
- It creates a **znode(ephemeral)** for each broker(using the **broker id**) thats connected with zookeeper. The znode is alive as long as the broker is active.

### The Controller

- The controller is one of the broker. In addition to the usual broker functionality it takes care of assigning the partition leaders.
  - There can be only one **controller** at any given time.
- The first broker that starts in the cluster becomes the controller by creating an **ephemeral node** in ZooKeeper called **/controller**.
- The brokers create a **Zookeeper watch** on the controller node so they get notified of changes to this node.
- When the controller goes down then the **epheermal node** goes down, the other brokers get notified and the other brokers will try to create the controller themselves and one will be successful and the others will create a **Zookeeper watch**.

#### Re-Election of Leader(One of the broker goes down)
- When the controller notices a broker left the cluster (by watching the **Zookeepers Path**), it knows that it needs to elect a new leader for the existing partitions and let the other brokers know about the new leader and the followers need to follow the new leader.
- The new leader knows that they need to start serving the producer and consumer requests for that particular parition.

#### Controller Summary
- Kafka uses Zookeeper’s ephemeral node feature to elect a controller and to notify the controller when nodes join and leave the cluster.
- The controller is responsible for electing leaders among the partitions and replicas whenever it notices nodes join and leave the cluster

### Replication
- Replication is at the heart of Kafka’s architecture
- Kafka is often described as **a distributed, partitioned, replicated commit log service**.
- Kafka guarantees availability and durability when individual nodes inevitably fail.
- Kafka is organized by TOPICs
  - Each topic is partitioned, and each partition can have multiple replicas.
- There are two types of replica:
  - **leader replica:**
    - This holds the replica of the data sent to that partition by the producer and consumer.
    - All the consumer and producer requests for that partition goes through the Leader.
    - This replica makes sure all the follower replicas are up to date. The leader marks the replica as **out of sync** if it had not received any request for 10 seconds.
  - **follower replica:**
    - This holds the replication of data from the leader. These don't serve the client requests directly.
    - Follower replica should stay upto date with the leader. In the event of a crash of the leader one of the follower becomes the leader.
    - Out Of Sync Replica:
      - A replica is considered to be out of sync replica if it had not requested for data for about 10 seconds.
      - An Out of Sync replica can never be leader.
    - In Sync Replica
          - A follower replica which is upto date with the leader is called in sync replica.

### Request Processing
- Kafka does the below processing:
  - process requests sent to the partition leaders from clients,
  - partition replicas,
  - controller.
- Kafka has a binary protocol (over TCP) that specifies the format of the requests and how brokers respond to them—both when the request is processed successfully or when the broker encounters errors while processing the request.
- All requests have a standard header
  - Request Type
  - Request Version
  - Correlation ID
  - Client Id
  - Example message is below.
  ```
  Sending PRODUCE {acks=1,timeout=30000,partitionSizes=[test-topic-0=71]} with correlation id 3 to node 0
  ```
- Both produce requests and fetch requests have to be sent to the leader replica of a partition.
  - The client performs a metadata request type which has information about:
    - Leader replica
    - follower replica and
    - Partitions in the topic.
-   MetaData request can be sent to any brokers because all the brokers have information about the each other and its cached.
- Typically its cached in the client end also. It periodically refreshes the metadata and using the **metadata.max.age.ms** settings.

#### Produce Requests
- When the leader of the partition receives the request, it runs through certain validations:
  - Does the client have the priviliges to make the request?
  - Is the number of acks specified in the request is valid?
  - If acks is set to **all** then do we have enough **in-sync** replicas.
- Once the above are validated then it writes the data in to the disk.
- Once the data is written to the leader replica then it checks the **acks** config and respond accordingly.
  - If **acks** is set as 0 or 1 then the response will be sent immediately.
  - If **acks** is set as **all** then this will wait until all the follower replicas have replicated the data. The request will be stored in a memory called **purgatory** until the leader observes the followers have replicated it.

#### Fetch Requests
- Fetch requests work similar as the **producer** requests. The calls will go to the leader of the parition.
- How much of data and from which offset the data is needed is represented in the request itself.
- It runs a set of validations once the request is received:
    - Does the client have the necessary credentials to perform the request ?
    - Is the requested offset present in the parition?.
-   Kafka uses the **Zero-Copy** method to send the messages to the clients.
  - This means kafka reads the message directly to the network channel. It does not buffer anything.
- Clients only read the messages that are written to the **insync** replicas.
  - This is to make sure that the client won't see an inconsistent behavior if the leader crashes.

#### Other Requests
- There are 20 different requests that are available in kafka. We just explored a few :
  - FetchRequest,
  - Produce Request and
  - MetaDataRequest  
### Physical Storage

- The basic physical unit of storage is the **replica**   
- The **log.dirs** holds the directory where the partition replica will be written.

#### Parition Allocation
- The paritions are assigned to the broker in an intelligent way so that the replicas are present in the other brokers.
- If racks are involved then the replica will be present in the different different rack al-together.

#### File Management
- Kafka is not going to retain the data forever. It retains the data based on the configuration period.
- All the messages are written to a log file and the directory information is present in **log.dirs** directory.
- The file is normally split in to **segments** so that it is easy to purge the data when data timeframe is greater than the retention time.
  - By Default, the segment size is 1 GB or a week of data.
- The current segment that the partition writes to is the **active** segment.

#### File Format

- Check the book for this one.

#### Indexes
- Kafka maintaines an index for each partition,
- The index maps to segment file and then map to the position inside the file.
- With the help of index its really easy to purge the data

### Compaction
- Kafka will store messages for a set amount of time and purge messages older than the retention period.
- Kafka supports retention time as **compact**
  - This means that stores the most recent value for a given key in the topic.
- Key cannot be null for topics that are compacted.

#### How Compaction is performed:
- When Kafka is started and the **log.cleaner.enabled** flag as true.
  - This starts two sets of threads:
    - Compaction Manager Thread
    - Number of Compaction threads
  - These are responsible for performing compaction related tasks.
- To Compact a partition, the cleaner thread reads the dirtiest part of the partition and loads the key in to the map and the offset of the previous message that had the same key.
- Once the map is built, the cleaner thread starts reading the clean section of the partition and checks the map has the same key.
  -  If the same key exists the it replaced the one with the offset fm the latest map.
- Messages are eligble for compaction only on inactive segments.    
