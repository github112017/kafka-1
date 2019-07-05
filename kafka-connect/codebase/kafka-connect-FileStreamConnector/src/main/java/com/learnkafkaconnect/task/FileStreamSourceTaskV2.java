package com.learnkafkaconnect.task;

import com.learnkafkaconnect.constants.FileStreamConstants;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.learnkafkaconnect.constants.FileStreamConstants.FILENAME_FIELD;
import static com.learnkafkaconnect.constants.FileStreamConstants.POSITION_FIELD;

public class FileStreamSourceTaskV2 extends SourceTask {

    private static final Logger log = LoggerFactory.getLogger(FileStreamSourceTaskV2.class);
    private static final Schema VALUE_SCHEMA = Schema.STRING_SCHEMA;

    private String filename;
    LineNumberReader lineNumberReader =  null;
    private String topic;

    @Override
    public String version() {
        return null;
    }

    @Override
    public void start(Map<String, String> props) {
        filename = props.get(FileStreamConstants.FILENAME_FIELD);
        try {
            lineNumberReader = openOrThrowError(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("Exception occured in start : " + e);

        }
        topic = props.get(FileStreamConstants.TOPIC_CONFIG);
    }

    private LineNumberReader openOrThrowError(String fileName) throws FileNotFoundException {
        return new LineNumberReader(new FileReader(fileName));
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {

        log.info("Inside poll");
        try {
            ArrayList<SourceRecord> records = new ArrayList<>();
            Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(FILENAME_FIELD, filename));
            log.info("Offset is : " + offset.get(POSITION_FIELD));
            String currentLine;
            while ((currentLine = lineNumberReader.readLine())!=null && records.isEmpty()) {
                if (currentLine != null) {
                    int lineNumber = lineNumberReader.getLineNumber();
                    log.info("Line Number Read is : " + lineNumber);
                    records.add(new SourceRecord(sourcePartitionn(filename), sourceOffset(lineNumber), topic, Schema.STRING_SCHEMA, currentLine));
                } else {
                    Thread.sleep(1);
                }
            }
            return records;
        } catch (IOException e) {
            // Underlying stream was killed, probably as a result of calling stop. Allow to return
            // null, and driving thread will handle any shutdown if necessary.
            log.error("Exception Occurred in poll : " + e);
        }
        return null;
    }

    private Map<String, String> sourcePartitionn(String filename) {
        return Collections.singletonMap(FILENAME_FIELD, filename);
    }

    private Map<String, Integer> sourceOffset(Integer lineNumber) {
        return Collections.singletonMap(POSITION_FIELD, lineNumber);
    }

    @Override
    public synchronized void stop() {
        try {
            log.info("Inside stop");
            lineNumberReader.close();
        } catch (IOException e) {
            log.error("Exception occured in stop : " + e);
        }
    }
}
