package com.learnkafka.config

import com.learnkafka.consmer.MessageConsumerRetryListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import java.lang.Long


@Configuration
@EnableKafka
class ConsumerConfig(@Autowired val consumerRetryListener: MessageConsumerRetryListener) {

    @Value("\${spring.kafka.retry.backoff.initial-interval}")
    lateinit var initialBackoffInterval: Long

    @Value("\${spring.kafka.retry.generate-alert-retry-threshold}")
    lateinit var maxRetries: Integer


    @Bean
    fun deliveryConsumerContainerFactory (
            configurer: ConcurrentKafkaListenerContainerFactoryConfigurer,
            kafkaConsumerFactory: ConsumerFactory<*, *>
    ): ConcurrentKafkaListenerContainerFactory<*, *> {
        val factory = ConcurrentKafkaListenerContainerFactory<Any, Any>()
        configurer.configure(factory, kafkaConsumerFactory as ConsumerFactory<Any, Any>)
        factory.setRetryTemplate(retryTemplate())
        return factory
    }

    @Bean
    fun retryTemplate(): RetryTemplate {
        val simpleRetryPolicy = getRetryPolicy()
        val fixedBackOffPolicy = getBackOffPolicy()
        val retryTemplate = RetryTemplate()
        retryTemplate.setRetryPolicy(simpleRetryPolicy)
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy)
       retryTemplate.registerListener(consumerRetryListener)// This listener takes care of listening to the activity and logs the necessary events.
        return retryTemplate
    }

    /**
     * SimpleRetryPolicy sets the number of times the retry will happen.
     * @return
     */
    fun getRetryPolicy(): SimpleRetryPolicy {
        val simpleRetryPolicy = SimpleRetryPolicy()
        simpleRetryPolicy.maxAttempts = maxRetries.toInt()
        return simpleRetryPolicy
    }

    /**
     * FixedBackOffPolicy sets the interval between the retry.
     * @return
     */
    fun getBackOffPolicy(): FixedBackOffPolicy {
        val backOffPolicy = FixedBackOffPolicy()
        backOffPolicy.backOffPeriod = initialBackoffInterval.toLong()
        return backOffPolicy
    }


}