package com.piyush.trustguard.risk;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UrlAnalyzerTest
{
    private final UrlAnalyzer analyzer = new UrlAnalyzer();

    @Test
    void shouldAcceptNormalDomain()
    {
        assertFalse(
                analyzer.isSuspicious("https://amazon.in/orders")
        );
    }

    @Test
    void shouldDetectIpAddress()
    {
        assertTrue(
                analyzer.isSuspicious("http://192.168.1.10/login")
        );
    }

    @Test
    void shouldDetectInvalidUrl()
    {
        assertTrue(
                analyzer.isSuspicious("not-a-valid-url")
        );
    }
}
