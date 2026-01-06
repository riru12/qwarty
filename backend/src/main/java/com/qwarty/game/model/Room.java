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
     * "Reserves" a slot for a player to make connections to the room. 
     * 
     * This is primarily used in {@link #RoomManager.createRoom} and {@link #RoomManager.joinRoom}
     * Used as a fallback in {@link #RoomWsController} when joining to reserve a slot if they already don't have one.
     * */
    public void initPlayerConnection(String sessionUid) {
        playerSessionConnections.putIfAbsent(sessionUid, 0);
    }

    /**
     * Increments the number of connections a user has to a room.
     */
    @SuppressWarnings("null")
    public void addConnection(String sessionUid) {
        if (!playerSessionConnections.containsKey(sessionUid)) {
            throw new IllegalStateException("Player does not have a reserved slot in this room.");
        }
        playerSessionConnections.merge(sessionUid, 1, Integer::sum);
    }

    /** 
     * Decrements the number of connections a user has to a room.  
     * 
     * If result connection count is 0, the user loses its reserved slot completely.
     * */
    public void removeConnection(String sessionUid) {
        playerSessionConnections.computeIfPresent(sessionUid, (id, count) -> count > 1 ? count - 1 : null);
    }

    /* Returns sessionUIDs of all the players with reserved slots in the room */
    public List<String> getPlayers() {
        return new ArrayList<>(playerSessionConnections.keySet());
    }

    /* Checks if a player has a slot in the room regardless of any active connections  */
    public boolean hasPlayer(String sessionUid) {
        return playerSessionConnections.containsKey(sessionUid);
    }

    /* Checks if a player's sessionUid has an active connection to the room  */
    public boolean isPlayerConnected(String sessionUid) {
        Integer count = playerSessionConnections.get(sessionUid);
        return count != null && count > 0;
    }

    /* Checks if the max number of reserved slots available for a room is filled */
    public boolean isFull() {
        return playerSessionConnections.size() >= gameMode.getMaxPlayers();
    }

    /**
     * Checks if all users (if any are left) in the room all have no active connections 
     * 
     * Note: this is currently an O(n) solution. It might be worth considering adding an O(1) solution in the future.
     */
    public boolean isEmpty() {
        return playerSessionConnections.values().stream().allMatch(count -> count == 0);
    }
}
