package com.learnkafka.service

import com.learnkafka.exception.MessageNoRetryException
import mu.KLogging
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class MessageService {


    @Throws(RuntimeException::class)
    fun processMessage(message : String) {
        val poisonousRecord = "5"
        val noRetryPoisionousRecord = "6"

        when (message) {
            poisonousRecord -> throw RuntimeException("Exception Throwm")
            noRetryPoisionousRecord -> throw MessageNoRetryException("No Retry Exception")
            else -> {
                logger.info("Successfully processed the record : $message")
            }
        }
    }

    companion object : KLogging()
}