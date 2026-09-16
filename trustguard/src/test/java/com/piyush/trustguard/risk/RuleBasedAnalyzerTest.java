package com.piyush.trustguard.risk;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RuleBasedAnalyzerTest
{
    private final WebRiskService webRiskService =
            mock(WebRiskService.class);

    private final UrlAnalyzer urlAnalyzer =
            new UrlAnalyzer(webRiskService);

    private final RuleBasedAnalyzer analyzer =
            new RuleBasedAnalyzer(urlAnalyzer);


    @Test
    void shouldDetectUrgency()
    {
        RuleAnalysisResult result =
                analyzer.analyze("URGENT! Please act now.");

        assertEquals(20, result.getScore());
        assertTrue(
                result.getRedFlags().contains("Urgency language")
        );
    }


    @Test
    void shouldDetectAccountThreat()
    {
        RuleAnalysisResult result =
                analyzer.analyze("Your account will be blocked.");

        assertEquals(25, result.getScore());
        assertTrue(
                result.getRedFlags().contains("Account threat")
        );
    }


    @Test
    void shouldDetectCredentialRequest()
    {
        RuleAnalysisResult result =
                analyzer.analyze(
                        "Please provide your password and OTP."
                );

        assertEquals(30, result.getScore());
        assertTrue(
                result.getRedFlags().contains(
                        "Credential or sensitive information request"
                )
        );
    }


    @Test
    void shouldDetectMultipleRedFlags()
    {
        RuleAnalysisResult result =
                analyzer.analyze(
                        "URGENT! Your account will be blocked. " +
                                "Please provide your OTP."
                );

        assertEquals(75, result.getScore());

        assertTrue(
                result.getRedFlags().contains("Urgency language")
        );

        assertTrue(
                result.getRedFlags().contains("Account threat")
        );

        assertTrue(
                result.getRedFlags().contains(
                        "Credential or sensitive information request"
                )
        );
    }


    @Test
    void shouldDetectSuspiciousUrl()
    {
        String url = "http://192.168.1.10/login";

        RuleAnalysisResult result =
                analyzer.analyze(
                        "Please login here: " + url
                );

        assertTrue(
                result.getRedFlags().contains("Suspicious URL")
        );

        assertTrue(result.getScore() >= 20);

        verifyNoInteractions(webRiskService);
    }


    @Test
    void shouldNotFlagLegitimateUrl()
    {
        String url = "https://amazon.in/orders";

        when(webRiskService.isThreatDetected(url))
                .thenReturn(false);

        RuleAnalysisResult result =
                analyzer.analyze(
                        "Please check your order here: " + url
                );

        assertFalse(
                result.getRedFlags().contains("Suspicious URL")
        );

        assertEquals(0, result.getScore());

        verify(webRiskService).isThreatDetected(url);
    }


    @Test
    void shouldDetectThreatFromWebRisk()
    {
        String url = "https://example.com";

        when(webRiskService.isThreatDetected(url))
                .thenReturn(true);

        RuleAnalysisResult result =
                analyzer.analyze(
                        "Check this link: " + url
                );

        assertTrue(
                result.getRedFlags().contains("Suspicious URL")
        );

        assertEquals(20, result.getScore());

        verify(webRiskService).isThreatDetected(url);
    }


    @Test
    void shouldReturnZeroForSafeMessage()
    {
        RuleAnalysisResult result =
                analyzer.analyze(
                        "Hello, hope you are having a great day."
                );

        assertEquals(0, result.getScore());
        assertTrue(result.getRedFlags().isEmpty());
        verifyNoInteractions(webRiskService);
    }
}