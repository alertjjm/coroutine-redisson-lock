package com.example.coroutineredislock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CoroutineRedisLockApplication

fun main(args: Array<String>) {
    runApplication<CoroutineRedisLockApplication>(*args)
}
