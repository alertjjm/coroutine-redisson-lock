package com.example.coroutineredislock

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/redis")
class RedisController(
    private val redisService: RedisService
) {
    @GetMapping("/{key}")
    suspend fun getValue(
        @PathVariable("key")
        key: String
    ): String? {
        return redisService.get(key)
    }

    @GetMapping("/{key}/{value}")
    suspend fun setValue(
        @PathVariable("key")
        key: String,
        @PathVariable("value")
        value: String
    ): String {
        redisService.set(key, value)
        return "ok"
    }

    @GetMapping("/start-lock-test-reactive")
    suspend fun startLockReactiveTest(): String {
        redisService.distributedReactiveLockTest()
        return "ok"
    }

    @GetMapping("/start-lock-test")
    suspend fun startLockTest(): String {
        redisService.distributedLockTest()
        return "ok"
    }

    @GetMapping("/lock/{key}")
    suspend fun startLockTest(
        @PathVariable("key")
        key: String
    ): String {
        redisService.lock(key)
        return "ok"
    }
}