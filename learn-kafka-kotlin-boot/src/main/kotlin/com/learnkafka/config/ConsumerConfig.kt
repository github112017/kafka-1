package com.learnkafka.config

import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory


@Configuration
@EnableKafka
class ConsumerConfig {

    @Bean
    fun deliveryConsumerContainerFactory (
            configurer: ConcurrentKafkaListenerContainerFactoryConfigurer,
            kafkaConsumerFactory: ConsumerFactory<*, *>
    ): ConcurrentKafkaListenerContainerFactory<*, *> {
        val factory = ConcurrentKafkaListenerContainerFactory<Any, Any>()
        configurer.configure(factory, kafkaConsumerFactory as ConsumerFactory<Any, Any>)
        return factory
    }
}