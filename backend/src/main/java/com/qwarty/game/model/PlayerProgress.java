package com.qwarty.game.model;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerProgress {
    private int position;
    private Instant timestamp;
}
