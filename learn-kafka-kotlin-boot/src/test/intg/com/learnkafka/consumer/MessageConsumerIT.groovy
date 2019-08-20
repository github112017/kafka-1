package com.learnkafka.consumer

import com.learnkafka.consmer.MessageConsumer
import com.learnkafka.service.MessageService
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.producer.ProducerRecord
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.MessageListenerContainer
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.ContainerTestUtils
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import java.util.concurrent.CountDownLatch

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
@EmbeddedKafka(ports = [9092], topics = ["\${spring.kafka.consumer.topic}"])
class MessageConsumerIT extends Specification {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka

    @Autowired
    MessageConsumer messageConsumer

    @Autowired
    private KafkaTemplate<String, String> template;

    @SpringBean
    MessageService messageService = Mock()

    def "Integration test for MessageConsumer"() {

        when:
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("test-topic", "my-aggregate-id", "my-test-value")
        template.send(producerRecord)
        boolean result = true
        Thread.sleep(sleepTime)

        then:
        result == true
        count * messageService.processMessage(_)

        where:
        message | count | sleepTime
        "3"     | 1     | 3000

    }
}
