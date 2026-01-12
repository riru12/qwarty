package com.qwarty.game.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class GameState {

    private final String textPrompt = "A quick brown fox jumps over the lazy dog. Shoogabaloogadonkers.";
    private final Map<String, String> playerProgress = new HashMap<>();
    
}
