package com.qwarty.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SignupAuthRequestDTO(
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank String password) {}
