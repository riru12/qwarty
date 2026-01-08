package com.qwarty.game.websocket;

import com.qwarty.game.dto.PlayerListEventDTO;
import com.qwarty.game.dto.RoomDetailsDTO;
import com.qwarty.game.lov.RoomMessageType;
import com.qwarty.game.service.RoomManager;

import java.security.Principal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final RoomManager roomManager;
    private final SimpMessagingTemplate messagingTemplate;

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

        RoomDetailsDTO roomDetailsDto = roomManager.leaveRoom(roomId, username);

        if (roomDetailsDto == null) {
            return;
        }
        PlayerListEventDTO eventDTO = PlayerListEventDTO.builder()
                .roomId(roomDetailsDto.roomId())
                .players(roomDetailsDto.players())
                .messageType(RoomMessageType.LEAVE)
                .build();
        messagingTemplate.convertAndSend("/topic/room/" + roomId, eventDTO);
    }
}
