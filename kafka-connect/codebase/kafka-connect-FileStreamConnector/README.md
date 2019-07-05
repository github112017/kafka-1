# FileStreaamConnector

## Build Connector

```youtrack
./gradlew fatJar
```

## Moving the Jar to the Kafka Distribution

```youtrack
 cp /Users/z001qgd/Dilip/study/Tech-Reference-Guide/codebase/kafka/kafka-connect/codebase/kafka-connect-FileStreamConnector/build/libs/kafka-connect-FileStreamConnector-all-1.0-SNAPSHOT.jar $CONFLUENT_HOME/share/java/kafka-connect-file-stream/
```

## Launch Kafka Connector with the new Connector

### Standalone Mode

```youtrack
$CONFLUENT_HOME/bin/connect-standalone $CONFLUENT_HOME/etc/kafka/connect-standalone.properties /Users/z001qgd/Dilip/study/Tech-Reference-Guide/codebase/kafka/kafka-connect/codebase/kafka-connect-FileStreamConnector/config/FileStreamConnector.properties 
```