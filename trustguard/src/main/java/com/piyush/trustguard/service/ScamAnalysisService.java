package com.piyush.trustguard.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.piyush.trustguard.dto.ScamAnalysisRequest;
import com.piyush.trustguard.dto.ScamAnalysisResponse;
import com.piyush.trustguard.exception.AnalysisException;
import com.piyush.trustguard.risk.InputTypeDetector;
import com.piyush.trustguard.risk.RuleAnalysisResult;
import com.piyush.trustguard.risk.RuleBasedAnalyzer;
import com.piyush.trustguard.risk.RiskAssessmentService;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;

import java.util.ArrayList;

@Service
public class ScamAnalysisService
{
    private final Client geminiClient;
    private final ObjectMapper objectMapper;
    private final RuleBasedAnalyzer ruleBasedAnalyzer;
    private final RiskAssessmentService riskAssessmentService;
    private final InputTypeDetector inputTypeDetector;
    private final StringRedisTemplate redisTemplate;
    private static final String CACHE_PREFIX = "trustguard:analysis:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    public ScamAnalysisService(
            Client geminiClient,
            ObjectMapper objectMapper,
            RuleBasedAnalyzer ruleBasedAnalyzer,
            RiskAssessmentService riskAssessmentService,
            InputTypeDetector inputTypeDetector,
            StringRedisTemplate redisTemplate)
    {
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
        this.ruleBasedAnalyzer = ruleBasedAnalyzer;
        this.riskAssessmentService = riskAssessmentService;
        this.inputTypeDetector = inputTypeDetector;
        this.redisTemplate = redisTemplate;
    }


    public ScamAnalysisResponse analyze(ScamAnalysisRequest request)
    {
        String text = request.getText().trim();

        String cacheKey = buildCacheKey(text);

        try
        {
            String cachedResult =
                    redisTemplate.opsForValue().get(cacheKey);

            if (cachedResult != null)
            {
                return objectMapper.readValue(
                        cachedResult,
                        ScamAnalysisResponse.class
                );
            }
        }
        catch (Exception ignored)
        {
        }
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

        ScamAnalysisResponse analysis;

        try
        {
            GenerateContentResponse response =
                    geminiClient.models.generateContent(
                            "gemini-3.6-flash",
                            prompt,
                            config
                    );

            String json = response.text();

            if (json == null || json.isBlank())
            {
                throw new AnalysisException("AI returned an empty response");
            }

            analysis =
                    objectMapper.readValue(
                            json,
                            ScamAnalysisResponse.class
                    );
        }
        catch (AnalysisException exception)
        {
            throw exception;
        }
        catch (Exception exception)
        {
            throw new AnalysisException(
                    "Unable to complete scam analysis",
                    exception
            );
        }

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

        try
        {
            String result =
                    objectMapper.writeValueAsString(analysis);

            redisTemplate.opsForValue().set(
                    cacheKey,
                    result,
                    CACHE_TTL
            );
        }
        catch (Exception ignored)
        {
        }

        return analysis;
    }
    private String buildCacheKey(String text)
    {
        try
        {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            text.getBytes(StandardCharsets.UTF_8)
                    );

            return CACHE_PREFIX +
                    HexFormat.of().formatHex(hash);
        }
        catch (Exception exception)
        {
            throw new IllegalStateException(
                    "Unable to create cache key",
                    exception
            );
        }
    }
}