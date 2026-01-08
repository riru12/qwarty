package com.qwarty.game.session;

import java.security.Principal;

import com.qwarty.game.lov.GameStatus;
import com.qwarty.game.model.Room;

public interface GameSession {
    /**
     * Checks if the game can already start
     */
    boolean canStart();

    /**
     * Notifies a joining user during the `COUNTDOWN` phase the remaining time left
     */
    void broadcastUserOngoingCountdown(Principal principal);

    /**
     * Notifies all users in the room the beginning of a game's `COUNTDOWN` phase and schedules the game's formal start
     */
    void broadcastAllAndStartCountdown();

    /**
     * Start the game session
     */
    void start();

    /**
     * End the game session, cleans up resources
     */
    void end();

    /**
     * Retrieves the game's current status
     */
    GameStatus getStatus();

    /**
     * Retrieves the game's state
     */
    Object getState();

    /**
     * Retrieve the room owner of this session
     */
    Room getRoom();

    /**
     * Handles a player's action in the game
     * Returns true if action is successfully handled
     */
    boolean handleAction(String username, Object actionData);

    void addPlayer(String username);
}