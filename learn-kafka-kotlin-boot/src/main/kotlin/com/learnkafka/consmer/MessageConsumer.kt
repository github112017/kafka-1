package com.learnkafka.consmer

import mu.KLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class MessageConsumer {

    @KafkaListener(id = "test-topic", topics = ["\${spring.kafka.consumer.topic}"])
    fun onMessage(consumerRecord: ConsumerRecord<String, String>, acknowledgement: Acknowledgment) {

        try {
            logger.info("Record in onMessage : " + consumerRecord.value())
        } catch (ex: Exception) {
            logger.error("Exception in onMessage : ", ex)
        } finally {
            acknowledgement.acknowledge()
        }

    }

    companion object : KLogging()

}