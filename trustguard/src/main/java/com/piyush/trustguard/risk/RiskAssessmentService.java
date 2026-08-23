package com.piyush.trustguard.risk;

import org.springframework.stereotype.Service;

@Service
public class RiskAssessmentService
{
    public int calculateRiskScore(int ruleScore,double aiConfidence,boolean aiScam)
    {
        double normalizedRuleScore=(ruleScore/75.0)*100;
        double aiScore= aiScam?aiConfidence *100:0;
        double finalScore=(normalizedRuleScore*0.6+aiScore*0.4);
        return (int)Math.round(finalScore);
    }
}
