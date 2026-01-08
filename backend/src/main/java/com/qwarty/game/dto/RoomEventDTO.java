package com.qwarty.game.dto;

import java.time.Instant;

import com.qwarty.game.lov.GameMode;
import com.qwarty.game.lov.RoomMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RoomEventDTO {
    private String sender;
    private String roomId;
    private GameMode gameMode;
    private RoomMessageType messageType;
    private Instant timestamp;
}
