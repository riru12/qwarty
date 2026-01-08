package com.qwarty.game.session;

import java.security.Principal;
import java.time.Instant;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.qwarty.game.dto.CountdownPayload;
import com.qwarty.game.dto.GameEventDTO;
import com.qwarty.game.dto.GameStatePayload;
import com.qwarty.game.lov.GameMessageType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GameBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;
    private final String GAME_DESTINATION = "/topic/room/%s/game";
    private final String USER_DESTINATION = "/queue/game/%s";

    public void broadcastGameStart(String roomId) {
        String destination = String.format(GAME_DESTINATION, roomId);

        GameEventDTO event = GameEventDTO.builder().messageType(GameMessageType.START).timestamp(Instant.now()).build();

        messagingTemplate.convertAndSend(destination, event);
    }

    public void broadcastGlobalCountdown(String roomId, CountdownPayload payload) {
        String destination = String.format(GAME_DESTINATION, roomId);

        GameEventDTO event = GameEventDTO.builder()
            .messageType(GameMessageType.COUNTDOWN)
            .payload(payload)
            .build();

        messagingTemplate.convertAndSend(destination, event);
    }

    public void broadcastUserCountdown(String roomId, Principal principal, CountdownPayload payload) {
        String destination = String.format(USER_DESTINATION, roomId);

        GameEventDTO event = GameEventDTO.builder()
            .messageType(GameMessageType.COUNTDOWN)
            .payload(payload)
            .build();

        messagingTemplate.convertAndSendToUser(principal.getName(), destination, event);
    }

    public void broadcastGlobalGameState(String roomId, GameStatePayload payload) {
        String destination = String.format(GAME_DESTINATION, roomId);

        GameEventDTO event = GameEventDTO.builder()
            .messageType(GameMessageType.STATE)
            .payload(payload)
            .build();
        
        messagingTemplate.convertAndSend(destination, event);
    }

}
