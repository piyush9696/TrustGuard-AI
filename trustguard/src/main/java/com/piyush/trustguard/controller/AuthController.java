package com.piyush.trustguard.controller;

import com.piyush.trustguard.dto.LoginRequest;
import com.piyush.trustguard.dto.LoginResponse;
import com.piyush.trustguard.dto.RegisterRequest;
import com.piyush.trustguard.dto.RegisterResponse;
import com.piyush.trustguard.entity.User;
import com.piyush.trustguard.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    private final AuthService authService;
    public AuthController(AuthService authService)
    {
        this.authService=authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request)
    {
        User user=authService.register(request);
        return new RegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request)
    {
        String token= authService.login(request);
        return new LoginResponse(token);
    }
}
