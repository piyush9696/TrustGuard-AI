package com.piyush.trustguard.security;

import com.piyush.trustguard.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService)
    {
        this.jwtService=jwtService;
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/auth/");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException
    {
        String authHeader= request.getHeader("Authorization");
        if(authHeader==null||!authHeader.startsWith("Bearer "))
        {
            filterChain.doFilter(request,response);
            return;
        }
        String token=authHeader.substring(7);
        String email=jwtService.extractEmail(token);
        if(email!=null&& SecurityContextHolder.getContext()
                .getAuthentication()==null)
        {
            if (jwtService.isTokenValid(token, email))
            {
                var authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request,response);
    }
}
