package com.qwarty.game.dto;

import com.qwarty.game.lov.GameMode;
import java.util.Collection;

public record RoomDetailsDTO(String roomId, Collection<PlayerInfoDTO> players, GameMode gameMode) {}
