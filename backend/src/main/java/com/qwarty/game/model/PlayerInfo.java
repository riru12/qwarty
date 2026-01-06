package com.qwarty.game.model;

import com.qwarty.auth.lov.UserType;

public record PlayerInfo(String sessionUid,
        String username,
        UserType userType) {

}
