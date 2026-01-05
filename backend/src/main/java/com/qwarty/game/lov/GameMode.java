package com.qwarty.game.lov;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum GameMode {
    RACER("RACER", 5),
    CLASSIC("CLASSIC", 2),
    ZEN("ZEN", 1);

    private final String mode;
    private final int maxPlayers;

    GameMode(String mode, int maxPlayers) {
        this.mode = mode;
        this.maxPlayers = maxPlayers;
    }

    public static GameMode from(String value) {
        return Arrays.stream(values())
                .filter(m -> m.mode.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid game mode: " + value));
    }
}
