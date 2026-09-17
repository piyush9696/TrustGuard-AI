package com.piyush.trustguard.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.piyush.trustguard.dto.ScamAnalysisRequest;
import com.piyush.trustguard.dto.ScamAnalysisResponse;
import com.piyush.trustguard.risk.InputTypeDetector;
import com.piyush.trustguard.risk.RuleAnalysisResult;
import com.piyush.trustguard.risk.RuleBasedAnalyzer;
import com.piyush.trustguard.risk.RiskAssessmentService;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;

@Service
public class ScamAnalysisService
{
    private final Client geminiClient;
    private final ObjectMapper objectMapper;
    private final RuleBasedAnalyzer ruleBasedAnalyzer;
    private final RiskAssessmentService riskAssessmentService;
    private final InputTypeDetector inputTypeDetector;

    public ScamAnalysisService(
            Client geminiClient,
            ObjectMapper objectMapper,
            RuleBasedAnalyzer ruleBasedAnalyzer,
            RiskAssessmentService riskAssessmentService,
            InputTypeDetector inputTypeDetector)
    {
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
        this.ruleBasedAnalyzer = ruleBasedAnalyzer;
        this.riskAssessmentService = riskAssessmentService;
        this.inputTypeDetector = inputTypeDetector;
    }

    public ScamAnalysisResponse analyze(ScamAnalysisRequest request)
            throws Exception
    {
        String text = request.getText().trim();

        boolean isUrl = inputTypeDetector.isUrl(text);

        String prompt;

        if (isUrl)
        {
            prompt = """
                    You are a cybersecurity URL safety analysis assistant.

                    Analyze the following URL for signs of phishing,
                    malware, fraud, impersonation, malicious behavior,
                    or other security threats.

                    Return ONLY valid JSON.

                    The JSON MUST contain exactly these six fields:
                    - scam
                    - confidence
                    - category
                    - redFlags
                    - explanation
                    - recommendation

                    DO NOT include any other fields.

                    Rules:
                    - confidence must be between 0 and 1.
                    - scam should be true only when there is meaningful
                      evidence that the URL is malicious or unsafe.
                    - redFlags should contain the specific suspicious
                      characteristics found.
                    - category should describe the threat type, or
                      "NONE" if it does not appear to be malicious.
                    - Do not invent information that is not present.
                    - Do not include markdown or code fences.

                    URL to analyze:
                    %s
                    """.formatted(text);
        }
        else
        {
            prompt = """
                    You are a cybersecurity scam detection assistant.

                    Analyze the following message for signs of scams,
                    phishing, fraud, impersonation, malicious requests,
                    urgency tactics, or suspicious financial activity.

                    Return ONLY valid JSON.

                    The JSON MUST contain exactly these six fields:
                    - scam
                    - confidence
                    - category
                    - redFlags
                    - explanation
                    - recommendation

                    DO NOT include any other fields.

                    Rules:
                    - confidence must be between 0 and 1.
                    - scam should be true only when there is meaningful
                      evidence of scam/fraudulent behavior.
                    - redFlags should contain the specific suspicious
                      characteristics found in the message.
                    - category should describe the scam type, or
                      "NONE" if it does not appear to be a scam.
                    - Do not invent information that is not present.
                    - Do not include markdown or code fences.

                    Message to analyze:
                    %s
                    """.formatted(text);
        }

        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .responseMimeType("application/json")
                        .build();

        GenerateContentResponse response =
                geminiClient.models.generateContent(
                        "gemini-3.6-flash",
                        prompt,
                        config
                );

        String json = response.text();

        ScamAnalysisResponse analysis =
                objectMapper.readValue(
                        json,
                        ScamAnalysisResponse.class
                );

        RuleAnalysisResult ruleResult =
                ruleBasedAnalyzer.analyze(text);

        if (analysis.getRedFlags() == null)
        {
            analysis.setRedFlags(new ArrayList<>());
        }

        for (String flag : ruleResult.getRedFlags())
        {
            if (!analysis.getRedFlags().contains(flag))
            {
                analysis.getRedFlags().add(flag);
            }
        }

        int riskScore =
                riskAssessmentService.calculateRiskScore(
                        ruleResult.getScore(),
                        analysis.getConfidence(),
                        analysis.isScam()
                );

        analysis.setRiskScore(riskScore);

        return analysis;
    }
}