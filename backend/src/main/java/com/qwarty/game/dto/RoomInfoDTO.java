package com.qwarty.game.dto;

import java.util.Map;

import com.qwarty.game.lov.GameStatus;
import com.qwarty.game.model.PlayerProgress;

public record RoomInfoDTO(GameStatus status, String textPrompt, Map<String, PlayerProgress> playerProgressMap) {

}
