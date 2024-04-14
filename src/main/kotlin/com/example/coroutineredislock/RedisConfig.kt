package com.example.coroutineredislock

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.api.RedissonReactiveClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedisConfig {
    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer().setAddress("redis://localhost:6379")
        return Redisson.create(config)
    }

    @Bean
    fun redissonReactiveClient(): RedissonReactiveClient {
        return redissonClient().reactive()
    }
}