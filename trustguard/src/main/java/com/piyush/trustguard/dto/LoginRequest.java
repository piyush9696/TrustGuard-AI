package com.piyush.trustguard.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest
{
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid Email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min=8, message = "Password must have at least 8 characters")
    private String password;
}
