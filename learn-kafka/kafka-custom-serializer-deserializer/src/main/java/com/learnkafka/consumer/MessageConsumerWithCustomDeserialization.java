package com.learnkafka.consumer;

import com.learnkafka.deserializer.CustomMessageDeserializer;
import com.learnkafka.domain.Message;
import com.learnkafka.serializer.CustomMessageSerializer;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

import static com.learnkafka.constants.MessageConstants.TOPIC;
import static com.learnkafka.consumer.KafkaConsumerProps.propsMap;

public class MessageConsumerWithCustomDeserialization {

    private static final Logger logger = LoggerFactory.getLogger(MessageConsumerWithCustomDeserialization.class);

    public void subscribe(){
        Map<String,String> subscribeProps = propsMap();
        subscribeProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CustomMessageDeserializer.class.getName());
        subscribeProps.put(ConsumerConfig.GROUP_ID_CONFIG, "subscribeconsumer");
        subscribeProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "10000");
        KafkaConsumer subscribeConsumer = new KafkaConsumer(subscribeProps);
        subscribeConsumer.subscribe(Arrays.asList(TOPIC), new RebalanceHandler());
        try{
            while(true){
                ConsumerRecords<String, Message> records =  subscribeConsumer.poll(Duration.ofMillis(100));
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

    public static void main(String[] args) {
        new MessageConsumerWithCustomDeserialization().subscribe();
    }
}
