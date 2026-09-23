package com.piyush.trustguard.service;

import com.piyush.trustguard.dto.ScamAnalysisRequest;
import com.piyush.trustguard.dto.ScamAnalysisResponse;
import com.piyush.trustguard.exception.AnalysisException;
import com.piyush.trustguard.risk.InputTypeDetector;
import com.piyush.trustguard.risk.RuleAnalysisResult;
import com.piyush.trustguard.risk.RuleBasedAnalyzer;
import com.piyush.trustguard.risk.RiskAssessmentService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Service
public class ScamAnalysisService
{
    private final RestClient restClient;
    private final String groqApiKey;
    private final ObjectMapper objectMapper;
    private final RuleBasedAnalyzer ruleBasedAnalyzer;
    private final RiskAssessmentService riskAssessmentService;
    private final InputTypeDetector inputTypeDetector;
    private final StringRedisTemplate redisTemplate;

    private static final String CACHE_PREFIX =
            "trustguard:v2:analysis:";

    private static final Duration CACHE_TTL =
            Duration.ofMinutes(15);

    public ScamAnalysisService(
            ObjectMapper objectMapper,
            RuleBasedAnalyzer ruleBasedAnalyzer,
            RiskAssessmentService riskAssessmentService,
            InputTypeDetector inputTypeDetector,
            StringRedisTemplate redisTemplate,
            @Value("${groq.api-key}") String groqApiKey)
    {
        this.restClient = RestClient.create();
        this.groqApiKey = groqApiKey;
        this.objectMapper = objectMapper;
        this.ruleBasedAnalyzer = ruleBasedAnalyzer;
        this.riskAssessmentService = riskAssessmentService;
        this.inputTypeDetector = inputTypeDetector;
        this.redisTemplate = redisTemplate;
    }

    public ScamAnalysisResponse analyze(
            ScamAnalysisRequest request)
    {
        String text = request.getText().trim();

        String cacheKey = buildCacheKey(text);

        try
        {
            String cachedResult =
                    redisTemplate.opsForValue()
                            .get(cacheKey);

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

        boolean isUrl =
                inputTypeDetector.isUrl(text);

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
                    - threatDetected
                    - confidence
                    - category
                    - redFlags
                    - explanation
                    - recommendation

                    DO NOT include any other fields.

                    Rules:
                    - confidence must be between 0 and 1.
                    - threatDetected should be true only when there is
                      meaningful evidence that the URL is malicious
                      or represents a security threat.
                    - redFlags should contain the specific suspicious
                      characteristics found.
                    - category should describe the threat type, or
                      "NONE" if no meaningful threat is detected.
                    - Possible categories include:
                      PHISHING
                      MALWARE
                      CREDENTIAL_THEFT
                      IMPERSONATION
                      SOCIAL_ENGINEERING
                      NONE
                    - Do not invent information that is not present.
                    - Do not include markdown or code fences.

                    URL to analyze:
                    %s
                    """.formatted(text);
        }
        else
        {
            prompt = """
                    You are a cybersecurity threat detection assistant.

                    Analyze the following message for signs of phishing,
                    scams, fraud, impersonation, malicious requests,
                    urgency tactics, credential theft, or suspicious
                    financial activity.

                    Return ONLY valid JSON.

                    The JSON MUST contain exactly these six fields:
                    - threatDetected
                    - confidence
                    - category
                    - redFlags
                    - explanation
                    - recommendation

                    DO NOT include any other fields.

                    Rules:
                    - confidence must be between 0 and 1.
                    - threatDetected should be true only when there is
                      meaningful evidence of a security threat.
                    - redFlags should contain the specific suspicious
                      characteristics found in the message.
                    - category should describe the threat type, or
                      "NONE" if no meaningful threat is detected.
                    - Possible categories include:
                      PHISHING
                      FINANCIAL_SCAM
                      REWARD_SCAM
                      IMPERSONATION
                      MALWARE
                      CREDENTIAL_THEFT
                      SOCIAL_ENGINEERING
                      NONE
                    - Do not invent information that is not present.
                    - Do not include markdown or code fences.

                    Message to analyze:
                    %s
                    """.formatted(text);
        }

        ScamAnalysisResponse analysis;

        try
        {
            String json =
                    callGroq(prompt);

            if (json == null || json.isBlank())
            {
                throw new AnalysisException(
                        "AI returned an empty response"
                );
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
            analysis.setRedFlags(
                    new ArrayList<>()
            );
        }

        for (String flag :
                ruleResult.getRedFlags())
        {
            if (!analysis.getRedFlags()
                    .contains(flag))
            {
                analysis.getRedFlags()
                        .add(flag);
            }
        }

        int riskScore =
                riskAssessmentService
                        .calculateRiskScore(
                                ruleResult.getScore(),
                                analysis.getConfidence(),
                                analysis.isThreatDetected()
                        );

        analysis.setRiskScore(riskScore);

        try
        {
            String result =
                    objectMapper.writeValueAsString(
                            analysis
                    );

            redisTemplate.opsForValue()
                    .set(
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
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            text.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return CACHE_PREFIX +
                    HexFormat.of()
                            .formatHex(hash);
        }
        catch (Exception exception)
        {
            throw new IllegalStateException(
                    "Unable to create cache key",
                    exception
            );
        }
    }

    private String callGroq(String prompt)
    {
        Map<String, Object> schema =
                Map.of(
                        "type",
                        "object",

                        "properties",
                        Map.of(
                                "threatDetected",
                                Map.of(
                                        "type",
                                        "boolean"
                                ),

                                "confidence",
                                Map.of(
                                        "type",
                                        "number",
                                        "minimum",
                                        0,
                                        "maximum",
                                        1
                                ),

                                "category",
                                Map.of(
                                        "type",
                                        "string"
                                ),

                                "redFlags",
                                Map.of(
                                        "type",
                                        "array",
                                        "items",
                                        Map.of(
                                                "type",
                                                "string"
                                        )
                                ),

                                "explanation",
                                Map.of(
                                        "type",
                                        "string"
                                ),

                                "recommendation",
                                Map.of(
                                        "type",
                                        "string"
                                )
                        ),

                        "required",
                        List.of(
                                "threatDetected",
                                "confidence",
                                "category",
                                "redFlags",
                                "explanation",
                                "recommendation"
                        ),

                        "additionalProperties",
                        false
                );

        Map<String, Object> jsonSchema =
                Map.of(
                        "name",
                        "threat_analysis",

                        "strict",
                        true,

                        "schema",
                        schema
                );

        Map<String, Object> request =
                Map.of(
                        "model",
                        "openai/gpt-oss-20b",

                        "messages",
                        List.of(
                                Map.of(
                                        "role",
                                        "user",
                                        "content",
                                        prompt
                                )
                        ),

                        "reasoning_effort",
                        "low",

                        "response_format",
                        Map.of(
                                "type",
                                "json_schema",

                                "json_schema",
                                jsonSchema
                        )
                );

        ResponseEntity<String> response =
                restClient
                        .post()
                        .uri(
                                "https://api.groq.com/openai/v1/chat/completions"
                        )
                        .header(
                                "Authorization",
                                "Bearer " + groqApiKey
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .body(request)
                        .retrieve()
                        .toEntity(String.class);

        try
        {
            JsonNode root =
                    objectMapper.readTree(
                            response.getBody()
                    );

            JsonNode content =
                    root.at(
                            "/choices/0/message/content"
                    );

            if (content.isMissingNode()
                    || content.isNull()
                    || content.asText().isBlank())
            {
                throw new AnalysisException(
                        "AI response did not contain analysis content"
                );
            }

            return content.asText();
        }
        catch (AnalysisException exception)
        {
            throw exception;
        }
        catch (Exception exception)
        {
            throw new AnalysisException(
                    "Unable to parse AI response",
                    exception
            );
        }
    }
}