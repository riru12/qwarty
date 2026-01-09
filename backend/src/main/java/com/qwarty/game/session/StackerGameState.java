package com.qwarty.game.session;

import java.time.Instant;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StackerGameState {

    private int sequence = 0;
    private final Deque<String> p1Stack = new ConcurrentLinkedDeque<>();
    private final Deque<String> p2Stack = new ConcurrentLinkedDeque<>();
    private String player1;
    private String player2;
    private Instant lastUpdate = Instant.now();
    private String lastUpdatedBy = null;

    public void incrementSequence() {
        this.sequence = this.sequence + 1;
    }

}
