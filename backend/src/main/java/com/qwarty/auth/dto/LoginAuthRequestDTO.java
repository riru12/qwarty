package com.qwarty.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginAuthRequestDTO(
        @NotBlank String username, @NotBlank String password) {}
