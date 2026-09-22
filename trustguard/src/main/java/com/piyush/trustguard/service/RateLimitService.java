package com.piyush.trustguard.service;

import com.piyush.trustguard.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService
{
    private final ProxyManager<String> proxyManager;
    private final int capacity;
    private final int refillTokens;
    private final Duration refillDuration;

    public RateLimitService(
            ProxyManager<String> proxyManager,
            @Value("${trustguard.rate-limit.capacity}") int capacity,
            @Value("${trustguard.rate-limit.refill-tokens}") int refillTokens,
            @Value("${trustguard.rate-limit.refill-duration}") Duration refillDuration)
    {
        this.proxyManager = proxyManager;
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillDuration = refillDuration;
    }

    public void checkRateLimit(String userId)
    {
        String key = "trustguard:ratelimit:user:" + userId;

        Bucket bucket =
                proxyManager.getProxy(
                        key,
                        () -> BucketConfiguration.builder()
                                .addLimit(
                                        Bandwidth.builder()
                                                .capacity(capacity)
                                                .refillGreedy(
                                                        refillTokens,
                                                        refillDuration
                                                )
                                                .build()
                                )
                                .build()
                );

        ConsumptionProbe probe =
                bucket.tryConsumeAndReturnRemaining(1);

        if (!probe.isConsumed())
        {
            long retryAfterSeconds =
                    Math.max(
                            1,
                            Duration
                                    .ofNanos(
                                            probe.getNanosToWaitForRefill()
                                    )
                                    .toSeconds()
                    );

            throw new RateLimitExceededException(
                    retryAfterSeconds
            );
        }
    }
}