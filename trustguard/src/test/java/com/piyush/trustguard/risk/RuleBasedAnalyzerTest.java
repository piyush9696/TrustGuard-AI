package com.piyush.trustguard.risk;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class RuleBasedAnalyzerTest
{
    private final RuleBasedAnalyzer analyzer=
            new RuleBasedAnalyzer();

    @Test
    void shouldDetectUrgency()
    {
        RuleAnalysisResult result=analyzer.analyze("URGENT! Act now!");
        assertEquals(20,result.getScore());
        assertTrue(result.getRedFlags().contains("Urgency language"));
    }
    @Test
    void shouldDetectAccountThreat()
    {
        RuleAnalysisResult result =
                analyzer.analyze("Your account will be blocked.");
        assertEquals(25, result.getScore());
        assertTrue(result.getRedFlags()
                .contains("Account threat"));
    }
    @Test
    void shouldDetectCredentialRequest()
    {
        RuleAnalysisResult result =
                analyzer.analyze("Please provide your OTP.");
        assertEquals(30, result.getScore());
        assertTrue(result.getRedFlags()
                .contains("Credential or sensitive information request"));
    }
    @Test
    void shouldCombineMultipleSignals()
    {
        RuleAnalysisResult result =
                analyzer.analyze(
                        "URGENT! Your account will be blocked. Please provide your OTP."
                );
        assertEquals(75, result.getScore());
        assertEquals(3, result.getRedFlags().size());
        assertTrue(result.getRedFlags()
                .contains("Urgency language"));
        assertTrue(result.getRedFlags()
                .contains("Account threat"));
        assertTrue(result.getRedFlags()
                .contains("Credential or sensitive information request"));
    }

    @Test
    void shouldDetectMultipleRedFlags()
    {
        RuleAnalysisResult result =
                analyzer.analyze(
                        "URGENT! Your account will be blocked. Please provide your OTP."
                );

        assertEquals(75, result.getScore());

        assertEquals(3, result.getRedFlags().size());

        assertTrue(result.getRedFlags()
                .contains("Urgency language"));

        assertTrue(result.getRedFlags()
                .contains("Account threat"));

        assertTrue(result.getRedFlags()
                .contains("Credential or sensitive information request"));
    }
}
