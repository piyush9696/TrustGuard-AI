package com.piyush.trustguard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class ScamAnalysisResponse
{
    @JsonProperty("scam")
    private boolean scam;
    private double confidence;
    private String category;
    private List<String> redFlags;
    private String explanation;
    private String recommendation;
    private int riskScore;
}
