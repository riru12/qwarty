package com.qwarty.game.model;

import java.time.Instant;
import com.qwarty.game.session.GameSession;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class GameRoom {
    private final String roomId;
    private GameSession session;
    private Instant created = Instant.now();
}
