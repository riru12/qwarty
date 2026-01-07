package com.qwarty.game.dto;

import com.qwarty.game.lov.GameMode;
import com.qwarty.game.lov.MessageType;
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
    private MessageType messageType;
}
