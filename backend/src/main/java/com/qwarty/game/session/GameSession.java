package com.qwarty.game.session;

import java.time.Instant;
import java.util.Deque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import com.qwarty.game.broadcaster.GameBroadcaster;
import com.qwarty.game.dto.GameInputDTO;
import com.qwarty.game.generator.WordGenerator;
import com.qwarty.game.lov.GameStatus;
import com.qwarty.game.lov.MessageType;
import com.qwarty.game.model.GameState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class GameSession {

    private final String roomId;
    private final GameBroadcaster broadcaster;

    // Used for ticks (sending state updates per tick)
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> tickTask;
    
    private GameStatus status = GameStatus.WAITING;
    private GameState state = new GameState();
    private Instant updated = Instant.now();

    /**
     * Adds a player to GameState, first checking the p1 slot before the p2 slot
     */
    public synchronized boolean addPlayer(String username) {
        if (state.getPlayer1() != null && state.getPlayer2() != null) {
            return false;
        }

        if (state.getPlayer1() == null) {
            state.setPlayer1(username);
        } else if (state.getPlayer2() == null) {
            state.setPlayer2(username);
        }

        // after adding a player, announce to room subscribers the change in state
        broadcaster.broadcastToRoom(roomId, MessageType.GAME_STATE, state);
        return true;
    }

    /**
     * Attempts to start a game, fails if both player slots are NOT yet filled
     */
    public boolean startGame() {
        if (state.getPlayer1() == null || state.getPlayer2() == null) {
            return false;
        }
        status = GameStatus.IN_PROGRESS;
        initializeStacks();

        broadcaster.broadcastToRoom(roomId, MessageType.GAME_STATE, state);
        broadcaster.broadcastToRoom(roomId, MessageType.GAME_STATUS, status);

        return true;
    }

    private void initializeStacks() {
        state.getPlayer1Stack().clear();
        state.getPlayer2Stack().clear();

        for (int i = 0; i < 5; i++) {
            state.getPlayer1Stack().addLast(WordGenerator.randomWord());
            state.getPlayer2Stack().addLast(WordGenerator.randomWord());
        }
    }
    
    public synchronized boolean handleInput(String username, GameInputDTO input) {
        if (status != GameStatus.IN_PROGRESS) {
            return false;
        }
        if (input == null || input.word() == null || input.word().isBlank()) {
            return false;
        }
        if (!username.equals(state.getPlayer1()) && !username.equals(state.getPlayer2())) {
            return false;
        }
        
        // Identify player and stacks
        boolean isPlayer1 = username.equals(state.getPlayer1());
        Deque<String> playerStack = isPlayer1 ? state.getPlayer1Stack() : state.getPlayer2Stack();
        Deque<String> opponentStack = isPlayer1 ? state.getPlayer2Stack() : state.getPlayer1Stack();

        // Verify correctness of word input
        String expectedWord = playerStack.peekFirst();
        if (!input.word().equalsIgnoreCase(expectedWord)) {
            return false;
        }
        
        // Transfer word to opponent stack
        opponentStack.addLast(playerStack.removeFirst());

        // If playerStack size is reduced to 1, refill an extra word
        if (playerStack.size() == 1) {
            playerStack.addLast(WordGenerator.randomWord());
        }

        updated = Instant.now();
        broadcaster.broadcastToRoom(roomId, MessageType.GAME_STATE, state);
        return true;
    }

}
