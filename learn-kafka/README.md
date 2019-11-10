# Kafka Internals

## HeartBeats
-   Heart Beats are generally sent in a different thread and it can be found in the below thread name
 
```aidl
kafka-coordinator-heartbeat-thread
```

-   To view the heartbeat thread logger in the console we need to add the **logback.xml** to the below value.

```aidl
<root level="trace">
        <appender-ref ref="CONSOLE"/>
    </root>

``` 