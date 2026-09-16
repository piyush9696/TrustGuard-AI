package com.piyush.trustguard.risk;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UrlAnalyzerTest
{
    private final WebRiskService webRiskService =
            mock(WebRiskService.class);

    private final UrlAnalyzer urlAnalyzer =
            new UrlAnalyzer(webRiskService);

    @Test
    void shouldDetectLegitimateUrl()
    {
        when(webRiskService.isThreatDetected("https://amazon.in/orders"))
                .thenReturn(false);

        assertFalse(
                urlAnalyzer.isSuspicious("https://amazon.in/orders")
        );
    }

    @Test
    void shouldDetectIpBasedUrl()
    {
        assertTrue(
                urlAnalyzer.isSuspicious("http://192.168.1.10/login")
        );

        verifyNoInteractions(webRiskService);
    }

    @Test
    void shouldDetectInvalidUrl()
    {
        assertTrue(
                urlAnalyzer.isSuspicious("not-a-valid-url")
        );

        verifyNoInteractions(webRiskService);
    }
}