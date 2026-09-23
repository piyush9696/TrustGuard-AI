package com.piyush.trustguard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class ScamAnalysisResponse
{
    private boolean threatDetected;
    private double confidence;
    private String category;
    private List<String> redFlags;
    private String explanation;
    private String recommendation;
    private int riskScore;
}
