package com.qwarty.game.model;

import com.qwarty.game.lov.GameMode;
import com.qwarty.game.lov.RoomState;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Room {
    private final String id;
    private final GameMode gameMode;
    private final RoomState roomState = RoomState.WAITING;
    private final Set<String> players = ConcurrentHashMap.newKeySet();

    public Room(String id, GameMode gameMode) {
        this.id = id;
        this.gameMode = gameMode;
    }

    public void addPlayer(String username) {
        players.add(username);
    }

    public boolean isFull() {
        return players.size() >= gameMode.getMaxPlayers();
    }
}
