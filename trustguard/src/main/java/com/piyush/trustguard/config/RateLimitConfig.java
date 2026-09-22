package com.piyush.trustguard.config;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig
{
    @Bean
    public RedisClient rateLimitRedisClient(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port)
    {
        RedisURI redisURI =
                RedisURI.builder()
                        .withHost(host)
                        .withPort(port)
                        .build();

        return RedisClient.create(redisURI);
    }

    @Bean
    public StatefulRedisConnection<String, byte[]> rateLimitRedisConnection(
            RedisClient rateLimitRedisClient)
    {
        RedisCodec<String, byte[]> codec =
                RedisCodec.of(
                        StringCodec.UTF8,
                        ByteArrayCodec.INSTANCE
                );

        return rateLimitRedisClient.connect(codec);
    }

    @Bean
    public ProxyManager<String> rateLimitProxyManager(
            StatefulRedisConnection<String, byte[]> connection)
    {
        return Bucket4jLettuce
                .casBasedBuilder(connection)
                .expirationAfterWrite(
                        ExpirationAfterWriteStrategy
                                .basedOnTimeForRefillingBucketUpToMax(
                                        Duration.ofMinutes(2)
                                )
                )
                .maxRetries(5)
                .build();
    }
}