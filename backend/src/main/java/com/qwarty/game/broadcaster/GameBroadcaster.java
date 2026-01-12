package com.qwarty.game.broadcaster;

import java.security.Principal;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GameBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;

    private final String GAME_DESTINATION = "/topic/room/%s";
    private final String USER_DESTINATION = "/queue/room/%s";

    public void broadcastToUser(String roomId, Principal principal) {
        String destination = String.format(USER_DESTINATION, roomId);
        
        // messagingTemplate.convertAndSendToUser(principal.getName(), destination, null);
    }

    public void broadcastToRoom(String roomId) {
        String destination = String.format(GAME_DESTINATION, roomId);

        // messagingTemplate.convertAndSend(destination, null);
    }

}
