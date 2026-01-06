package com.qwarty.game.model;

import com.qwarty.game.lov.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameEvent {

    private String content;
    private String sender;
    private String roomId;
    private MessageType messageType;
}
