package com.learnkafka.consmer

import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter
import org.springframework.retry.RecoveryCallback
import org.springframework.retry.support.RetryTemplate

class MessageRetryConsumer(messageListener: MessageListener<String, String>?, retryTemplate: RetryTemplate?, recoveryCallback: RecoveryCallback<out Any>?) : RetryingMessageListenerAdapter<String, String>(messageListener, retryTemplate, recoveryCallback) {
}