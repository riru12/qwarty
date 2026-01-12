package com.qwarty.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank String username, @NotBlank String password) {}
