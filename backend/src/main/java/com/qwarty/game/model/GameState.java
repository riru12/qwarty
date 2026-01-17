package com.qwarty.game.model;

import java.util.ArrayDeque;
import java.util.Deque;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameState {
    private String player1;
    private String player2;
    private Deque<String> player1Stack = new ArrayDeque<>();
    private Deque<String> player2Stack = new ArrayDeque<>();
}
