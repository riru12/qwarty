package com.qwarty.game.model;

import com.qwarty.game.lov.GameMode;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Room {
    private final String id;
    private final GameMode gameMode;
    private final Set<String> players = ConcurrentHashMap.newKeySet();

    public Room(String id, GameMode gameMode) {
        this.id = id;
        this.gameMode = gameMode;
    }

    public void addPlayer(String sessionUid) {
        players.add(sessionUid);
    }

    public void removePlayer(String sessionUid) {
        players.remove(sessionUid);
    }

    public boolean hasPlayer(String sessionUid) {
        return players.contains(sessionUid);
    }

    public boolean isFull() {
        return players.size() >= gameMode.getMaxPlayers();
    }
}
