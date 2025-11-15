package com.amool.adapters.in.rest.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank String surname,
        @NotBlank String username,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String confirmPassword
) {}
