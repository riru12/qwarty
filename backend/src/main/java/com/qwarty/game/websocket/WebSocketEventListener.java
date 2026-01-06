package com.qwarty.game.websocket;

import com.qwarty.game.dto.PlayerListEventDTO;
import com.qwarty.game.lov.MessageType;
import com.qwarty.game.model.Room;
import com.qwarty.game.service.RoomManager;
import java.util.ArrayList;
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
        Map<String, Object> attributes = (Map<String, Object>) event.getMessage()
                .getHeaders()
                .get("simpSessionAttributes");

        if (attributes == null) return;
        
        String sessionUid = (String) attributes.get("SESSION_UID");
        String roomId = (String) attributes.get("ROOM_ID");

        if (sessionUid == null || roomId == null) return;

        Room room = roomManager.getRoom(roomId);
        if (room != null && room.hasPlayer(sessionUid)) {
            room.removeConnection(sessionUid);

            if (room.isEmpty()) {
                roomManager.removeRoom(room.getId());
            } else {
                PlayerListEventDTO eventDTO = PlayerListEventDTO.builder()
                        .roomId(room.getId())
                        .players(new ArrayList<>(room.getPlayers()))
                        .messageType(MessageType.LEAVE)
                        .build();
                messagingTemplate.convertAndSend("/topic/room/" + room.getId(), eventDTO);
            }
        }
    }
}
