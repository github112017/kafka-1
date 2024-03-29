package com.learnkafkaconnect.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import java.util.Map;


public class FileStreamSourceConnectorConfig extends AbstractConfig {

    public static final String TOPIC_CONFIG = "topic";
    private static final String TOPIC_DOC = "Topic to write to";

    public static final String FILE_NAME = "fileName";
    private static final String FILE_DOC = "File to read the data from";


    public static final String BATCH_SIZE_CONFIG = "batch.size";
    private static final String BATCH_SIZE_DOC = "Number of data points to retrieve at a time. Defaults to 100 (max value)";

    public FileStreamSourceConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
        super(config, parsedConfig);
    }

    public FileStreamSourceConnectorConfig(Map<String, String> parsedConfig) {
        this(conf(), parsedConfig);
    }

    public static ConfigDef conf() {
        return new ConfigDef()
                .define(TOPIC_CONFIG, Type.STRING, Importance.HIGH, TOPIC_DOC)
                .define(FILE_NAME, Type.STRING, Importance.HIGH, FILE_DOC)
                .define(BATCH_SIZE_CONFIG, Type.INT, 5, Importance.LOW, BATCH_SIZE_DOC);
    }


    public Integer getBatchSize() {
        return this.getInt(BATCH_SIZE_CONFIG);
    }

    public String getTopic() {
        return this.getString(TOPIC_CONFIG);
    }

}
