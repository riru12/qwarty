package com.qwarty.game.listener;

import java.security.Principal;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.qwarty.game.service.GameRoomService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final GameRoomService gameRoomService;

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        @SuppressWarnings("unchecked")
        Map<String, Object> attributes =
                (Map<String, Object>) event.getMessage().getHeaders().get("simpSessionAttributes");

        if (attributes == null) return;

        Principal principal = event.getUser();
        String username = principal.getName();
        String roomId = (String) attributes.get("ROOM_ID");

        if (username == null || roomId == null) return;

        gameRoomService.leaveRoom(roomId, username);
    }

}
