package com.qwarty.game.session;

import java.util.concurrent.ScheduledExecutorService;

import org.springframework.stereotype.Component;

import com.qwarty.game.lov.GameMode;
import com.qwarty.game.model.Room;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GameSessionFactory {

    private final GameBroadcaster gameBroadcaster;
    private final ScheduledExecutorService gameScheduler;

    public GameSession createSession(GameMode gameMode, Room room) {
        return switch (gameMode) {
            case RACER -> new RacerGameSession(room, gameBroadcaster, gameScheduler);
            default -> throw new IllegalArgumentException("Unknown game mode: " + gameMode);
        };
    }

}
