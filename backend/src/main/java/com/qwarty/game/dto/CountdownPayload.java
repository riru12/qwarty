package com.qwarty.game.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public final class CountdownPayload implements GamePayload {
    private final double time;
}
