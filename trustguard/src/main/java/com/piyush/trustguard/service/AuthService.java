package com.piyush.trustguard.service;

import com.piyush.trustguard.dto.LoginRequest;
import com.piyush.trustguard.dto.RegisterRequest;
import com.piyush.trustguard.entity.User;
import com.piyush.trustguard.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService)
    {
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtService=jwtService;
    }
    public User register(RegisterRequest request)
    {
        if(userRepository.existsByEmail(request.getEmail()))
        {
            throw new RuntimeException("Email Already exists");
        }

        User user=new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }
    public String login(LoginRequest request)
    {
        User user=userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(()->
                        new RuntimeException("Invalid email or password")
                );
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword()))
        {
            throw new RuntimeException("Invalid email or password");
        }
        return jwtService.generateToken(user.getEmail());
    }
}
