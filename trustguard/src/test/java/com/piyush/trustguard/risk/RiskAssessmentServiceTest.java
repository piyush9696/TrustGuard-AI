package com.piyush.trustguard.risk;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RiskAssessmentServiceTest
{
    private final RiskAssessmentService service =
            new RiskAssessmentService();

    @Test
    void shouldCalculateHighRisk()
    {
        int result = service.calculateRiskScore(75, 0.98,true);

        assertEquals(99, result);
    }

    @Test
    void shouldCalculateZeroRisk()
    {
        int result = service.calculateRiskScore(0, 0.0,false);

        assertEquals(0, result);
    }

    @Test
    void shouldCombineRuleAndAiScore()
    {
        int result = service.calculateRiskScore(30, 0.50,true);

        assertEquals(44, result);
    }

    @Test
    void shouldNotTreatSafeConfidenceAsRisk()
    {
        int result =
                service.calculateRiskScore(0, 0.99, false);

        assertEquals(0, result);
    }
}
