package com.piyush.trustguard.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.piyush.trustguard.dto.ScamAnalysisRequest;
import com.piyush.trustguard.dto.ScamAnalysisResponse;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class ScamAnalysisService {

    private final Client geminiClient;
    private final ObjectMapper objectMapper;

    public ScamAnalysisService(Client geminiClient,
                               ObjectMapper objectMapper) {
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }

    public ScamAnalysisResponse analyze(ScamAnalysisRequest request)
            throws Exception {

        String prompt = """
                You are a cybersecurity scam detection assistant.

                Analyze the following message for signs of scams,
                phishing, fraud, impersonation, malicious requests,
                urgency tactics, or suspicious financial activity.

                Return ONLY valid JSON.
                
                The JSON MUST contain exactly these six fields:
                - isScam
                - confidence
                - category
                - redFlags
                - explanation
                - recommendation
                
                DO NOT include any other fields.
                In particular, DO NOT include a field named "scam".

                Rules:
                - confidence must be between 0 and 1.
                - isScam should be true only when there is meaningful
                  evidence of scam/fraudulent behavior.
                - redFlags should contain the specific suspicious
                  characteristics found in the message.
                - category should describe the scam type, or
                  "NONE" if it does not appear to be a scam.
                - Do not invent information that is not present.
                - Do not include markdown or code fences.
                
                Message to analyze:
                %s
                """.formatted(request.getText());

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

        return objectMapper.readValue(
                json,
                ScamAnalysisResponse.class
        );
    }
}
