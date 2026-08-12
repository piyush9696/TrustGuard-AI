package com.piyush.trustguard.controller;

import com.piyush.trustguard.entity.User;
import com.piyush.trustguard.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController
{
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public User createUser(@RequestBody User user)
    {
        return userRepository.save(user);
    }

    @GetMapping
    public List<User> getUsers()
    {
        return userRepository.findAll();
    }
}
