package com.qwarty.game.lov;

public enum RoomState {
    WAITING, // Waiting for players to join
    READY, // Room is full
    STARTING, // Game is starting
    IN_PROGRESS, // Game is running
    FINISHED, // Game has finished
    REMATCH, // Players deciding on rematch
    ABANDONED, // Players left mid-game
    CLOSED // Room is closed/disbanded
}
