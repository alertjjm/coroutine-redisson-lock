package com.example.coroutineredislock

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.reactive.asFlow
import org.redisson.api.RedissonReactiveClient
import org.springframework.stereotype.Service

@Service
class RedisService(
    private val redissonReactiveClient: RedissonReactiveClient
) {
    suspend fun get(key: String): String? {
        return redissonReactiveClient.getBucket<String>(key).get().asFlow().firstOrNull()
    }

    suspend fun set(key: String, value: String) {
        redissonReactiveClient.getBucket<String>(key).set(value).asFlow().firstOrNull()
    }
}