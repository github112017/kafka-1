# Kafka Connect

- Kafka Connect provides streaming integrations between data stores and Kafka.
- Kafka Connect Includes
  - Connectors  - Jar files that defines how to integrate with Data Store itself.
  - Converters  - Handling Serialization and DeSerialization
  - Transforms  - Inflight Manipulation of messages.

## Why Kafka Connect ?
- Makes is easy to import the data from Kafka to some other data sources(Postgres, Cassandra, Mongo)
- Makes is easy to import the data from data sources(Postgres, Cassandra, Mongo) to Kafka.
- It consists of connectors to get the data from Kafka to other sources and vice versa.
- Kafka Connect is recommended for the realtime ETL pipeline.

## Concepts:

- Kafka Connect has workers which is nothing but a java process.
- Kafka connect has multiple connectors. (Choose one according to your need).
  - Each connector is a jar file.
- Kafka Connect + User Configurations creates something called **tasks**.
  - A task is linked to a Connector Configuration.
  - A job configuration spawns multiple tasks.
- Tasks are  executed by Kafka Connect workers.
  - A worker is a single Java process.

**Kafka Source Connector**:
- This refers to connecting to the data from the data source(like db or file) to the Kafka

**Kafka Sink Connector**:
- This takes the data from Kafka to the external data source.

## How Converters/Seraialzation works in Kafka Connect?

- Check this below link which has more information.

https://www.confluent.io/blog/kafka-connect-deep-dive-converters-serialization-explained

## Kafka Connect standalone mode

- A single java process runs your connectors and tasks.
- Configuration is bundled with your process. You need to pass the configuration as part of the kafka connect launch command itself.
- Used for development.
- No fault tolerant, no scalability. Hard to Monitor.

## Kafka Connect distributed mode
- Multiple workers run your connectors and tasks.
- Configurations is submitted by REST API.
- Easy to scale and its fault tolerant.

## Kafka Connect REST APIs

- The APIs can be found in the following link - https://docs.confluent.io/current/connect/references/restapi.html

#### Create a Source Connector

- The location of the file should be in the base path of the Kafka download.

```
curl -d '{"name": "local-file-source","config": {"connector.class": "FileStreamSource","tasks.max": 1,"file": "test-distributed.txt","topic": "connect-test"}}' \
  -H "Content-Type: application/json" \
  -X POST "http://localhost:8083/connectors"
```

#### Delete a connector
- Pass the connector you want to delete as part of the path param.

```
curl -X DELETE "http://localhost:8083/connectors/local-file-source"
```

#### Get All connector

- Return all the connectors in the **Connect Cluster**.
```
curl http://localhost:8083/connectors
```

#### Update the connector

- Example update payload

```
{
    "connector.class": "FileStreamSource",
    "tasks.max": "2",
    "topic": "connect-test",
}
```
- Curl command to update the connector.
```
curl -d '{"connector.class": "FileStreamSource","tasks.max": "2","topic": "connect-test"}' \
  -H "Content-Type: application/json" \
  -X PUT "http://localhost:8083/connectors/local-file-source/config"
```

#### Status of the Connector

```
curl http://localhost:8083/connectors/jdbc-sink/status
```
#### Restart the Connector

```
curl -X POST http://localhost:8083/connectors/jdbc-sink/restart
```

## Running Kafka Connect

- Download the connect from Confluent.

- Set the path of the like below in your mac.

```
export CONFLUENT_HOME="/Users/z001qgd/Dilip/software/confluent-5.2.1"
```

### Running Connect in StandAlone Mode
- Running Connect in your local
  - **connect-standalone** - Represents the standalone kafka connect server.
  - **connect-standalone.properties** - Represents the kafka connect stand alone properties such as bootstrap server, OffsetCommit interval.
  - **connect-file-source.properties** - It has the File Source information.
    - It has information like file it needs to read the data from.
    - The topic it needs to write the data to.

-   Launch the connect cluster using the below command
```
$CONFLUENT_HOME/bin/connect-standalone $CONFLUENT_HOME/etc/kafka/connect-standalone.properties $CONFLUENT_HOME/etc/kafka/connect-file-source.properties
```

### Running Connect in Distributed Mode

- Launch Kafka Cluster first.

```
./zookeeper-server-start.sh .fig/zookeeper.properties
```

```
./kafka-server-start.sh ../config/server.properties
```

- Console Consumer to read the messages from the topic.

```
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic connect-configs --from-beginning
```


- Launch the connect cluster in distributed mode using the below command.

```
$CONFLUENT_HOME/bin/connect-distributed $CONFLUENT_HOME/etc/kafka/connect-distributed.properties
```

### Source Connector

- A Source connector is used to take the data from the Data Source(File,JDBC, ElasticSearch etc.,) to Kafka.

- Create a new connector payload

```
{
    "name": "local-file-source1",
    "config": {
        "connector.class": "FileStreamSource",
        "tasks.max": 1,
        "file": "../../test-distributed.txt",
        "topic": "connect-test"
    }
}
```

#### Create a Source Connector

- The location of the file should be in the base path of the Kafka download.

```
curl -d '{"name": "local-file-source","config": {"connector.class": "FileStreamSource","tasks.max": 1,"file": "test-distributed.txt","topic": "connect-test"}}' \
  -H "Content-Type: application/json" \
  -X POST "http://localhost:8083/connectors"
```

### titions and OffSet:

- These concepts are just for **Kafka Connect** API.
- Source Partition:
  - This allows Kafka Connect to know which source you have been reading.
- Source Offset
  - This allows Kafka Connect to track until when you have been reading from the source partition.

- Topic to use for storing connector and task configurations;
```
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic connect-configs --from-beginning
```

- Topic to use for storing statuses.
```
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic connect-status --from-beginning
```

- Topic to use for storing offsets.

```
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic connect-offsets --from-beginning
```



### Sink Connector

- This is used to take the data from Kafka to the Data Source(File,JDBC, ElasticSearch etc.,).

- Create **SinkConnector** Payload.
  - One key difference to call out in the payload is the usage of **topics** json Key instead of **topic**.
```
{
    "name": "local-file-sink",
    "config": {
        "connector.class": "FileStreamSink",
        "tasks.max": 1,
        "file": "test-distributed.sink.txt",
        "topics": "connect-distributed"
    }
}
```

#### Create a File Sink Connector

- The location of the file should be in the base path of the Kafka download.

```
curl -d '{"name": "local-file-sink","config": {"connector.class": "FileStreamSink","tasks.max": 1,"file": "test-distributed-sink.txt","topics": "connect-test"}}' \
  -H "Content-Type: application/json" \
  -X POST "http://localhost:8083/connectors"
```

### JDBC

#### DB Set Up

- Connect to local DB

```
psql localdb localhost -U postgres
```

- Set the search path to the correct schema in your database.
```
ALTER ROLE postgres SET search_path TO public;
```

#### JDBC Sink Connector:

- Follow the steps that are given below, which will give you a clear picture about how to set up a **Sink Connector**.
  - Need to have postgres running in your machine
  - search path of postgres set to public.
  - Create the Kafka topic you want to read from.
  - Create a sink Connector Task.

- Create topic **kafka_orders**.

```
./kafka-topics.sh --create --topic kafka-orders -zookeeper localhost:2181 --replication-factor 1 --partitions 4
```

- The SinkConnector payload is given below.

```
{
	"name": "jdbc-sink",
	"config": {
		"connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
		"tasks.max": "1",
		"topics": "kafka_orders",
		"connection.url": "jdbc:postgresql://postgres:5432/localdb",
    "connection.user" : "postgres",
		"insert.mode": "upsert",
		"pk.mode": "kafka",
		"pk.fields": "__connect_topic,__connect_partition,__connect_offset",
		"fields_whitelist": "id,description",
		"auto.create": "true",
		"auto.evolve": "true"
	}
}
```

-  Curl command to create a new JDBC Sink connector to the Connect Cluster.

```
curl -XPOST -H "Content-type: application/json" -d '{
	"name": "jdbc-sink",
	"config": {
		"connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
		"tasks.max": "1",
		"topics": "kafka_orders",
		"connection.url": "jdbc:postgresql://postgres:5432/localdb",
    "connection.user" : "postgres",
		"insert.mode": "upsert",
		"pk.mode": "kafka",
		"pk.fields": "__connect_topic,__connect_partition,__connect_offset",
		"fields_whitelist": "id,description",
		"auto.create": "true",
		"auto.evolve": "true"
	}
}' 'http://localhost:8083/connectors'
```

- Produce message to the Kafka topic

```
./kafka-console-producer.sh --broker-list localhost:9092 --topic kafka-orders
```

- Message to publish to the kafka topic.
  - The Schema type should be struct and the field mapping is done at the fields attribute.

```
{"schema":{"type":"struct","fields":[{"type": "int32","optional": true,"field": "id"}, {"type": "string","optional": true,"field": "description"}],"optional":false},"payload":{"id":1,"description":"first_order"}}
```

#### JDBC Source Connector:
- - Follow the steps that are given below, which will give you a clear picture about how to set up a **Source Connector**.
  - Need to have postgres running in your machine
  - search path of postgres set to public.
  - Create the table you want to read from.
  - Create the Kafka topic you want to write to.
  - Create a Source Connector Task.

- The first step is to have the table ready.

```
create table foobar (c1 int, c2 varchar(255),create_ts timestamp DEFAULT CURRENT_TIMESTAMP , update_ts timestamp DEFAULT CURRENT_TIMESTAMP );
```
- Second step is to create the topic in your local cluster.

```
./kafka-topics.sh --create --topic postgres_foobar -zookeeper localhost:2181 --replication-factor 1 --partitions 4
```

- JDBC Connect Source Payload

```
{
        "name": "jdbc_source_postgres_foobar_01",
        "config": {
                "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
                "key.converter": "org.apache.kafka.connect.json.JsonConverter",
                "key.converter.schemas.enable": "false",
                "value.converter": "org.apache.kafka.connect.json.JsonConverter",
                "value.converter.schemas.enable": "false",
                "connection.url": "jdbc:postgresql://postgres:5432/localdb",
                "connection.user": "postgres",
                "table.whitelist": "foobar",
                "mode": "timestamp",
                "timestamp.column.name": "create_ts",
                "validate.non.null": "false",
                "topic.prefix": "postgres_"
        }
}
```

-  Curl command to create a new JDBC Source connector to the Connect Cluster.

```
curl -XPOST -H "Content-type: application/json" -d '{
        "name": "jdbc_source_postgres_foobar_01",
        "config": {
                "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
                "key.converter": "org.apache.kafka.connect.json.JsonConverter",
                "key.converter.schemas.enable": "false",
                "value.converter": "org.apache.kafka.connect.json.JsonConverter",
                "value.converter.schemas.enable": "false",
                "connection.url": "jdbc:postgresql://postgres:5432/localdb",
                "connection.user": "postgres",
                "table.whitelist": "foobar",
                "mode": "timestamp",
                "timestamp.column.name": "create_ts",
                "validate.non.null": "false",
                "topic.prefix": "postgres_"
        }
}' 'http://localhost:8083/connectors'
```

- Publish new data to the table.

```
insert into foobar (c1,c2) values(1,'foo');
```

### Build Custom Connector:

- In order to build a custom connector. We just need to implement two interfaces.
  - Connector
  - Task

#### FileStream

#### GitHub Source Connector

- Make sure you are in the root path of Kafka installation.

```
cd $CONFLUENT_HOME
```

- Create topic **kafka_orders**.

```
./kafka-topics.sh --create --topic github-issues -zookeeper localhost:2181 --replication-factor 1 --partitions 4
```

- Launch the Connect with the github Connector using the below command.

```
$CONFLUENT_HOME/bin/connect-standalone  /Users/z001qgd/Dilip/study/Tech-Reference-Guide/codebase/kafka/kafka-connect/codebase/kafka-connect-sourceconnector-github/config/worker.properties  /Users/z001qgd/Dilip/study/Tech-Reference-Guide/codebase/kafka/kafka-connect/codebase/kafka-connect-sourceconnector-github/config/GitHubSourceConnectorExample.properties
```

### Maintaining Offset

#### StandAlone Mode

- Pass this below information in the standalone worker properties.

```
offset.storage.file.filename=/Users/z001qgd/Dilip/software/confluent-5.2.1/share/java/kafka-connect-github/standalone.offsets
offset.flush.interval.ms=10000
```
- This takes care of writing the offset to the file given in the worker.properties in the given time interval.
