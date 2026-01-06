package com.qwarty.game.controller;

import com.qwarty.game.dto.PlayerListEventDTO;
import com.qwarty.game.lov.MessageType;
import com.qwarty.game.model.Room;
import com.qwarty.game.service.RoomManager;
import java.util.ArrayList;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RoomWsController {

    private final RoomManager roomManager;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/room.join/{roomId}")
    public void joinRoom(@DestinationVariable String roomId, StompHeaderAccessor accessor) {

        accessor.getSessionAttributes().put("ROOM_ID", roomId); // upon joining a room, save the roomId into the WS session's attributes
        String sessionUid = (String) accessor.getSessionAttributes().get("SESSION_UID");

        Room room = roomManager.getRoom(roomId);
        if (!room.hasPlayer(sessionUid)) {
            if (room.isFull()) {
                sendError(sessionUid, "ROOM_FULL", "Room is full");
                return;
            }
            room.addPlayer(sessionUid);
        }

        PlayerListEventDTO event = PlayerListEventDTO.builder()
                .roomId(roomId)
                .players(new ArrayList<>(room.getPlayers()))
                .messageType(MessageType.JOIN)
                .build();

        messagingTemplate.convertAndSend("/topic/room/" + roomId, event);
    }

    @MessageMapping("/room.leave/{roomId}")
    public void leaveRoom(@DestinationVariable String roomId, StompHeaderAccessor accessor) {
        String sessionUid = (String) accessor.getSessionAttributes().get("SESSION_UID");

        Room room = roomManager.getRoom(roomId);
        if (room.hasPlayer(sessionUid)) {
            room.removePlayer(sessionUid);
        }

        if (room.isEmpty()) {
            roomManager.removeRoom(roomId);
        } else {
            PlayerListEventDTO eventDTO = PlayerListEventDTO.builder()
                    .roomId(roomId)
                    .players(new ArrayList<>(room.getPlayers()))
                    .messageType(MessageType.LEAVE)
                    .build();
            messagingTemplate.convertAndSend("/topic/room/" + roomId, eventDTO);
        }
    }

    private void sendError(String sessionUid, String code, String message) {
        Map<String, Object> error = Map.of(
                "type", "ERROR",
                "code", code,
                "message", message);
        messagingTemplate.convertAndSendToUser(sessionUid, "/queue/errors", error);
    }
}
