package com.learnkafka.consumer

import com.learnkafka.consmer.MessageConsumer
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
import spock.util.concurrent.PollingConditions

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
@EmbeddedKafka(ports = [9092], topics = ["\${spring.kafka.consumer.topic}"])
@TestPropertySource(properties = ["spring.kafka.retry.generate-alert-retry-threshold = 4",
        "spring.kafka.retry.backoff.initial-interval=500"])
class MessageConsumerIT extends Specification {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka

    @Autowired
    MessageConsumerRetryListener messageConsumerRetryListener

    @Autowired
    private KafkaTemplate<String, String> template;

    @SpringBean
    MessageService messageServiceMock = Mock(MessageService.class)

    @Autowired
    MessageConsumer messageConsumer

    def "Integration test for MessageConsumer"() {

        given:
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("test-topic", "my-aggregate-id", message)
        def latch = new CountDownLatch(1)

        when:
        template.send(producerRecord)
        latch.await(3, TimeUnit.SECONDS)

        then:
        count * messageServiceMock.processMessage(message) >> null
        0 * messageServiceMock.processRecovery(message) >> null

        where:
        message | count
        "3"     | 1

    }

    def "Integration test for MessageConsumer - Exception Scenario"() {

        given:
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("test-topic", "my-aggregate-id", message)
        messageServiceMock.processMessage(_) >> { throw new RuntimeException("Exception thrown") }
        def latch = new CountDownLatch(1)

        when:
        template.send(producerRecord)
        latch.await(3, TimeUnit.SECONDS)

        then:
        count * messageServiceMock.processRecovery(_)

        where:
        message | count
        "5"     | 1

    }
}
