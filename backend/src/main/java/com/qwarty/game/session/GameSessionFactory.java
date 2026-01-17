package com.qwarty.game.session;

import java.util.concurrent.ScheduledExecutorService;

import org.springframework.stereotype.Component;

import com.qwarty.game.broadcaster.GameBroadcaster;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GameSessionFactory {
    
    private final GameBroadcaster broadcaster;
    private final ScheduledExecutorService scheduler;

    public GameSession create(String roomId) {
        return new GameSession(roomId, broadcaster, scheduler);
    }
}
