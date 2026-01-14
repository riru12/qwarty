package com.qwarty.game.dto;

import com.qwarty.game.lov.GameStatus;
import com.qwarty.game.model.GameState;

public record RoomInfoDTO(GameStatus status, GameState state) {

}
