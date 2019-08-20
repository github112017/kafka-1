package com.learnkafka.consumer


import com.learnkafka.consmer.MessageConsumerRetryListener
import com.learnkafka.service.MessageService
import org.apache.kafka.clients.producer.ProducerRecord
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
@EmbeddedKafka(ports = [9092], topics = ["\${spring.kafka.consumer.topic}"])
@TestPropertySource(properties = ["spring.kafka.retry.generate-alert-retry-threshold = 3" ,
        "spring.kafka.retry.backoff.initial-interval=500"])
class MessageConsumerIT extends Specification {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka

    @Autowired
    MessageConsumerRetryListener messageConsumerRetryListener

    @Autowired
    private KafkaTemplate<String, String> template;

    @SpringBean
    MessageService messageService = Mock()

    def "Integration test for MessageConsumer"() {

        when:
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("test-topic", "my-aggregate-id", message)
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

    def "Integration test for MessageConsumer - Exception Scenario"() {

        given:
        messageService.processMessage(_) >> { throw new RuntimeException("Exception thrown") }

        when:
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("test-topic", "my-aggregate-id", message)
        template.send(producerRecord)
        boolean result = true
        Thread.sleep(sleepTime)

        then:
        result == true
        retriedCount == messageConsumerRetryListener.retryCount
        1 * messageService.processRecovery(_)

        where:
        message | retriedCount | sleepTime
        "5"     | 3            | 3000

    }
}
