package com.piyush.trustguard.risk;

import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class UrlAnalyzer
{
    public boolean isSuspicious(String url)
    {
        try
        {
            URI uri=URI.create(url);
            String host=uri.getHost();
            if(host==null)
            {
                return true;
            }
            if(host.matches("\\d+\\.\\d+\\.\\d+\\.\\d+"))
            {
                return true;
            }
            return false;
        }
        catch (Exception e)
        {
            return true;
        }
    }
}
