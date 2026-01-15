package com.qwarty.game.session;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.qwarty.game.dto.WebSocketInputDTO;
import com.qwarty.game.lov.GameStatus;
import com.qwarty.game.model.PlayerProgress;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSession {

    private GameStatus status = GameStatus.WAITING;
    private final String textPrompt = "A quick brown fox jumps over the lazy dog. Shoogabaloogadonkers.";
    private final Map<String, PlayerProgress> playerProgressMap = new HashMap<>();

    public void addPlayer(String username) {
        PlayerProgress progress = new PlayerProgress();
        progress.setPosition(0);
        progress.setTimestamp(Instant.now());

        playerProgressMap.put(username, progress);
    }

    public void handleInput(String username, WebSocketInputDTO input) {
        if (status != GameStatus.IN_PROGRESS) {
            return;
        }

        if (!playerProgressMap.containsKey(username)) {
            return;
        }

        PlayerProgress progress = playerProgressMap.get(username);
        progress.setPosition(input.position());
        progress.setTimestamp(Instant.now());
    }

}
