package com.piyush.trustguard.exception;

public class RateLimitExceededException extends RuntimeException
{
    private final long retryAfterSeconds;

    public RateLimitExceededException(long retryAfterSeconds)
    {
        super("Rate limit exceeded. Please try again later.");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds()
    {
        return retryAfterSeconds;
    }
}