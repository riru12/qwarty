package com.qwarty.game.session;

import org.springframework.stereotype.Component;

import com.qwarty.game.broadcaster.GameBroadcaster;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GameSessionFactory {
    
    private final GameBroadcaster broadcaster;

    public GameSession create(String roomId) {
        return new GameSession(roomId, broadcaster);
    }
}
