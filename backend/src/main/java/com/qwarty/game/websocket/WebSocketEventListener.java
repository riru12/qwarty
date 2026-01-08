package com.qwarty.game.websocket;

import com.qwarty.game.service.RoomService;

import java.security.Principal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    
    private final RoomService roomService;

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
        
        roomService.leaveRoom(roomId, username);
    }
}
