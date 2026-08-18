package com.piyush.trustguard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ScamAnalysisRequest
{
    @NotBlank(message = "Test cannot be empty")
    @Size(max=5000, message = "Text cannot exceed 5000 characters")
    private String text;
}
