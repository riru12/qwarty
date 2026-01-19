package com.qwarty.game.session;

import java.time.Instant;
import java.util.Deque;
import com.qwarty.game.broadcaster.GameBroadcaster;
import com.qwarty.game.dto.GameInputDTO;
import com.qwarty.game.generator.WordGenerator;
import com.qwarty.game.lov.GameStatus;
import com.qwarty.game.lov.MessageType;
import com.qwarty.game.model.GameState;
import com.qwarty.game.model.Word;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class GameSession {

    private static final int MAX_STACK_SIZE = 15;

    private final String roomId;
    private final GameBroadcaster broadcaster;
    
    private GameStatus status = GameStatus.WAITING;
    private GameState state = new GameState();

    private boolean player1Connected = false;
    private boolean player2Connected = false;

    private Instant updated = Instant.now();

    /**
     * Adds a player to GameState
     * 
     * This first checks the player1 slot and then the player2 slot. Once a player
     * has been added to the session's state, they cannot be removed any longer.
     */
    public synchronized boolean addPlayer(String username) {
        if (state.getPlayer1() != null && state.getPlayer2() != null) {
            return false;
        }

        if (state.getPlayer1() == null) {
            state.setPlayer1(username);
            player1Connected = true;
        } else if (state.getPlayer2() == null) {
            state.setPlayer2(username);
            player2Connected = true;
        }

        // after adding a player, announce to room subscribers the change in state
        broadcaster.broadcastToRoom(roomId, MessageType.GAME_STATE, state);
        return true;
    }

    /**
     * Set disconnected player connection flag to false
     */
    public synchronized void handleDisconnect(String username) {
        if (!username.equals(state.getPlayer1()) && !username.equals(state.getPlayer2())) {
            return;
        }

        boolean isPlayer1 = username.equals(state.getPlayer1());
        if (isPlayer1) {
            player1Connected = false;
        } else {
            player2Connected = false;
        }
    }

    public boolean isAbandoned() {
        return !player1Connected && !player2Connected;
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

    private void endGame(String winner) {
        status = GameStatus.FINISHED;

        broadcaster.broadcastToRoom(roomId, MessageType.GAME_STATUS, status);
    }

    private void initializeStacks() {
        state.getPlayer1Stack().clear();
        state.getPlayer2Stack().clear();

        for (int i = 0; i < 5; i++) {
            state.getPlayer1Stack().addLast(new Word(WordGenerator.randomWord(), true));
            state.getPlayer2Stack().addLast(new Word(WordGenerator.randomWord(), true));
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
        Deque<Word> playerStack = isPlayer1 ? state.getPlayer1Stack() : state.getPlayer2Stack();
        Deque<Word> opponentStack = isPlayer1 ? state.getPlayer2Stack() : state.getPlayer1Stack();

        // Verify correctness of word input
        Word activeWord = playerStack.peekFirst();
        if (!input.word().equalsIgnoreCase(activeWord.getText())) {
            return false;
        }
        
        // Transfer word to opponent stack
        Word typedWord = playerStack.removeFirst();
        if (typedWord.isFresh()) {
            typedWord.setFresh(false);
            opponentStack.addLast(typedWord);
        }

        // If playerStack size is reduced to 1, refill an extra word
        if (playerStack.size() == 1) {
            playerStack.addLast(new Word(WordGenerator.randomWord(), true));
        }

        updated = Instant.now();
        broadcaster.broadcastToRoom(roomId, MessageType.GAME_STATE, state);
        
        if (opponentStack.size() >= MAX_STACK_SIZE) {
            endGame(username);
        }
        return true;
    }

}
