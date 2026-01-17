package com.qwarty.game.model;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.qwarty.game.session.GameSession;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class GameRoom {

    private static final int MAX_PLAYERS = 2;

    private final String roomId;
    private final Set<String> players = new HashSet<>();
    private GameSession session;
    private Instant created = Instant.now();
    
    public synchronized boolean addPlayer(String playerId) {
        if (players.size() >= MAX_PLAYERS) {
            return false;
        }
        return players.add(playerId);
    }

    public synchronized void removePlayer(String playerId) {
        players.remove(playerId);
    }

    public synchronized boolean isFull() {
        return players.size() == MAX_PLAYERS;
    }

    public synchronized boolean isEmpty() {
        return players.isEmpty();
    }

    public synchronized Set<String> getPlayers() {
        return Collections.unmodifiableSet(new HashSet<>(players));
    }

}
