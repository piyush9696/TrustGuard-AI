package com.piyush.trustguard.risk;

import org.springframework.stereotype.Service;

@Service
public class RiskAssessmentService
{
    public int calculateRiskScore(
            int ruleScore,
            double aiConfidence,
            boolean aiScam)
    {
        int cappedRuleScore =
                Math.min(ruleScore, 75);

        double normalizedRuleScore =
                (cappedRuleScore / 75.0) * 100;

        double aiScore =
                aiScam ? aiConfidence * 100 : 0;

        double finalScore =
                normalizedRuleScore * 0.6
                        + aiScore * 0.4;

        return (int) Math.round(
                Math.min(finalScore, 100)
        );
    }
}
