package com.qwarty.game.dto;

import com.qwarty.game.lov.GameMode;
import java.util.List;

public record RoomDetailsDTO(String roomId, List<String> players, GameMode gameMode) {}
