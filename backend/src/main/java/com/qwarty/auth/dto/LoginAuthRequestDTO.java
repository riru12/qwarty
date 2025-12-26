package com.qwarty.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginAuthRequestDTO(
        @JsonProperty(required = true) String username,
        @JsonProperty(required = true) String password) {}
