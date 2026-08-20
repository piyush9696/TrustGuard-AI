package com.piyush.trustguard.risk;

import lombok.Data;

import java.util.*;

@Data
public class RuleAnalysisResult
{
    int score;
    List<String> redFlags=new ArrayList<>();
}

