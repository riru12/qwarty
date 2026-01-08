package com.qwarty.game.dto;

public sealed interface GamePayload permits
    CountdownPayload,
    GameStatePayload {
}
