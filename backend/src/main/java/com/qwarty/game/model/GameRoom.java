package com.qwarty.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import com.qwarty.game.session.StackerGameRunner;
import com.qwarty.game.session.StackerGameSession;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameRoom {
    private final int SLOTS = 2;

    private final String id;
    private final Map<String, Integer> playerConnections = new ConcurrentHashMap<>();
    private final Semaphore slots;
    private StackerGameSession stackerGameSession;
    private StackerGameRunner stackerGameRunner;

    public GameRoom(String id) {
        this.id = id;
        this.slots = new Semaphore(SLOTS);
    }

    /**
     * Atomically tries to add a connection to the room.
     * Returns true if successful, false if room is full.
     */
    @SuppressWarnings("null")
    public boolean addConnection(String username) {
        // existing player -> increment connection count
        if (playerConnections.containsKey(username)) {
            playerConnections.merge(username, 1, Integer::sum);
            return true;
        }
        
        // new player -> try to acquire slot
        if (!slots.tryAcquire()) {
            return false; // room is full
        }
        
        Integer prev = playerConnections.putIfAbsent(username, 1);
        if (prev != null) {
            slots.release();
            playerConnections.merge(username, 1, Integer::sum);
        }
        
        return true;
    }

   /**
     * Decrements the number of connections a user has to a room.
     * If connection count reaches 0, the user loses their slot completely.
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
        return playerConnections.size() >= SLOTS;
    }

    /**
     * Checks if all users (if any are left) in the room all have no active connections
     */
    public boolean isEmpty() {
        return playerConnections.isEmpty();
    }
}
