package com.learnkafka.exception

import java.lang.RuntimeException

class MessageRetryException(val msg: String) : RuntimeException() {
}