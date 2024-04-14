package com.example.coroutineredislock

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.redisson.api.RedissonClient
import org.redisson.api.RedissonReactiveClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisService(
    private val redissonReactiveClient: RedissonReactiveClient,
    private val redissonClient: RedissonClient
) {
    suspend fun get(key: String): String? {
        return redissonReactiveClient.getBucket<String>(key).get().asFlow().firstOrNull()
    }

    suspend fun set(key: String, value: String) {
        redissonReactiveClient.getBucket<String>(key).set(value).asFlow().firstOrNull()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun distributedReactiveLockTest() {
        withContext(Dispatchers.IO.limitedParallelism(1)) {
            val key = "COMMON_LOCK_KEY"

            (1..5).map {
                launch {
                    println("try$it     ${Thread.currentThread().id}   ${Thread.currentThread().name}")
                    val lock = redissonReactiveClient.getLock(key)
                    try {
                        val acquired = lock.tryLock(20, 200, TimeUnit.SECONDS).asFlow().firstOrNull()
                        if (acquired == true) {
                            println("try$it     ${Thread.currentThread().id}   ${Thread.currentThread().name}")
                            println("try$it lock acquired")
                            delay(100L)
                        } else {
                            println("lock acquired failed")
                        }
                    } finally {
                        println("try$it     ${Thread.currentThread().id}   ${Thread.currentThread().name}")
                        lock.unlock().awaitFirstOrNull()
                        println("try$it lock released")
                    }
                }
            }.joinAll()
        }
    }

    suspend fun distributedLockTest() {
        withContext(Dispatchers.IO.limitedParallelism(3)) {
            val key = "COMMON_LOCK_KEY"

            (1..10).map {
                launch {
                    println("try$it     ${Thread.currentThread().id}   ${Thread.currentThread().name}")
                    val lock = redissonClient.getLock(key)
                    try {
                        val acquired = lock.tryLock(20, 200, TimeUnit.SECONDS)
                        if (acquired == true) {
                            println("try$it     ${Thread.currentThread().id}   ${Thread.currentThread().name}")
                            println("try$it lock acquired")
                            delay(100L)
                        } else {
                            println("lock acquired failed")
                        }
                    } finally {
                        println("try$it     ${Thread.currentThread().id}   ${Thread.currentThread().name}")
                        lock.unlock()
                        println("try$it lock released")
                    }
                }
            }.joinAll()
        }
    }

    suspend fun lock(key: String) {
        val lock = redissonReactiveClient.getLock(key)
        val acquired = lock.tryLock(20, 200, TimeUnit.SECONDS).asFlow().firstOrNull()
        if (acquired == true) {
            println("")
        } else {
            println(acquired)
        }

    }
}