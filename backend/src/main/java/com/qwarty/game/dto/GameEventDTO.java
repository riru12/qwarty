package com.qwarty.game.dto;

import java.time.Instant;

import com.qwarty.game.lov.GameMessageType;

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
public class GameEventDTO {

    private GameMessageType messageType;
    private GamePayload payload;

    @Builder.Default
    private Instant timestamp = Instant.now();
    
}
