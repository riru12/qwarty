package com.qwarty.auth.dto;

import com.qwarty.auth.lov.UserType;

public record IdentityResponseDTO(String username, UserType userType) {}
