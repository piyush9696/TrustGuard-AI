package com.piyush.trustguard.risk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WebRiskService
{
    private final RestClient restClient;
    private final String apiKey;

    public WebRiskService(
            @Value("${webrisk.api-key}") String apiKey)
    {
        this.restClient = RestClient.create();
        this.apiKey = apiKey;
    }

    public boolean isThreatDetected(String url)
    {
        String requestUrl =
                UriComponentsBuilder
                        .fromUriString(
                                "https://webrisk.googleapis.com/v1/uris:search"
                        )
                        .queryParam("threatTypes", "MALWARE")
                        .queryParam("threatTypes", "SOCIAL_ENGINEERING")
                        .queryParam("uri", url)
                        .queryParam("key", apiKey)
                        .toUriString();

        ResponseEntity<String> response =
                restClient.get()
                        .uri(requestUrl)
                        .retrieve()
                        .toEntity(String.class);

        return response.getBody() != null
                && response.getBody().contains("\"threat\"");
    }
}