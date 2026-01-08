package com.qwarty.game.model;

import com.qwarty.game.lov.GameMode;
// import com.qwarty.game.session.GameSession;
import com.qwarty.game.session.GameSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Room {
    private final String id;
    private final GameMode gameMode;
    private final Map<String, Integer> playerConnections = new ConcurrentHashMap<>();
    private final Semaphore slots;

    private GameSession gameSession;

    public Room(String id, GameMode gameMode) {
        this.id = id;
        this.gameMode = gameMode;
        this.slots = new Semaphore(gameMode.getMaxPlayers());
    }

    /**
     * Atomically tries to add a connection to the room.
     * Returns true if successful, false if room is full.
     */
    @SuppressWarnings("null")
    public boolean addConnection(String username) {
        // Existing player → just increment
        if (playerConnections.containsKey(username)) {
            playerConnections.merge(username, 1, Integer::sum);
            return true;
        }
        
        // New player → try to acquire slot
        if (!slots.tryAcquire()) {
            return false; // Room is full
        }
        
        Integer prev = playerConnections.putIfAbsent(username, 1);
        if (prev != null) {
            // Race: someone else added this player first
            slots.release(); // Give back the slot we acquired
            playerConnections.merge(username, 1, Integer::sum);
        }
        
        return true;
    }

   /**
     * Decrements the number of connections a user has to a room.
     * If connection count reaches 0, the user loses their reserved slot completely.
     */
    public void removeConnection(String username) {
        playerConnections.computeIfPresent(username, (k, v) -> {
            if (v == 1) {
                slots.release(); // Release slot when last connection is removed
                return null; // Remove entry from map
            }
            return v - 1;
        });
    }

    /* Returns usernames of all the players in the room */
    public List<String> getPlayers() {
        return new ArrayList<>(playerConnections.keySet());
    }

    /* Checks if a player has any connections to the room  */
    public boolean hasPlayer(String username) {
        return playerConnections.containsKey(username);
    }

    /* Checks if the max number of reserved slots available for a room is filled */
    public boolean isFull() {
        return playerConnections.size() >= gameMode.getMaxPlayers();
    }

    /**
     * Checks if all users (if any are left) in the room all have no active connections
     */
    public boolean isEmpty() {
        return playerConnections.isEmpty();
    }
}
