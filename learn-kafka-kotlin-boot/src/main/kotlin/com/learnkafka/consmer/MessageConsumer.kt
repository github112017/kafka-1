package com.learnkafka.consmer

import com.learnkafka.exception.MessageNoRetryException
import com.learnkafka.service.MessageService
import mu.KLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.lang.Long

@Component
class MessageConsumer(@Autowired val messageService: MessageService,
                      @Autowired val consumerRetryListener: MessageConsumerRetryListener) {

    @Value("\${spring.kafka.retry.generate-alert-retry-threshold}")
    lateinit var maxRetries: Long

    @KafkaListener(id = "test-topic", topics = ["\${spring.kafka.consumer.topic}"],containerFactory = "deliveryConsumerContainerFactory")
    fun onMessage(consumerRecord: ConsumerRecord<String, String>, acknowledgement: Acknowledgment) {

        try {
            logger.info("Record in onMessage : " + consumerRecord.value())
            messageService.processMessage(consumerRecord.value())
        } catch (ex: MessageNoRetryException) {
            logger.error("MessageNoRetryException in onMessage : ", ex)
        }catch (ex: Exception) {
            logger.error("Exception in onMessage : ", ex)
            throw ex
        } finally {
            acknowledgement.acknowledge()
            logger.info("Retry Count is ${consumerRetryListener.retryCount}.")
            invokeRecovery(consumerRecord)
            logger.info("OffSet Commmited! Message Partition is ${consumerRecord.partition()} and the offset Value is :  ${consumerRecord.offset()}")
        }

    }


   fun invokeRecovery(consumerRecord: ConsumerRecord<String, String>){
        if(consumerRetryListener.retryCount == maxRetries.toInt()){
            messageService.processRecovery(consumerRecord)
        }
    }
    companion object : KLogging()
}