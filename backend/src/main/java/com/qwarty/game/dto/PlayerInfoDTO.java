package com.qwarty.game.dto;

import com.qwarty.auth.lov.UserType;

public record PlayerInfoDTO(String username, UserType userType) {}
