package com.qwarty.game.session;

import java.util.Deque;
import java.util.List;

import com.qwarty.game.lov.GameStatus;

import lombok.Getter;

@Getter
public class StackerGameSession {

    private GameStatus status = GameStatus.IN_PROGRESS;
    private final StackerGameState stackerGameState;
    private final String player1;
    private final String player2;

    public StackerGameSession(List<String> players) {
        this.stackerGameState = new StackerGameState();
        
        this.player1 = players.get(0);
        this.player2 = players.get(1);

        this.stackerGameState.setPlayer1(player1);
        this.stackerGameState.setPlayer2(player2);

        this.stackerGameState.getP1Stack().addAll(List.of("cat", "dog", "fish"));
        this.stackerGameState.getP2Stack().addAll(List.of("cat", "dog", "fish"));
    }

    public StackerGameState retrieveGameState() {
        this.stackerGameState.incrementSequence();
        return this.stackerGameState;
    }

    public boolean processWord(String username, String typedWord) {
        Deque<String> playerStack = getPlayerStack(username);
        Deque<String> opponentStack = getOpponentStack(username);

        String targetWord = playerStack.peekFirst();

        if (targetWord == null || !targetWord.equalsIgnoreCase(typedWord)) {
            return false;
        }
        playerStack.pollFirst();
        opponentStack.addLast(targetWord);

        this.stackerGameState.incrementSequence();
        this.stackerGameState.setLastUpdatedBy(username);

        return true;
    }

    private Deque<String> getPlayerStack(String username) {
        return (player1.equals(username) ? this.stackerGameState.getP1Stack() : this.stackerGameState.getP2Stack());
    }

    private Deque<String> getOpponentStack(String username) {
        return (player1.equals(username) ? this.stackerGameState.getP2Stack() : this.stackerGameState.getP1Stack());
    }

}
