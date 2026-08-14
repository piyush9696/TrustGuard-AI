package com.piyush.trustguard.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest
{
    @NotNull(message = "Username is required")
    private String username;

    @NotNull(message = "Email is required")
    @Email(message = "Enter a valid Email")
    private String email;

    @NotNull(message = "Password is required")
    @Size(min=8 , message = "Password must contain at least 8 characters")
    private String password;
}
