package com.piyush.trustguard.risk;

import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

@Component
public class RuleBasedAnalyzer
{
    private final UrlAnalyzer urlAnalyzer;
    public RuleBasedAnalyzer(UrlAnalyzer urlAnalyzer)
    {
        this.urlAnalyzer=urlAnalyzer;
    }
    public RuleAnalysisResult analyze(String text)
    {
        int score=0;
        List<String> redFlags=new ArrayList<>();
        String message=text.toLowerCase();
        if (message.contains("urgent")
                || message.contains("immediately")
                || message.contains("act now")) {

            score += 20;
            redFlags.add("Urgency language");
        }

        if (message.contains("account blocked")
                || message.contains("account will be blocked")
                || message.contains("account suspended")
                || message.contains("account will be closed")) {

            score += 25;
            redFlags.add("Account threat");
        }

        if (message.contains("password")
                || message.contains("otp")
                || message.contains("pin")
                || message.contains("verify your account")
                || message.contains("login details")) {

            score += 30;
            redFlags.add("Credential or sensitive information request");
        }

        Pattern pattern=Pattern.compile("https?://\\S+");
        Matcher matcher=pattern.matcher(text);
        while (matcher.find())
        {
            String url = matcher.group();

            if (urlAnalyzer.isSuspicious(url))
            {
                score += 20;
                redFlags.add("Suspicious URL");
            }
        }

        RuleAnalysisResult result=new RuleAnalysisResult();
        result.setScore(score);
        result.setRedFlags(redFlags);
        return result;
    }
}
