package com.piyush.trustguard.controller;

import com.piyush.trustguard.dto.ScamAnalysisRequest;
import com.piyush.trustguard.dto.ScamAnalysisResponse;
import com.piyush.trustguard.service.ScamAnalysisService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/analyze")
public class ScamAnalysisController
{
    private final ScamAnalysisService scamAnalysisService;
    public ScamAnalysisController(ScamAnalysisService scamAnalysisService)
    {
        this.scamAnalysisService=scamAnalysisService;
    }

    @PostMapping
    public ScamAnalysisResponse analyze(
            @Valid @RequestBody ScamAnalysisRequest request)
        throws Exception
    {
        return scamAnalysisService.analyze(request);
    }
}
