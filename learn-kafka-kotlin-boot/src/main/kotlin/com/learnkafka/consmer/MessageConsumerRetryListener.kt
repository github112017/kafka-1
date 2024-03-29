package com.learnkafka.consmer

import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.listener.RetryListenerSupport
import org.springframework.stereotype.Component

@Component
class MessageConsumerRetryListener : RetryListenerSupport() {

    var retryCount: Int=0

    @Value("\${spring.kafka.retry.generate-alert-retry-threshold}")
    var maxRetries: Long=0

    override fun <T, E : Throwable> open(context: RetryContext?, callback: RetryCallback<T, E>?): Boolean {
        logger.info("Retry context opened")
        retryCount=0 //resetting RetryCount for Every Context
        return true
    }

    override fun <T, E : Throwable> close(context: RetryContext?, callback: RetryCallback<T, E>?, throwable: Throwable?) {
        if (context!!.retryCount == maxRetries.toInt()){
            logger.info("Retry Threshold reached")
        }
    }

    override fun <T, E : Throwable> onError(context: RetryContext?, callback: RetryCallback<T, E>?, throwable: Throwable?) {
        logger.info("Retry in onError")
        setRetryCount(context)
    }

    fun setRetryCount(context: RetryContext?){
        retryCount = context!!.retryCount // Retry index starts from 0.Retry Context is incremented.

    }

    companion object : KLogging()

}

