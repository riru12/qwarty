package com.qwarty.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignupAuthRequestDTO(
        @JsonProperty(required = true) String username,
        @JsonProperty(required = true) String email,
        @JsonProperty(required = true) String password) {}
