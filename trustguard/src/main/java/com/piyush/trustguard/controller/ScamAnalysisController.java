package com.piyush.trustguard.controller;

import com.piyush.trustguard.dto.ScamAnalysisRequest;
import com.piyush.trustguard.dto.ScamAnalysisResponse;
import com.piyush.trustguard.service.ScamAnalysisService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.piyush.trustguard.service.RateLimitService;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("api/analyze")
public class ScamAnalysisController
{
    private final ScamAnalysisService scamAnalysisService;
    private final RateLimitService rateLimitService;
    public ScamAnalysisController(
            ScamAnalysisService scamAnalysisService,
            RateLimitService rateLimitService)
    {
        this.scamAnalysisService = scamAnalysisService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping
    public ScamAnalysisResponse analyze(
            @Valid @RequestBody ScamAnalysisRequest request,
            Authentication authentication)
    {
        rateLimitService.checkRateLimit(authentication.getName());

        return scamAnalysisService.analyze(request);
    }
}
