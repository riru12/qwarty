package com.qwarty.game.dto;

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
public class GameRoomEventDTO {
    private String sender;
    private String roomId;
    private MessageType messageType;
}
