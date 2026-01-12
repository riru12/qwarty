package com.qwarty.game.session;

import com.qwarty.game.lov.GameStatus;
import com.qwarty.game.model.GameState;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSession {

    private GameState state = new GameState();
    private GameStatus status = GameStatus.WAITING;

    public void addPlayer(String username) {
        state.getPlayerProgress().put(username, "");
    }

}
