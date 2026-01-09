package com.qwarty.game.session;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.qwarty.game.dto.GameStateEventDTO;
import com.qwarty.game.lov.GameStatus;
import com.qwarty.game.lov.MessageType;

public class StackerGameRunner {

    private final String server = "SERVER";

    private final String roomId;
    private final StackerGameSession session;
    private final SimpMessagingTemplate messagingTemplate;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public StackerGameRunner(
            String roomId,
            StackerGameSession session,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.roomId = roomId;
        this.session = session;
        this.messagingTemplate = messagingTemplate;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(
            this::tick,
            0,
            100,
            TimeUnit.MILLISECONDS   // 10 ticks/sec
        );
    }

    private void tick() {
        if (GameStatus.FINISHED.equals(session.getStatus())) {
            stop();
        }

        StackerGameState state = session.retrieveGameState();

        GameStateEventDTO event = GameStateEventDTO.builder()
            .sender(server)
            .roomId(roomId)
            .messageType(MessageType.GAME_STATE)
            .stackerGameState(state)
            .build();

        messagingTemplate.convertAndSend(
            "/topic/room/" + roomId,
            event
        );
    }

    public void stop() {
        scheduler.shutdownNow();
    }

}
