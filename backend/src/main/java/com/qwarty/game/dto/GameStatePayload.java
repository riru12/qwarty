package com.qwarty.game.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public final class GameStatePayload implements GamePayload {
    private final String prompt;
    private final Map<String, Object> playerProgress;
}
