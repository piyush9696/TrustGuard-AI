package com.piyush.trustguard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheck
{
    @GetMapping("/api/health")
    public String health()
    {
        return "TrustGuard is working";
    }
}
