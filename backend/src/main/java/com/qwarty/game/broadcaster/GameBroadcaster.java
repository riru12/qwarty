package com.qwarty.game.broadcaster;

import java.security.Principal;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import com.qwarty.game.dto.WebSocketMessageDTO;
import com.qwarty.game.lov.MessageType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GameBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;

    private final String GAME_DESTINATION = "/topic/room/%s";
    private final String USER_DESTINATION = "/queue/room/%s";

    public <T> void broadcastToUser(String roomId, Principal principal, MessageType type, T payload) {
        String destination = String.format(USER_DESTINATION, roomId);
        WebSocketMessageDTO<T> message = WebSocketMessageDTO.of(type, payload);
        messagingTemplate.convertAndSendToUser(principal.getName(), destination, message);
    }

    public <T> void broadcastToRoom(String roomId, MessageType type, T payload) {
        String destination = String.format(GAME_DESTINATION, roomId);
        WebSocketMessageDTO<T> message = WebSocketMessageDTO.of(type, payload);
        messagingTemplate.convertAndSend(destination, message);
    }

}
