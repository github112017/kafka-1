package com.learnkafka.service

import com.learnkafka.exception.MessageRetryException
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class MessageService {

    @Throws(MessageRetryException::class)
    fun processMessage(message : String){
        val poisonousRecord = "5"
        if (message == poisonousRecord){
            throw MessageRetryException("Exception Throwm")
        }else{logger.info("Successfully processed the record : $message")
        }
    }

    companion object : KLogging()
}