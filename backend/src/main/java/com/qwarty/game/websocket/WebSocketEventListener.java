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
        String sessionUid = (String) event.getMessage()
                .getHeaders()
                .get("simpSessionAttributes", Map.class)
                .get("SESSION_UID");

        if (sessionUid == null) return;

        for (Room room : roomManager.getAllRooms()) {
            if (room.hasPlayer(sessionUid)) {
                room.removePlayer(sessionUid);

                if (room.isEmpty()) {
                    roomManager.removeRoom(room.getId());
                    continue;
                }

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
