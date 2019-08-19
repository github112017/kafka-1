package com.learnkafka.consmer

import com.learnkafka.service.MessageService
import mu.KLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class MessageConsumer(@Autowired val messageService: MessageService) {


    @KafkaListener(id = "test-topic", topics = ["\${spring.kafka.consumer.topic}"],containerFactory = "deliveryConsumerContainerFactory")
    fun onMessage(consumerRecord: ConsumerRecord<String, String>, acknowledgement: Acknowledgment) {

        try {
            logger.info("Record in onMessage : " + consumerRecord.value())
            messageService.processMessage(consumerRecord.value())
        } catch (ex: Exception) {
            logger.error("Exception in onMessage : ", ex)
            throw ex
        } finally {
            acknowledgement.acknowledge()
            logger.info("OffSet Commmited! Message Partition is ${consumerRecord.partition()} and the offset Value is :  ${consumerRecord.offset()}")
        }

    }
    companion object : KLogging()
}