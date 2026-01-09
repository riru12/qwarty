package com.qwarty.game.dto;

import com.qwarty.game.session.StackerGameState;

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
public class GameStateEventDTO extends GameRoomEventDTO {
    private StackerGameState stackerGameState;
}
