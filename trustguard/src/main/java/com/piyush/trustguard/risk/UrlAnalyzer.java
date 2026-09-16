package com.piyush.trustguard.risk;

import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class UrlAnalyzer
{
    private final WebRiskService webRiskService;

    public UrlAnalyzer(WebRiskService webRiskService)
    {
        this.webRiskService = webRiskService;
    }

    public boolean isSuspicious(String url)
    {
        try
        {
            URI uri = URI.create(url);
            String host = uri.getHost();

            if (host == null)
            {
                return true;
            }

            if (host.matches("\\d+\\.\\d+\\.\\d+\\.\\d+"))
            {
                return true;
            }

            return webRiskService.isThreatDetected(url);
        }
        catch (Exception e)
        {
            return true;
        }
    }
}