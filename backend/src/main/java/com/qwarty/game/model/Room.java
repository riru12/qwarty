package com.qwarty.game.model;

import com.qwarty.game.lov.GameMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Room {
    private final String id;
    private final GameMode gameMode;
    private final Map<String, Integer> playerSessionConnections = new ConcurrentHashMap<>();

    public Room(String id, GameMode gameMode) {
        this.id = id;
        this.gameMode = gameMode;
    }

    /**
     * Increments the number of connections a user has to a room by 1.
     * 
     * If this is the player's first connection to the room, sets it to 1.
     */
    @SuppressWarnings("null")
    public void addConnection(String sessionUid) {
        playerSessionConnections.merge(sessionUid, 1, Integer::sum);
    }

    /** 
     * Decrements the number of connections a user has to a room by 1.  
     * 
     * If result connection count is 0, the user loses its entry in the hash map.
     * */
    public void removeConnection(String sessionUid) {
        playerSessionConnections.computeIfPresent(sessionUid, (id, count) -> count > 1 ? count - 1 : null);
    }

    /* Returns sessionUIDs of all the players in the room */
    public List<String> getPlayers() {
        return new ArrayList<>(playerSessionConnections.keySet());
    }

    /* Checks if a player has any connections to the room  */
    public boolean hasPlayer(String sessionUid) {
        return playerSessionConnections.containsKey(sessionUid);
    }

    /* Checks if the max number of reserved slots available for a room is filled */
    public boolean isFull() {
        return playerSessionConnections.size() >= gameMode.getMaxPlayers();
    }

    /**
     * Checks if all users (if any are left) in the room all have no active connections 
     */
    public boolean isEmpty() {
        return playerSessionConnections.isEmpty();
    }
}
