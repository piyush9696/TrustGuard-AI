package com.piyush.trustguard.risk;

import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class InputTypeDetector
{
    public boolean isUrl(String text)
    {
        try
        {
            URI uri = URI.create(text.trim());

            return ("http".equalsIgnoreCase(uri.getScheme())
                    || "https".equalsIgnoreCase(uri.getScheme()))
                    && uri.getHost() != null;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}