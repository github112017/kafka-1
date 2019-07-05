package com.learnkafkaconnect.connector;

import com.learnkafkaconnect.config.FileStreamSourceConnectorConfig;
import com.learnkafkaconnect.task.FileStreamSourceTaskV2;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileStreamSourceConnectorV2 extends SourceConnector {

    private FileStreamSourceConnectorConfig fileStreamSourceConnectorConfig;

    @Override
    public void start(Map<String, String> props) {

        fileStreamSourceConnectorConfig = new FileStreamSourceConnectorConfig(props);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return FileStreamSourceTaskV2.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<>(1);
        // Only one input partition makes sense.
        configs.add(fileStreamSourceConnectorConfig.originalsStrings());
        return configs;
    }

    @Override
    public void stop() {

    }

    @Override
    public ConfigDef config() {
        return FileStreamSourceConnectorConfig.conf();
    }

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }
}
